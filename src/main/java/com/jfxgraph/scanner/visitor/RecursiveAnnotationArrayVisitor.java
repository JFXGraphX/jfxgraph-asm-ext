package com.jfxgraph.scanner.visitor;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import com.jfxgraph.asm.AnnotationVisitor;
import com.jfxgraph.asm.Type;
import com.jfxgraph.scanner.util.AnnotationAttributes;
import com.jfxgraph.scanner.util.ObjectUtils;


public final class RecursiveAnnotationArrayVisitor extends AbstractRecursiveAnnotationVisitor
{
    private final String attributeName;

    private final List<AnnotationAttributes> allNestedAttributes = new ArrayList<AnnotationAttributes>();

    public RecursiveAnnotationArrayVisitor(
            String attributeName, AnnotationAttributes attributes, ClassLoader classLoader) {
        super(classLoader, attributes);
        this.attributeName = attributeName;
    }

    @Override
    public void visit(String attributeName, Object attributeValue) {
        Object newValue = attributeValue;
        Object existingValue = this.attributes.get(this.attributeName);
        if (existingValue != null) {
            newValue = ObjectUtils.addObjectToArray((Object[]) existingValue, newValue);
        }
        else {
            Object[] newArray = (Object[]) Array.newInstance(newValue.getClass(), 1);
            newArray[0] = newValue;
            newValue = newArray;
        }
        this.attributes.put(this.attributeName, newValue);
    }

    @Override
    public AnnotationVisitor visitAnnotation(String attributeName, String asmTypeDescriptor) {
        String annotationType = Type.getType(asmTypeDescriptor).getClassName();
        AnnotationAttributes nestedAttributes = new AnnotationAttributes();
        this.allNestedAttributes.add(nestedAttributes);
        return new RecursiveAnnotationAttributesVisitor(annotationType, nestedAttributes, this.classLoader);
    }

    public void visitEnd() {
        if (!this.allNestedAttributes.isEmpty()) {
            this.attributes.put(this.attributeName, this.allNestedAttributes.toArray(
                    new AnnotationAttributes[this.allNestedAttributes.size()]));
        }
    }
}
