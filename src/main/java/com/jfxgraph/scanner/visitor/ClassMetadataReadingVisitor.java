/**
 * Copyright (c) 2013 著作权由艾普工华武汉科技公司所有。著作权人保留一切权利。
 * 这份授权条款，在使用者符合以下四条件的情形下，授予使用者使用及再散播本 软件包装原始码及二进位可执行形式的权利，无论此包装是否经改作皆然：
 * 
 * 1. 对于本软件源代码的再散播，必须保留上述的版权宣告、此四条件表列，以 及下述的免责声明。 2.
 * 对于本套件二进位可执行形式的再散播，必须连带以文件以及／或者其他附 于散播包装中的媒介方式，重制上述之版权宣告、此四条件表列，以及下述 的免责声明。 3.
 * 所有提及本软件功能或是本软件使用之宣传材料，都必须包还含下列之交 待文字： “本产品内含有由柏克莱加州大学及其软件贡献者所开发的软件。” 4.
 * 未获事前取得书面许可，不得使用柏克莱加州大学或本软件贡献者之名称， 来为本软件之衍生物做任何表示支持、认可或推广、促销之行为。
 * 
 * 免责声明：本软件是由加州大学董事会及本软件之贡献者以现状（"as is"）提供， 本软件包装不负任何明示或默示之担保责任，包括但不限于就适售性以及特定目
 * 的的适用性为默示性担保。艾普工华武汉科技公司及本软件之贡献者，无论任何条件、 无论成因或任何责任主义、无论此责任为因合约关系、无过失责任主义或因非违
 * 约之侵权（包括过失或其他原因等）而起，对于任何因使用本软件包装所产生的 任何直接性、间接性、偶发性、特殊性、惩罚性或任何结果的损害（包括但不限
 * 于替代商品或劳务之购用、使用损失、资料损失、利益损失、业务中断等等）， 不负任何责任，即在该种使用已获事前告知可能会造成此类损害的情形下亦然。
 */
package com.jfxgraph.scanner.visitor;

import java.util.LinkedHashSet;
import java.util.Set;

import com.jfxgraph.asm.AnnotationVisitor;
import com.jfxgraph.asm.Attribute;
import com.jfxgraph.asm.ClassVisitor;
import com.jfxgraph.asm.FieldVisitor;
import com.jfxgraph.asm.MethodVisitor;
import com.jfxgraph.asm.Opcodes;
import com.jfxgraph.scanner.type.ClassMetadata;
import com.jfxgraph.scanner.util.ClassUtils;

/**
 * ASM class visitor which looks only for the class name and implemented types,
 * exposing them through the
 * {@link com.epichust.mestar.asm.type.springframework.core.type.ClassMetadata}
 * interface.
 * 
 * @author Rod Johnson
 * @author Costin Leau
 * @author Mark Fisher
 * @author Ramnivas Laddad
 * @author Chris Beams
 * @author Albert
 * @since 2.5
 * @since 4.0 2013年10月25日 下午12:00:50
 */
public class ClassMetadataReadingVisitor extends ClassVisitor implements ClassMetadata {
    private String className;

    private boolean isInterface;

    private boolean isAbstract;

    private boolean isFinal;

    private String enclosingClassName;

    private boolean independentInnerClass;

    private String superClassName;

    private String[] interfaces;

    private Set<String> memberClassNames = new LinkedHashSet<String>();

    public ClassMetadataReadingVisitor() {
        super(Opcodes.ASM4);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        this.className = ClassUtils.convertResourcePathToClassName(name);
        this.isInterface = ((access & Opcodes.ACC_INTERFACE) != 0);
        this.isAbstract = ((access & Opcodes.ACC_ABSTRACT) != 0);
        this.isFinal = ((access & Opcodes.ACC_FINAL) != 0);
        if (superName != null) {
            this.superClassName = ClassUtils.convertResourcePathToClassName(superName);
        }
        this.interfaces = new String[interfaces.length];
        for (int i = 0; i < interfaces.length; i++) {
            this.interfaces[i] = ClassUtils.convertResourcePathToClassName(interfaces[i]);
        }
    }

    @Override
    public void visitOuterClass(String owner, String name, String desc) {
        // super.visitOuterClass(owner, name, desc);
        this.enclosingClassName = ClassUtils.convertResourcePathToClassName(owner);
    }

    @Override
    public void visitInnerClass(String name, String outerName, String innerName, int access) {
        // super.visitInnerClass(name, outerName, innerName, access);
        if (outerName != null) {
            String fqName = ClassUtils.convertResourcePathToClassName(name);
            String fqOuterName = ClassUtils.convertResourcePathToClassName(outerName);
            if (this.className.equals(fqName)) {
                this.enclosingClassName = fqOuterName;
                this.independentInnerClass = ((access & Opcodes.ACC_STATIC) != 0);
            } else if (this.className.equals(fqOuterName)) {
                this.memberClassNames.add(fqName);
            }
        }
    }

    @Override
    public void visitSource(String source, String debug) {
        // no-op
    }

    @Override
    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        // no-op
        return new EmptyAnnotationVisitor();
    }

    @Override
    public void visitAttribute(Attribute attr) {
        // no-op
    }

    @Override
    public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
        // no-op
        return new EmptyFieldVisitor();
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        // no-op
        return new EmptyMethodVisitor();
    }

    @Override
    public void visitEnd() {
        // no-op
    }

    @Override
    public String getClassName() {
        return this.className;
    }

    @Override
    public boolean isInterface() {
        return this.isInterface;
    }

    @Override
    public boolean isAbstract() {
        return this.isAbstract;
    }

    @Override
    public boolean isConcrete() {
        return !(this.isInterface || this.isAbstract);
    }

    @Override
    public boolean isFinal() {
        return this.isFinal;
    }

    @Override
    public boolean isIndependent() {
        return (this.enclosingClassName == null || this.independentInnerClass);
    }

    @Override
    public boolean hasEnclosingClass() {
        return (this.enclosingClassName != null);
    }

    @Override
    public String getEnclosingClassName() {
        return this.enclosingClassName;
    }

    @Override
    public boolean hasSuperClass() {
        return (this.superClassName != null);
    }

    @Override
    public String getSuperClassName() {
        return this.superClassName;
    }

    @Override
    public String[] getInterfaceNames() {
        return this.interfaces;
    }

    @Override
    public String[] getMemberClassNames() {
        return this.memberClassNames.toArray(new String[this.memberClassNames.size()]);
    }
}

class EmptyAnnotationVisitor extends AnnotationVisitor {

    public EmptyAnnotationVisitor() {
        super(Opcodes.ASM4);
    }

    @Override
    public AnnotationVisitor visitAnnotation(String name, String desc) {
        return this;
    }

    @Override
    public AnnotationVisitor visitArray(String name) {
        return this;
    }
}

class EmptyMethodVisitor extends MethodVisitor {

    public EmptyMethodVisitor() {
        super(Opcodes.ASM4);
    }
}

class EmptyFieldVisitor extends FieldVisitor {

    public EmptyFieldVisitor() {
        super(Opcodes.ASM4);
    }

}