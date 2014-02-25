/**
 * Copyright (c) 2012-2013,Epic-HUST Technology(Wuhan)Co.,Ltd. All Rights Reserved.
 */
package com.jfxgraph.scanner.visitor;

import java.lang.reflect.Field;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jfxgraph.asm.AnnotationVisitor;
import com.jfxgraph.asm.Opcodes;
import com.jfxgraph.asm.Type;
import com.jfxgraph.scanner.util.AnnotationAttributes;
import com.jfxgraph.scanner.util.ReflectionUtils;

/**
 * AbstractRecursiveAnnotationVisitor.java
 * @author    Albert
 * @version   $Id: AbstractRecursiveAnnotationVisitor.java,v0.5 2013年10月27日 下午4:27:54 Albert Exp .
 * @since   3.1
 */
public abstract class AbstractRecursiveAnnotationVisitor extends AnnotationVisitor {
    protected final Logger logger =LoggerFactory.getLogger(this.getClass());
    protected final AnnotationAttributes attributes;

    protected final ClassLoader classLoader;

    public AbstractRecursiveAnnotationVisitor(ClassLoader classLoader, AnnotationAttributes attributes) {
        super(Opcodes.ASM4);
        this.classLoader = classLoader;
        this.attributes = attributes;
    }


    public void visit(String attributeName, Object attributeValue) {
        this.attributes.put(attributeName, attributeValue);
    }

    public AnnotationVisitor visitAnnotation(String attributeName, String asmTypeDescriptor) {
        String annotationType = Type.getType(asmTypeDescriptor).getClassName();
        AnnotationAttributes nestedAttributes = new AnnotationAttributes();
        this.attributes.put(attributeName, nestedAttributes);
        return new RecursiveAnnotationAttributesVisitor(annotationType, nestedAttributes, this.classLoader);
    }

    public AnnotationVisitor visitArray(String attributeName) {
        return new RecursiveAnnotationArrayVisitor(attributeName, this.attributes, this.classLoader);
    }

    public void visitEnum(String attributeName, String asmTypeDescriptor, String attributeValue) {
        Object valueToUse = attributeValue;
        try {
            Class<?> enumType = this.classLoader.loadClass(Type.getType(asmTypeDescriptor).getClassName());
            Field enumConstant = ReflectionUtils.findField(enumType, attributeValue);
            if (enumConstant != null) {
                valueToUse = enumConstant.get(null);
            }
        }
        catch (ClassNotFoundException ex) {
            this.logger.debug("Failed to classload enum type while reading annotation metadata", ex);
        }
        catch (IllegalAccessException ex) {
            this.logger.warn("Could not access enum value while reading annotation metadata", ex);
        }
        this.attributes.put(attributeName, valueToUse);
    }
}
