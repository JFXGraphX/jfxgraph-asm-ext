package com.jfxgraph.scanner.reader;

import java.io.IOException;

import com.jfxgraph.scanner.io.Resource;
import com.jfxgraph.scanner.io.loader.DefaultResourceLoader;
import com.jfxgraph.scanner.io.loader.ResourceLoader;
import com.jfxgraph.scanner.util.ClassUtils;

/**
 * {@link MetadataReaderFactory} 接口的简单实现,为每个请求创建一个新的ClassReader(ASM).
 * 
 * @author Albert
 * @version $Id: SimpleMetadataReaderFactory.java,v0.5 2013年10月27日 下午5:40:03 Albert Exp .
 * @since 1.0
 */
public class SimpleMetadataReaderFactory implements MetadataReaderFactory
{

    private final ResourceLoader resourceLoader;

    /**
     * Create a new SimpleMetadataReaderFactory for the default class loader.
     */
    public SimpleMetadataReaderFactory()
    {
        this.resourceLoader = new DefaultResourceLoader();
    }

    /**
     * Create a new SimpleMetadataReaderFactory for the given resource loader.
     * 
     * @param resourceLoader
     *            the Spring ResourceLoader to use (also determines the ClassLoader to use)
     */
    public SimpleMetadataReaderFactory(ResourceLoader resourceLoader)
    {
        this.resourceLoader = (resourceLoader != null ? resourceLoader : new DefaultResourceLoader());
    }

    /**
     * Create a new SimpleMetadataReaderFactory for the given class loader.
     * 
     * @param classLoader
     *            the ClassLoader to use
     */
    public SimpleMetadataReaderFactory(ClassLoader classLoader)
    {
        this.resourceLoader = (classLoader != null ? new DefaultResourceLoader(classLoader) : new DefaultResourceLoader());
    }

    /**
     * 返回MetadataReaderFactory构造阶段使用的资源加载器。
     * 
     * @return ResourceLoader
     */
    public final ResourceLoader getResourceLoader()
    {
        return this.resourceLoader;
    }

    /**
     * SimpleMetadataReaderFactory简单实现.
     * @see com.jfxgraph.scanner.reader.MetadataReaderFactory#getMetadataReader(java.lang.String)
     */
    @Override
    public MetadataReader getMetadataReader(String className) throws IOException
    {
        String resourcePath = ResourceLoader.CLASSPATH_URL_PREFIX + ClassUtils.convertClassNameToResourcePath(className) + ClassUtils.CLASS_FILE_SUFFIX;
        return getMetadataReader(this.resourceLoader.getResource(resourcePath));
    }

    /**
     * SimpleMetadataReaderFactory简单实现.
     * @see com.jfxgraph.scanner.reader.MetadataReaderFactory#getMetadataReader(com.jfxgraph.scanner.io.Resource)
     */
    @Override
    public MetadataReader getMetadataReader(Resource resource) throws IOException
    {
        return new SimpleMetadataReader(resource, this.resourceLoader.getClassLoader());
    }
}
