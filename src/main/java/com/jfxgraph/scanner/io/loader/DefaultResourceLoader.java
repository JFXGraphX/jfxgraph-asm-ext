package com.jfxgraph.scanner.io.loader;

import java.net.MalformedURLException;
import java.net.URL;

import com.jfxgraph.scanner.io.ClassPathResource;
import com.jfxgraph.scanner.io.ContextResource;
import com.jfxgraph.scanner.io.Resource;
import com.jfxgraph.scanner.io.UrlResource;
import com.jfxgraph.scanner.util.Assert;
import com.jfxgraph.scanner.util.ClassUtils;
import com.jfxgraph.scanner.util.StringUtils;

/**
 * 默认的资源加载器.
 * @author Albert
 * @since 1.0
 */
public class DefaultResourceLoader implements ResourceLoader
{
    private ClassLoader classLoader;

    public DefaultResourceLoader()
    {
        this.classLoader = ClassUtils.getDefaultClassLoader();
    }
    
    public DefaultResourceLoader(ClassLoader classLoader)
    {
        this.classLoader = classLoader;
    }

    public void setClassLoader(ClassLoader classLoader)
    {
        this.classLoader = classLoader;
    }

    public ClassLoader getClassLoader()
    {
        return (this.classLoader != null ? this.classLoader : ClassUtils.getDefaultClassLoader());
    }

    /**
     * 通过传入的路径读取资源。
     */
    public Resource getResource(String location)
    {
        Assert.notNull(location, "Location must not be null");

        if (location.startsWith(CLASSPATH_URL_PREFIX))
        {
            return new ClassPathResource(location.substring(CLASSPATH_URL_PREFIX.length()), getClassLoader());
        } else
        {
            try
            {
                // Try to parse the location as a URL...
                URL url = new URL(location);
                return new UrlResource(url);
            } catch (MalformedURLException ex)
            {
                // No URL -> resolve as resource path.
                return getResourceByPath(location);
            }
        }
    }

    /**
     * 返回指定的路径下资源的资源句柄
     */
    protected Resource getResourceByPath(String path)
    {
        return new ClassPathContextResource(path, getClassLoader());
    }

    /**
     * 通过实现ContextResource接口，明确地表示一个上下文下的ClassPath资源.
     * @author    Albert
     * @since   1.0
     */
    private static class ClassPathContextResource extends ClassPathResource implements ContextResource
    {
        public ClassPathContextResource(String path, ClassLoader classLoader)
        {
            super(path, classLoader);
        }

        public String getPathWithinContext()
        {
            return getPath();
        }

        @Override
        public Resource createRelative(String relativePath)
        {
            String pathToUse = StringUtils.applyRelativePath(getPath(), relativePath);
            return new ClassPathContextResource(pathToUse, getClassLoader());
        }
    }
}
