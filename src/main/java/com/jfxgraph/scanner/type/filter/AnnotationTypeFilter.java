/**
 * Copyright (c) 2012-2013,Epic-HUST Technology(Wuhan)Co.,Ltd. All Rights Reserved.
 */
package com.jfxgraph.scanner.type.filter;

import java.lang.annotation.Annotation;
import java.lang.annotation.Inherited;

import com.jfxgraph.scanner.reader.MetadataReader;
import com.jfxgraph.scanner.type.AnnotationMetadata;

/**
 * AnnotationTypeFilter
 * @author Albert
 * @since 1.0
 */
public class AnnotationTypeFilter extends AbstractTypeHierarchyTraversingFilter
{
    private final Class<? extends Annotation> annotationType;

    private final boolean considerMetaAnnotations;

    /**
     * Create a new AnnotationTypeFilter for the given annotation type. This filter will also match
     * meta-annotations. To disable the meta-annotation matching, use the constructor that accepts a
     * '{@code considerMetaAnnotations}' argument. The filter will not match interfaces.
     * 
     * @param annotationType
     *            the annotation type to match
     */
    public AnnotationTypeFilter(Class<? extends Annotation> annotationType)
    {
        this(annotationType, true);
    }

    /**
     * Create a new AnnotationTypeFilter for the given annotation type. The filter will not match
     * interfaces.
     * 
     * @param annotationType
     *            the annotation type to match
     * @param considerMetaAnnotations
     *            whether to also match on meta-annotations
     */
    public AnnotationTypeFilter(Class<? extends Annotation> annotationType, boolean considerMetaAnnotations)
    {
        this(annotationType, considerMetaAnnotations, false);
    }

    /**
     * Create a new {@link AnnotationTypeFilter} for the given annotation type.
     * 
     * @param annotationType
     *            the annotation type to match
     * @param considerMetaAnnotations
     *            whether to also match on meta-annotations
     * @param considerInterfaces
     *            whether to also match interfaces
     */
    public AnnotationTypeFilter(Class<? extends Annotation> annotationType, boolean considerMetaAnnotations, boolean considerInterfaces)
    {
        super(annotationType.isAnnotationPresent(Inherited.class), considerInterfaces);
        this.annotationType = annotationType;
        this.considerMetaAnnotations = considerMetaAnnotations;
    }

    @Override
    protected boolean matchSelf(MetadataReader metadataReader)
    {
        AnnotationMetadata metadata = metadataReader.getAnnotationMetadata();
        return metadata.hasAnnotation(this.annotationType.getName()) || (this.considerMetaAnnotations && metadata.hasMetaAnnotation(this.annotationType.getName()));
    }

    @Override
    protected Boolean matchSuperClass(String superClassName)
    {
        if (Object.class.getName().equals(superClassName))
        {
            return Boolean.FALSE;
        } else if (superClassName.startsWith("java."))
        {
            try
            {
                Class<?> clazz = getClass().getClassLoader().loadClass(superClassName);
                return (clazz.getAnnotation(this.annotationType) != null);
            } catch (ClassNotFoundException ex)
            {
                // Class not found - can't determine a match that way.
            }
        }
        return null;
    }
}
