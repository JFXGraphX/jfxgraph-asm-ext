package com.jfxgraph.scanner.io.loader;

import java.io.IOException;

import com.jfxgraph.scanner.io.Resource;

/**
 * Strategy interface for resolving a location pattern (for example, an Ant-style path pattern) into Resource objects.
 * 
 * 
 * analysis into clear-cut components 解析过程 定位模式(Ant 样式路径模式) 到资源对象 的策略接口.
 * 
 * <p>
 * This is an extension to the {@link org.springframework.core.io.ResourceLoader} interface. A passed-in ResourceLoader
 * (for example, an {@link org.springframework.context.ApplicationContext} passed in via
 * {@link org.springframework.context.ResourceLoaderAware} when running in a context) can be checked whether it
 * implements this extended interface too. 
 * 传入的资源加载程序 可以检查它是否实现了这个扩展接口。 在一个上下文运行过程中 ApplicationContext 通过
 * ResourceLoaderAware 传入.
 * 
 * <p>
 * {@link PathMatchingResourcePatternResolver} is a standalone implementation that is usable outside an
 * ApplicationContext, also used by {@link ResourceArrayPropertyEditor} for populating Resource array bean properties.
 * 
 * <p>
 * Can be used with any sort of location pattern (e.g. "/WEB-INF/*-context.xml"): Input patterns have to match the
 * strategy implementation. This interface just specifies the conversion method rather than a specific pattern format.
 * 
 * <p>
 * This interface also suggests a new resource prefix "classpath*:" for all matching resources from the class path. Note
 * that the resource location is expected to be a path without placeholders in this case (e.g. "/beans.xml"); JAR files
 * or classes directories can contain multiple files of the same name.
 * 
 * @author Juergen Hoeller
 * @since 1.0.2
 */
public interface ResourcePatternResolver extends ResourceLoader
{

    /**
     * Pseudo URL prefix for all matching resources from the class path: "classpath*:" This differs from
     * ResourceLoader's classpath URL prefix in that it retrieves all matching resources for a given name (e.g.
     * "/beans.xml"), for example in the root of all deployed JAR files.
     * <p>
     * 伪 URL 前缀的类路径中的所有匹配资源："类路径 *："这不同于 ResourceLoader 的类路径中的 URL 前缀，它检索一个给定的名称匹配的所有资源 （例如"豆类。xml"），例如在根中的所有部署的 JAR 文件。
     */
    String CLASSPATH_ALL_URL_PREFIX = "classpath*:";

    /**
     * Resolve the given location pattern into Resource objects.
     * <p>
     * Overlapping resource entries that point to the same physical resource should be avoided, as far as possible. The
     * result should have set semantics.
     * 
     * @param locationPattern
     *            the location pattern to resolve
     * @return the corresponding Resource objects
     * @throws IOException
     *             in case of I/O errors
     */
    Resource[] getResources(String locationPattern) throws IOException;

}
