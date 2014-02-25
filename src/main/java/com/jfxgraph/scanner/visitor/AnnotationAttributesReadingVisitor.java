/**
 * Copyright (c) 2012-2013,Epic-HUST Technology(Wuhan)Co.,Ltd. All Rights Reserved.
 */
package com.jfxgraph.scanner.visitor;

import java.lang.annotation.Annotation;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import com.jfxgraph.scanner.util.AnnotationAttributes;
import com.jfxgraph.scanner.util.AnnotationUtils;

/**
 * 〈一句话功能简述〉
 * <p>
 * 〈功能详细描述〉
 * </p>
 * 特殊描述:
 * @author    Albert
 * @version   $Id: AnnotationAttributesReadingVisitor.java,v0.5 2013年10月27日 下午4:32:25 Albert Exp .
 * @since   3.1
 */
public class AnnotationAttributesReadingVisitor extends RecursiveAnnotationAttributesVisitor {

    private final String annotationType;

    private final Map<String, AnnotationAttributes> attributesMap;

    private final Map<String, Set<String>> metaAnnotationMap;


    public AnnotationAttributesReadingVisitor(
            String annotationType, Map<String, AnnotationAttributes> attributesMap,
            Map<String, Set<String>> metaAnnotationMap, ClassLoader classLoader) {

        super(annotationType, new AnnotationAttributes(), classLoader);
        this.annotationType = annotationType;
        this.attributesMap = attributesMap;
        this.metaAnnotationMap = metaAnnotationMap;
    }

    @Override
    public void doVisitEnd(Class<?> annotationClass) {
        super.doVisitEnd(annotationClass);
        this.attributesMap.put(this.annotationType, this.attributes);
        registerMetaAnnotations(annotationClass);
    }

    private void registerMetaAnnotations(Class<?> annotationClass) {
        // Register annotations that the annotation type is annotated with.
        Set<String> metaAnnotationTypeNames = new LinkedHashSet<String>();
        for (Annotation metaAnnotation : annotationClass.getAnnotations()) {
            metaAnnotationTypeNames.add(metaAnnotation.annotationType().getName());
            if (!this.attributesMap.containsKey(metaAnnotation.annotationType().getName())) {
                this.attributesMap.put(metaAnnotation.annotationType().getName(),
                        AnnotationUtils.getAnnotationAttributes(metaAnnotation, true, true));
            }
            for (Annotation metaMetaAnnotation : metaAnnotation.annotationType().getAnnotations()) {
                metaAnnotationTypeNames.add(metaMetaAnnotation.annotationType().getName());
            }
        }
        if (this.metaAnnotationMap != null) {
            this.metaAnnotationMap.put(annotationClass.getName(), metaAnnotationTypeNames);
        }
    }
}
