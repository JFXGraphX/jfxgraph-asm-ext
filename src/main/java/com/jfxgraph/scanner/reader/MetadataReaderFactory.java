package com.jfxgraph.scanner.reader;

import java.io.IOException;

import com.jfxgraph.scanner.io.Resource;

/**
 * MetadataReader实例的工厂接口，为每一个原资源缓存一个MetadataReader。:
 * 
 * @author Albert
 * @version v0.5 2013年10月27日 下午5:28:53 Albert Exp .
 * @since 1.0
 * @see SimpleMetadataReaderFactory
 * @see CachingMetadataReaderFactory
 */
public interface MetadataReaderFactory
{
    /**
     * 通过类名获取MetadataReader实例。
     * 
     * @param className
     *            类名 (用于解析 ".class" 文件)
     * @return ClassReader实例的持有者 (never {@code null})
     * @throws IOException
     *             在I / O故障的情况下抛出此异常
     */
    MetadataReader getMetadataReader(String className) throws IOException;

    /**
     * 通过{@link Resource}获取MetadataReader实例。
     * 
     * @param resource
     *            资源 (一个 ".class" 文件)
     * @return ClassReader实例的持有者 (never {@code null})
     * @throws IOException
     *             在I / O故障的情况下抛出此异常
     */
    MetadataReader getMetadataReader(Resource resource) throws IOException;
}
