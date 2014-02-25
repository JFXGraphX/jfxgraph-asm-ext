package com.jfxgraph.scanner.reader;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import com.jfxgraph.scanner.io.Resource;
import com.jfxgraph.scanner.io.loader.ResourceLoader;

/**
 * MetadataReaderFactory接口的缓存实现，缓存每一个资源文件的MetadataReader实例。 <br>
 * caching {@link MetadataReader} per {@link Resource} handle (i.e. per ".class" file).
 * 
 * @author Albert
 * @version $Id: CachingMetadataReaderFactory.java,v0.5 2013年10月27日 下午5:52:37 Albert Exp .
 * @since 1.0
 */
public class CachingMetadataReaderFactory extends SimpleMetadataReaderFactory
{
    /** 默认MetadataReader最大缓存条目为：256 */
    public static final int DEFAULT_CACHE_LIMIT = 256;

    private volatile int cacheLimit = DEFAULT_CACHE_LIMIT;

    /**
     * Specify the maximum number of entries for the MetadataReader cache. Default is 256.
     */
    public void setCacheLimit(int cacheLimit)
    {
        this.cacheLimit = cacheLimit;
    }

    /**
     * Return the maximum number of entries for the MetadataReader cache.
     */
    public int getCacheLimit()
    {
        return this.cacheLimit;
    }

    private final Map<Resource, MetadataReader> metadataReaderCache = new LinkedHashMap<Resource, MetadataReader>(DEFAULT_CACHE_LIMIT, 0.75f, true)
    {
        private static final long serialVersionUID = 1L;

        @Override
        protected boolean removeEldestEntry(Map.Entry<Resource, MetadataReader> eldest)
        {
            return size() > getCacheLimit();
        }
    };

    /**
     * 为默认的类加载器创建一个新的CachingMetadataReaderFactory。
     */
    public CachingMetadataReaderFactory()
    {
        super();
    }

    /**
     * 对于给定的资源加载器创建一个新的CachingMetadataReaderFactory。
     * 
     * @param resourceLoader
     *            程序使用的资源加载器 (或者由ClassLoader决定)
     */
    public CachingMetadataReaderFactory(ResourceLoader resourceLoader)
    {
        super(resourceLoader);
    }

    /**
     * 为给定的{code ClassLoader}创建一个新的CachingMetadataReaderFactory。
     * 
     * @param classLoader
     *            传入的 {code ClassLoader}
     */
    public CachingMetadataReaderFactory(ClassLoader classLoader)
    {
        super(classLoader);
    }

    @Override
    public MetadataReader getMetadataReader(Resource resource) throws IOException
    {
        if (getCacheLimit() <= 0)
        {
            return super.getMetadataReader(resource);
        }
        synchronized (this.metadataReaderCache)
        {
            MetadataReader metadataReader = this.metadataReaderCache.get(resource);
            if (metadataReader == null)
            {
                metadataReader = super.getMetadataReader(resource);
                this.metadataReaderCache.put(resource, metadataReader);
            }
            return metadataReader;
        }
    }

    /**
     * Clear the entire MetadataReader cache, removing all cached class metadata.
     */
    public void clearCache()
    {
        synchronized (this.metadataReaderCache)
        {
            this.metadataReaderCache.clear();
        }
    }
}
