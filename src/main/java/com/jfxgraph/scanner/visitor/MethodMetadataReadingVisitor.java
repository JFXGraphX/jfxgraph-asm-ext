/**
 * Copyright (c) 2012-2013,Epic-HUST Technology(Wuhan)Co.,Ltd. All Rights Reserved.
 */
package com.jfxgraph.scanner.visitor;

import java.util.LinkedHashMap;
import java.util.Map;

import com.jfxgraph.asm.AnnotationVisitor;
import com.jfxgraph.asm.MethodVisitor;
import com.jfxgraph.asm.Opcodes;
import com.jfxgraph.asm.Type;
import com.jfxgraph.scanner.type.MethodMetadata;
import com.jfxgraph.scanner.util.AnnotationAttributes;
import com.jfxgraph.scanner.util.MultiValueMap;

/**
 * MethodMetadataReadingVisitor.java
 * @author    Albert
 * @version   $Id: MethodMetadataReadingVisitor.java,v0.5 2013年10月27日 下午4:24:56 Albert Exp .
 * @since   3.1
 */
public final class MethodMetadataReadingVisitor extends MethodVisitor implements MethodMetadata
{


    private final String name;

    private final int access;

    private String declaringClassName;

    private final ClassLoader classLoader;

    private final MultiValueMap<String, MethodMetadata> methodMetadataMap;

    private final Map<String, AnnotationAttributes> attributeMap = new LinkedHashMap<String, AnnotationAttributes>(2);

    public MethodMetadataReadingVisitor(String name, int access, String declaringClassName, ClassLoader classLoader,
            MultiValueMap<String, MethodMetadata> methodMetadataMap) {
        super(Opcodes.ASM4);
        this.name = name;
        this.access = access;
        this.declaringClassName = declaringClassName;
        this.classLoader = classLoader;
        this.methodMetadataMap = methodMetadataMap;
    }

    @Override
    public AnnotationVisitor visitAnnotation(final String desc, boolean visible) {
        String className = Type.getType(desc).getClassName();
        methodMetadataMap.add(className, this);
        return new AnnotationAttributesReadingVisitor(className, this.attributeMap, null, this.classLoader);
    }

    public String getMethodName() {
        return this.name;
    }

    public boolean isStatic() {
        return ((this.access & Opcodes.ACC_STATIC) != 0);
    }

    public boolean isFinal() {
        return ((this.access & Opcodes.ACC_FINAL) != 0);
    }

    public boolean isOverridable() {
        return (!isStatic() && !isFinal() && ((this.access & Opcodes.ACC_PRIVATE) == 0));
    }

    public boolean isAnnotated(String annotationType) {
        return this.attributeMap.containsKey(annotationType);
    }

    public AnnotationAttributes getAnnotationAttributes(String annotationType) {
        return this.attributeMap.get(annotationType);
    }

    public String getDeclaringClassName() {
        return this.declaringClassName;
    }

}
