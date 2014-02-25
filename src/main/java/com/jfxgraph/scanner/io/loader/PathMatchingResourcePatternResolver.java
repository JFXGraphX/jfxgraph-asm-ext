package com.jfxgraph.scanner.io.loader;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.JarURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jfxgraph.scanner.io.FileSystemResource;
import com.jfxgraph.scanner.io.Resource;
import com.jfxgraph.scanner.io.UrlResource;
import com.jfxgraph.scanner.util.AntPathMatcher;
import com.jfxgraph.scanner.util.Assert;
import com.jfxgraph.scanner.util.PathMatcher;
import com.jfxgraph.scanner.util.ReflectionUtils;
import com.jfxgraph.scanner.util.ResourceUtils;
import com.jfxgraph.scanner.util.StringUtils;

/**
 * PathMatchingResourcePatternResolver.
 * 
 * @author Albert
 * @since 1.0
 */
public class PathMatchingResourcePatternResolver implements ResourcePatternResolver
{
    protected static final Logger logger = LoggerFactory.getLogger(PathMatchingResourcePatternResolver.class);

    private static Method equinoxResolveMethod;

    static
    {
        // Detect Equinox OSGi (e.g. on WebSphere 6.1)
        try
        {
            Class<?> fileLocatorClass = PathMatchingResourcePatternResolver.class.getClassLoader().loadClass("org.eclipse.core.runtime.FileLocator");
            equinoxResolveMethod = fileLocatorClass.getMethod("resolve", URL.class);
            logger.debug("Found Equinox FileLocator for OSGi bundle URL resolution");
        } catch (Throwable ex)
        {
            equinoxResolveMethod = null;
        }
    }

    private final ResourceLoader resourceLoader;

    private PathMatcher pathMatcher = new AntPathMatcher();

    /**
     * Create a new PathMatchingResourcePatternResolver with a DefaultResourceLoader.
     */
    public PathMatchingResourcePatternResolver()
    {
        this.resourceLoader = new DefaultResourceLoader();
    }

    public PathMatchingResourcePatternResolver(ClassLoader classLoader)
    {
        this.resourceLoader = new DefaultResourceLoader(classLoader);
    }

    /**
     * Create a new PathMatchingResourcePatternResolver.
     * <p>
     * ClassLoader access will happen via the thread context class loader.
     * 
     * @param resourceLoader
     *            the ResourceLoader to load root directories and actual resources with
     */
    public PathMatchingResourcePatternResolver(ResourceLoader resourceLoader)
    {
        Assert.notNull(resourceLoader, "ResourceLoader must not be null");
        this.resourceLoader = resourceLoader;
    }

    /**
     * @see com.jfxgraph.scanner.io.loader.ResourceLoader#getClassLoader()
     */
    @Override
    public ClassLoader getClassLoader()
    {
        return getResourceLoader().getClassLoader();
    }

    /**
     * Set the PathMatcher implementation to use for this resource pattern resolver. Default is
     * AntPathMatcher.
     * 
     * @see org.springframework.util.AntPathMatcher
     */
    public void setPathMatcher(PathMatcher pathMatcher)
    {
        Assert.notNull(pathMatcher, "PathMatcher must not be null");
        this.pathMatcher = pathMatcher;
    }

    /**
     * Return the PathMatcher that this resource pattern resolver uses.
     */
    public PathMatcher getPathMatcher()
    {
        return this.pathMatcher;
    }

    /**
     * Return the ResourceLoader that this pattern resolver works with.
     */
    public ResourceLoader getResourceLoader()
    {
        return this.resourceLoader;
    }

    /**
     * @see com.jfxgraph.scanner.io.loader.ResourceLoader#getResource(java.lang.String)
     */
    @Override
    public Resource getResource(String location)
    {
        return getResourceLoader().getResource(location);
    }

    /**
     * 取得资源文件.
     * 
     * <p>
     * {code classpath*:com\epichust\mestar\**\*.class}
     * 
     * @see com.jfxgraph.scanner.io.loader.ResourcePatternResolver#getResources(java.lang.String)
     */
    @Override
    public Resource[] getResources(String locationPattern) throws IOException
    {
        Assert.notNull(locationPattern, "Location pattern must not be null");

        if (locationPattern.startsWith(CLASSPATH_ALL_URL_PREFIX))
        {
            // a class path resource (multiple resources for same name possible)
            // 路径中包含*或？号.
            if (getPathMatcher().isPattern(locationPattern.substring(CLASSPATH_ALL_URL_PREFIX.length())))
            {
                return findPathMatchingResources(locationPattern);
            } else
            {
                return findAllClassPathResources(locationPattern.substring(CLASSPATH_ALL_URL_PREFIX.length()));
            }
        } else
        {
            int prefixEnd = locationPattern.indexOf(":") + 1;
            if (getPathMatcher().isPattern(locationPattern.substring(prefixEnd)))
            {
                // a file pattern
                return findPathMatchingResources(locationPattern);
            } else
            {
                // a single resource with the given name
                return new Resource[] { getResourceLoader().getResource(locationPattern) };
            }
        }
    }

    /**
     * 通过ClassLoader获取给定路径下的所有资源文件。
     * 
     * @param location
     *            the absolute path within the classpath
     * @return the result as Resource array
     * @throws IOException
     *             in case of I/O errors
     * @see java.lang.ClassLoader#getResources
     * @see #convertClassLoaderURL
     */
    protected Resource[] findAllClassPathResources(String location) throws IOException
    {
        String path = location;
        if (path.startsWith("/"))
        {
            path = path.substring(1);
        }
        Enumeration<URL> resourceUrls = getClassLoader().getResources(path);
        Set<Resource> result = new LinkedHashSet<Resource>(16);
        while (resourceUrls.hasMoreElements())
        {
            URL url = resourceUrls.nextElement();
            result.add(convertClassLoaderURL(url));
        }
        return result.toArray(new Resource[result.size()]);
    }

    /**
     * 使用ClassLoader将给定的URL转换为资源对象.
     * <p>
     * The default implementation simply creates a UrlResource instance. 默认实现采用简单创建UrlResource实例。
     * 
     * @param url
     *            a URL as returned from the ClassLoader
     * @return the corresponding Resource object
     * @see java.lang.ClassLoader#getResources
     * @see Resource
     */
    protected Resource convertClassLoaderURL(URL url)
    {
        return new UrlResource(url);
    }

    /**
     * 使用Ant Style 的路径匹配器获取给定模式下的所有资源文件 支持读取jar包中及zip包中的文件.
     * 
     * @param locationPattern
     *            the location pattern to match
     * @return the result as Resource array
     * @throws IOException
     *             in case of I/O errors
     * @see #doFindPathMatchingJarResources
     * @see #doFindPathMatchingFileResources
     * @see PathMatcher
     */
    protected Resource[] findPathMatchingResources(String locationPattern) throws IOException
    {
        String rootDirPath = determineRootDir(locationPattern);
        String subPattern = locationPattern.substring(rootDirPath.length());

        Resource[] rootDirResources = getResources(rootDirPath);

        Set<Resource> result = new LinkedHashSet<Resource>(16);
        for (Resource rootDirResource : rootDirResources)
        {
            rootDirResource = resolveRootDirResource(rootDirResource);
            if (isJarResource(rootDirResource))
            {
                //jar文件时考虑仅扫描客户端相关的jar文件.
                result.addAll(doFindPathMatchingJarResources(rootDirResource, subPattern));
            } else
            {
                result.addAll(doFindPathMatchingFileResources(rootDirResource, subPattern));
            }
        }
        if (logger.isDebugEnabled())
        {
            logger.debug("匹配 [" + locationPattern + "] 路径模式的文件有{"+result.size()+"}个.");
            logger.debug(result+"");
            logger.debug("符合[" + locationPattern + "]模式的文件共计{"+result.size()+"}个，输出结束！！！");
        }
        return result.toArray(new Resource[result.size()]);
    }

    /**
     * 返回指定资源的资源句柄是否指示一个 jar资源。doFindPathMatchingJarResources方法可以处理。
     */
    protected boolean isJarResource(Resource resource) throws IOException
    {
        return ResourceUtils.isJarURL(resource.getURL());
    }

    /**
     * 将给定的jar file URL 转化为JarFile的对象.
     */
    protected JarFile getJarFile(String jarFileUrl) throws IOException
    {
        if (jarFileUrl.startsWith(ResourceUtils.FILE_URL_PREFIX))
        {
            try
            {
                return new JarFile(ResourceUtils.toURI(jarFileUrl).getSchemeSpecificPart());
            } catch (URISyntaxException ex)
            {
                // Fallback for URLs that are not valid URIs (should hardly ever happen).
                return new JarFile(jarFileUrl.substring(ResourceUtils.FILE_URL_PREFIX.length()));
            }
        } else
        {
            return new JarFile(jarFileUrl);
        }
    }

    /**
     * Find all resources in jar files that match the given location pattern via the Ant-style
     * PathMatcher.
     * 
     * @param rootDirResource
     *            the root directory as Resource
     * @param subPattern
     *            the sub pattern to match (below the root directory)
     * @return the Set of matching Resource instances
     * @throws IOException
     *             in case of I/O errors
     * @see java.net.JarURLConnection
     * @see org.springframework.util.PathMatcher
     */
    protected Set<Resource> doFindPathMatchingJarResources(Resource rootDirResource, String subPattern) throws IOException
    {

        URLConnection con = rootDirResource.getURL().openConnection();
        JarFile jarFile;
        String jarFileUrl;
        String rootEntryPath;
        boolean newJarFile = false;

        if (con instanceof JarURLConnection)
        {
            // Should usually be the case for traditional JAR files.
            JarURLConnection jarCon = (JarURLConnection) con;
            ResourceUtils.useCachesIfNecessary(jarCon);
            jarFile = jarCon.getJarFile();
            jarFileUrl = jarCon.getJarFileURL().toExternalForm();
            JarEntry jarEntry = jarCon.getJarEntry();
            rootEntryPath = (jarEntry != null ? jarEntry.getName() : "");
        } else
        {
            // No JarURLConnection -> need to resort to URL file parsing.
            // We'll assume URLs of the format "jar:path!/entry", with the protocol
            // being arbitrary as long as following the entry format.
            // We'll also handle paths with and without leading "file:" prefix.
            String urlFile = rootDirResource.getURL().getFile();
            int separatorIndex = urlFile.indexOf(ResourceUtils.JAR_URL_SEPARATOR);
            if (separatorIndex != -1)
            {
                jarFileUrl = urlFile.substring(0, separatorIndex);
                rootEntryPath = urlFile.substring(separatorIndex + ResourceUtils.JAR_URL_SEPARATOR.length());
                jarFile = getJarFile(jarFileUrl);
            } else
            {
                jarFile = new JarFile(urlFile);
                jarFileUrl = urlFile;
                rootEntryPath = "";
            }
            newJarFile = true;
        }

        try
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("Looking for matching resources in jar file [" + jarFileUrl + "]");
            }
            if (!"".equals(rootEntryPath) && !rootEntryPath.endsWith("/"))
            {
                // Root entry path must end with slash to allow for proper matching.
                // The Sun JRE does not return a slash here, but BEA JRockit does.
                rootEntryPath = rootEntryPath + "/";
            }
            Set<Resource> result = new LinkedHashSet<Resource>(8);
            for (Enumeration<JarEntry> entries = jarFile.entries(); entries.hasMoreElements();)
            {
                JarEntry entry = entries.nextElement();
                String entryPath = entry.getName();
                if (entryPath.startsWith(rootEntryPath))
                {
                    String relativePath = entryPath.substring(rootEntryPath.length());
                    if (getPathMatcher().match(subPattern, relativePath))
                    {
                        result.add(rootDirResource.createRelative(relativePath));
                    }
                }
            }
            return result;
        } finally
        {
            // Close jar file, but only if freshly obtained -
            // not from JarURLConnection, which might cache the file reference.
            if (newJarFile)
            {
                jarFile.close();
            }
        }
    }

    /**
     * Find all resources in the file system that match the given location pattern via the Ant-style
     * PathMatcher.
     * 
     * @param rootDirResource
     *            the root directory as Resource
     * @param subPattern
     *            the sub pattern to match (below the root directory)
     * @return the Set of matching Resource instances
     * @throws IOException
     *             in case of I/O errors
     * @see #retrieveMatchingFiles
     * @see org.springframework.util.PathMatcher
     */
    protected Set<Resource> doFindPathMatchingFileResources(Resource rootDirResource, String subPattern) throws IOException
    {
        File rootDir;
        try
        {
            rootDir = rootDirResource.getFile().getAbsoluteFile();
        } catch (IOException ex)
        {
            if (logger.isWarnEnabled())
            {
                logger.warn("Cannot search for matching files underneath " + rootDirResource + " because it does not correspond to a directory in the file system", ex);
            }
            return Collections.emptySet();
        }
        return doFindMatchingFileSystemResources(rootDir, subPattern);
    }

    /**
     * Find all resources in the file system that match the given location pattern via the Ant-style
     * PathMatcher.
     * 
     * @param rootDir
     *            the root directory in the file system
     * @param subPattern
     *            the sub pattern to match (below the root directory)
     * @return the Set of matching Resource instances
     * @throws IOException
     *             in case of I/O errors
     * @see #retrieveMatchingFiles
     * @see org.springframework.util.PathMatcher
     */
    protected Set<Resource> doFindMatchingFileSystemResources(File rootDir, String subPattern) throws IOException
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("在目录树中寻找匹配资源 [" + rootDir.getPath() + "]");
//            logger.debug("Looking for matching resources in directory tree [" + rootDir.getPath() + "]");
        }
        Set<File> matchingFiles = retrieveMatchingFiles(rootDir, subPattern);
        Set<Resource> result = new LinkedHashSet<Resource>(matchingFiles.size());
        for (File file : matchingFiles)
        {
            result.add(new FileSystemResource(file));
        }
        return result;
    }

    /**
     * Retrieve files that match the given path pattern, checking the given directory and its
     * subdirectories.
     * 
     * @param rootDir
     *            the directory to start from
     * @param pattern
     *            the pattern to match against, relative to the root directory
     * @return the Set of matching File instances
     * @throws IOException
     *             if directory contents could not be retrieved
     */
    protected Set<File> retrieveMatchingFiles(File rootDir, String pattern) throws IOException
    {
        if (!rootDir.exists())
        {
            // Silently skip non-existing directories.
            if (logger.isDebugEnabled())
            {
                logger.debug("Skipping [" + rootDir.getAbsolutePath() + "] because it does not exist");
            }
            return Collections.emptySet();
        }
        if (!rootDir.isDirectory())
        {
            // Complain louder if it exists but is no directory.
            if (logger.isWarnEnabled())
            {
                logger.warn("Skipping [" + rootDir.getAbsolutePath() + "] because it does not denote a directory");
            }
            return Collections.emptySet();
        }
        if (!rootDir.canRead())
        {
            if (logger.isWarnEnabled())
            {
                logger.warn("Cannot search for matching files underneath directory [" + rootDir.getAbsolutePath() + "] because the application is not allowed to read the directory");
            }
            return Collections.emptySet();
        }
        String fullPattern = StringUtils.replace(rootDir.getAbsolutePath(), File.separator, "/");
        if (!pattern.startsWith("/"))
        {
            fullPattern += "/";
        }
        fullPattern = fullPattern + StringUtils.replace(pattern, File.separator, "/");
        Set<File> result = new LinkedHashSet<File>(8);
        doRetrieveMatchingFiles(fullPattern, rootDir, result);
        return result;
    }

    /**
     * Recursively retrieve files that match the given pattern, adding them to the given result
     * list.
     * 
     * @param fullPattern
     *            the pattern to match against, with prepended root directory path
     * @param dir
     *            the current directory
     * @param result
     *            the Set of matching File instances to add to
     * @throws IOException
     *             if directory contents could not be retrieved
     */
    protected void doRetrieveMatchingFiles(String fullPattern, File dir, Set<File> result) throws IOException
    {
        if (logger.isDebugEnabled())
        {
//            logger.debug(" Searching directory [" + dir.getAbsolutePath() + "] for files matching pattern  [" + fullPattern + "]");
            logger.debug("搜索目录 [" + dir.getAbsolutePath() + "]匹配pattern的文件 [" + fullPattern + "]");
        }
        
        File[] dirContents = dir.listFiles();
        if (dirContents == null)
        {
            if (logger.isWarnEnabled())
            {
                logger.warn("无法检索目录内容 [" + dir.getAbsolutePath() + "]");
            }
            return;
        }
        for (File content : dirContents)
        {
            String currPath = StringUtils.replace(content.getAbsolutePath(), File.separator, "/");
            if (content.isDirectory() && getPathMatcher().matchStart(fullPattern, currPath + "/"))
            {
                if (!content.canRead())
                {
                    if (logger.isDebugEnabled())
                    {
                        logger.debug("Skipping subdirectory [" + dir.getAbsolutePath() + "] because the application is not allowed to read the directory");
                    }
                } else
                {
                    doRetrieveMatchingFiles(fullPattern, content, result);
                }
            }
            if (getPathMatcher().match(fullPattern, currPath))
            {
                result.add(content);
            }
        }
    }

    /**
     * Determine the root directory for the given location.
     * <p>
     * Used for determining the starting point for file matching, resolving the root directory
     * location to a {@code java.io.File} and passing it into {@code retrieveMatchingFiles}, with
     * the remainder of the location as pattern.
     * <p>
     * Will return "/WEB-INF/" for the pattern "/WEB-INF/*.xml", for example.
     * 
     * @param location
     *            the location to check
     * @return the part of the location that denotes the root directory
     * @see #retrieveMatchingFiles
     */
    protected String determineRootDir(String location)
    {
        int prefixEnd = location.indexOf(":") + 1;
        int rootDirEnd = location.length();
        while (rootDirEnd > prefixEnd && getPathMatcher().isPattern(location.substring(prefixEnd, rootDirEnd)))
        {
            rootDirEnd = location.lastIndexOf('/', rootDirEnd - 2) + 1;
        }
        if (rootDirEnd == 0)
        {
            rootDirEnd = prefixEnd;
        }
        return location.substring(0, rootDirEnd);
    }

    /**
     * Resolve the specified resource for path matching.
     * <p>
     * The default implementation detects an Equinox OSGi "bundleresource:" / "bundleentry:" URL and
     * resolves it into a standard jar file URL that can be traversed using Spring's standard jar
     * file traversal algorithm.
     * 
     * @param original
     *            the resource to resolve
     * @return the resolved resource (may be identical to the passed-in resource)
     * @throws IOException
     *             in case of resolution failure
     */
    protected Resource resolveRootDirResource(Resource original) throws IOException
    {
        if (equinoxResolveMethod != null)
        {
            URL url = original.getURL();
            if (url.getProtocol().startsWith("bundle"))
            {
                return new UrlResource((URL) ReflectionUtils.invokeMethod(equinoxResolveMethod, null, url));
            }
        }
        return original;
    }
}
