package com.jfxgraph.scanner.type;

/**
 * 一个标准类的元数据抽象接口，不需要加载类.
 * 
 * @see AnnotationMetadata
 * @author Albert
 * @since 1.0
 */
public interface ClassMetadata
{
    /**
     * Return the name of the underlying class. 返回基础类的名称。
     */
    String getClassName();

    /**
     * Return whether the underlying class represents an interface. 返回基础的类是否表示一个接口。
     */
    boolean isInterface();

    /**
     * Return whether the underlying class is marked as abstract.
     */
    boolean isAbstract();

    /**
     * 返回基础类是否表示一个具体的类，即不是接口或抽象类。
     */
    boolean isConcrete();

    /**
     * 基础类是否使用final标记符。
     */
    boolean isFinal();

    /**
     * Determine whether the underlying class is independent, i.e. whether it is a top-level class or a nested class
     * (static inner class) that can be constructed independent from an enclosing class.
     * <p>
     * 确定基础类是否是独立的即:无论是一个顶级类或可以用来构造独立封闭类中的嵌套的类 （静态内部类）。
     */
    boolean isIndependent();

    /**
     * Return whether the underlying class has an enclosing class (i.e. the underlying class is an inner/nested class or
     * a local class within a method).
     * <p>
     * If this method returns {@code false}, then the underlying class is a top-level class.
     */
    boolean hasEnclosingClass();

    /**
     * Return the name of the enclosing class of the underlying class, or {@code null} if the underlying class is a
     * top-level class.
     */
    String getEnclosingClassName();

    /**
     * Return whether the underlying class has a super class.
     */
    boolean hasSuperClass();

    /**
     * Return the name of the super class of the underlying class, or {@code null} if there is no super class defined.
     */
    String getSuperClassName();

    /**
     * Return the names of all interfaces that the underlying class implements, or an empty array if there are none.
     */
    String[] getInterfaceNames();

    /**
     * Return the names of all classes declared as members of the class represented by this ClassMetadata object. This
     * includes public, protected, default (package) access, and private classes and interfaces declared by the class,
     * but excludes inherited classes and interfaces. An empty array is returned if no member classes or interfaces
     * exist.
     */
    String[] getMemberClassNames();
}
