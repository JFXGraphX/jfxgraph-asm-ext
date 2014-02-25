package com.jfxgraph.scanner.reader;

import com.jfxgraph.scanner.io.Resource;
import com.jfxgraph.scanner.type.AnnotationMetadata;
import com.jfxgraph.scanner.type.ClassMetadata;

/**
 * Simple facade for accessing class metadata, as read by an ASM
 * @author    Albert
 * @version   $Id: MetadataReader.java,v0.5 2013年10月27日 下午4:47:05 Albert Exp .
 * @since   1.0
 */
public interface MetadataReader
{
    /**
     * 返回当前类所对应的资源对象.
     */
    Resource getResource();
    
    /**
     * 读取基础类的基础元数据。
     */
    ClassMetadata getClassMetadata();

    /**
     * 读取基础类的所有的注解元数据（包括注解的方法）.
     */
    AnnotationMetadata getAnnotationMetadata();
}
