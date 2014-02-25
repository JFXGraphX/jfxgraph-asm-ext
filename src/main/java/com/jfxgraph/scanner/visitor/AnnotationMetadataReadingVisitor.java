/**
 * Copyright (c) 2012-2013,Epic-HUST Technology(Wuhan)Co.,Ltd. All Rights Reserved.
 */
package com.jfxgraph.scanner.visitor;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.jfxgraph.asm.AnnotationVisitor;
import com.jfxgraph.asm.MethodVisitor;
import com.jfxgraph.asm.Type;
import com.jfxgraph.scanner.type.AnnotationMetadata;
import com.jfxgraph.scanner.type.MethodMetadata;
import com.jfxgraph.scanner.util.AnnotationAttributes;
import com.jfxgraph.scanner.util.CollectionUtils;
import com.jfxgraph.scanner.util.LinkedMultiValueMap;
import com.jfxgraph.scanner.util.MultiValueMap;

/**
 * 〈一句话功能简述〉
 * <p>
 * 〈功能详细描述〉
 * </p>
 * 特殊描述:
 * @author    Albert
 * @version   $Id: AnnotationMetadataReadingVisitor.java,v0.5 2013年10月27日 下午4:11:09 Albert Exp .
 * @since   3.1
 */
public class AnnotationMetadataReadingVisitor extends ClassMetadataReadingVisitor implements AnnotationMetadata
{

    private final ClassLoader classLoader;

    private final Set<String> annotationSet = new LinkedHashSet<String>();

    private final Map<String, Set<String>> metaAnnotationMap = new LinkedHashMap<String, Set<String>>(4);

    private final Map<String, AnnotationAttributes> attributeMap = new LinkedHashMap<String, AnnotationAttributes>(4);

    private final MultiValueMap<String, MethodMetadata> methodMetadataMap = new LinkedMultiValueMap<String, MethodMetadata>();

    public AnnotationMetadataReadingVisitor(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        return new MethodMetadataReadingVisitor(name, access, this.getClassName(), this.classLoader, this.methodMetadataMap);
    }

    @Override
    public AnnotationVisitor visitAnnotation(final String desc, boolean visible) {
        String className = Type.getType(desc).getClassName();
        this.annotationSet.add(className);
        return new AnnotationAttributesReadingVisitor(className, this.attributeMap, this.metaAnnotationMap, this.classLoader);
    }

    @Override
    public Set<String> getAnnotationTypes() {
        return this.annotationSet;
    }

    @Override
    public Set<String> getMetaAnnotationTypes(String annotationType) {
        return this.metaAnnotationMap.get(annotationType);
    }

    @Override
    public boolean hasAnnotation(String annotationType) {
        return this.annotationSet.contains(annotationType);
    }

    @Override
    public boolean hasMetaAnnotation(String metaAnnotationType) {
        Collection<Set<String>> allMetaTypes = this.metaAnnotationMap.values();
        for (Set<String> metaTypes : allMetaTypes) {
            if (metaTypes.contains(metaAnnotationType)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isAnnotated(String annotationType) {
        return this.attributeMap.containsKey(annotationType);
    }

    @Override
    public AnnotationAttributes getAnnotationAttributes(String annotationType) {
        return getAnnotationAttributes(annotationType, false);
    }

    @Override
    public AnnotationAttributes getAnnotationAttributes(String annotationType, boolean classValuesAsString) {
        return getAnnotationAttributes(annotationType, classValuesAsString, false);
    }

    public AnnotationAttributes getAnnotationAttributes(String annotationType, boolean classValuesAsString, boolean nestedAttributesAsMap) {

        AnnotationAttributes raw = this.attributeMap.get(annotationType);
        return convertClassValues(raw, classValuesAsString, nestedAttributesAsMap);
    }

    private AnnotationAttributes convertClassValues(AnnotationAttributes original, boolean classValuesAsString, boolean nestedAttributesAsMap) {

        if (original == null) {
            return null;
        }
        AnnotationAttributes result = new AnnotationAttributes(original.size());
        for (Map.Entry<String, Object> entry : original.entrySet()) {
            try {
                Object value = entry.getValue();
                if (value instanceof AnnotationAttributes) {
                    value = convertClassValues((AnnotationAttributes) value, classValuesAsString, nestedAttributesAsMap);
                } else if (value instanceof AnnotationAttributes[]) {
                    AnnotationAttributes[] values = (AnnotationAttributes[]) value;
                    for (int i = 0; i < values.length; i++) {
                        values[i] = convertClassValues(values[i], classValuesAsString, nestedAttributesAsMap);
                    }
                } else if (value instanceof Type) {
                    value = (classValuesAsString ? ((Type) value).getClassName() : this.classLoader.loadClass(((Type) value).getClassName()));
                } else if (value instanceof Type[]) {
                    Type[] array = (Type[]) value;
                    Object[] convArray = (classValuesAsString ? new String[array.length] : new Class[array.length]);
                    for (int i = 0; i < array.length; i++) {
                        convArray[i] = (classValuesAsString ? array[i].getClassName() : this.classLoader.loadClass(array[i].getClassName()));
                    }
                    value = convArray;
                } else if (classValuesAsString) {
                    if (value instanceof Class) {
                        value = ((Class<?>) value).getName();
                    } else if (value instanceof Class[]) {
                        Class<?>[] clazzArray = (Class[]) value;
                        String[] newValue = new String[clazzArray.length];
                        for (int i = 0; i < clazzArray.length; i++) {
                            newValue[i] = clazzArray[i].getName();
                        }
                        value = newValue;
                    }
                }
                result.put(entry.getKey(), value);
            } catch (Exception ex) {
                // Class not found - can't resolve class reference in annotation
                // attribute.
            }
        }
        return result;
    }

    @Override
    public boolean hasAnnotatedMethods(String annotationType) {
        return this.methodMetadataMap.containsKey(annotationType);
    }

    @Override
    public Set<MethodMetadata> getAnnotatedMethods(String annotationType) {
        List<MethodMetadata> list = this.methodMetadataMap.get(annotationType);
        if (CollectionUtils.isEmpty(list)) {
            return new LinkedHashSet<MethodMetadata>(0);
        }
        Set<MethodMetadata> annotatedMethods = new LinkedHashSet<MethodMetadata>(list.size());
        annotatedMethods.addAll(list);
        return annotatedMethods;
    }
}
