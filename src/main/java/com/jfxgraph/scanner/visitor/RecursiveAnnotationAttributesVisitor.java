package com.jfxgraph.scanner.visitor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import com.jfxgraph.scanner.util.AnnotationAttributes;
import com.jfxgraph.scanner.util.AnnotationUtils;

public class RecursiveAnnotationAttributesVisitor extends AbstractRecursiveAnnotationVisitor {

    private final String annotationType;


    public RecursiveAnnotationAttributesVisitor(
            String annotationType, AnnotationAttributes attributes, ClassLoader classLoader) {
        super(classLoader, attributes);
        this.annotationType = annotationType;
    }


    public final void visitEnd() {
        try {
            Class<?> annotationClass = this.classLoader.loadClass(this.annotationType);
            this.doVisitEnd(annotationClass);
        }
        catch (ClassNotFoundException ex) {
            this.logger.debug("Failed to classload type while reading annotation " +
                    "metadata. This is a non-fatal error, but certain annotation " +
                    "metadata may be unavailable.", ex);
        }
    }

    protected void doVisitEnd(Class<?> annotationClass) {
        registerDefaultValues(annotationClass);
    }

    private void registerDefaultValues(Class<?> annotationClass) {
        // Check declared default values of attributes in the annotation type.
        Method[] annotationAttributes = annotationClass.getMethods();
        for (Method annotationAttribute : annotationAttributes) {
            String attributeName = annotationAttribute.getName();
            Object defaultValue = annotationAttribute.getDefaultValue();
            if (defaultValue != null && !this.attributes.containsKey(attributeName)) {
                if (defaultValue instanceof Annotation) {
                    defaultValue = AnnotationAttributes.fromMap(
                            AnnotationUtils.getAnnotationAttributes((Annotation)defaultValue, false, true));
                }
                else if (defaultValue instanceof Annotation[]) {
                    Annotation[] realAnnotations = (Annotation[]) defaultValue;
                    AnnotationAttributes[] mappedAnnotations = new AnnotationAttributes[realAnnotations.length];
                    for (int i = 0; i < realAnnotations.length; i++) {
                        mappedAnnotations[i] = AnnotationAttributes.fromMap(
                                AnnotationUtils.getAnnotationAttributes(realAnnotations[i], false, true));
                    }
                    defaultValue = mappedAnnotations;
                }
                this.attributes.put(attributeName, defaultValue);
            }
        }
    }
}
