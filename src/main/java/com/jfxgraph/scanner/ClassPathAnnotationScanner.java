package com.jfxgraph.scanner;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jfxgraph.scanner.beans.AnnotatedBeanDefinition;
import com.jfxgraph.scanner.beans.ScannedGenericBeanDefinition;
import com.jfxgraph.scanner.io.Resource;
import com.jfxgraph.scanner.io.loader.PathMatchingResourcePatternResolver;
import com.jfxgraph.scanner.io.loader.ResourcePatternResolver;
import com.jfxgraph.scanner.reader.CachingMetadataReaderFactory;
import com.jfxgraph.scanner.reader.MetadataReader;
import com.jfxgraph.scanner.reader.MetadataReaderFactory;
import com.jfxgraph.scanner.type.filter.TypeFilter;
import com.jfxgraph.scanner.util.ClassUtils;

/**
 * ClassPath下的Annotation扫描器. 特殊描述:
 * 
 * @author Albert
 * @since 1.0
 */
public class ClassPathAnnotationScanner
{
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    static final String DEFAULT_RESOURCE_PATTERN = "**/*.class";

    private String resourcePattern = DEFAULT_RESOURCE_PATTERN;

    // 路径解析器.
    private ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();

    // 元数据读写工厂
    private MetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory(this.resourcePatternResolver);

    private final List<TypeFilter> includeFilters = new LinkedList<TypeFilter>();

    private final List<TypeFilter> excludeFilters = new LinkedList<TypeFilter>();
    
    /**
     * Add an include type filter to the <i>end</i> of the inclusion list.
     */
    public void addIncludeFilter(TypeFilter includeFilter) 
    {
        this.includeFilters.add(includeFilter);
    }

    /**
     * 获取baskPackage包下的所有注解元数据.
     * 
     * @see {@link SimpleMetadataReader}
     * @param basePackage
     */
    public Set<ScannedGenericBeanDefinition> findAnnotationDefinition(String basePackage)
    {
        // 准备容器进行转移读取到的元数据信息.
        Set<ScannedGenericBeanDefinition> candidates = new LinkedHashSet<ScannedGenericBeanDefinition>();

        String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + resolveBasePackage(basePackage) + "/" + this.resourcePattern;
        // 解析为搜索的路径
        try
        {
            // classpath*:com/epichust/mestar/**/*.class
            Resource[] resources = this.resourcePatternResolver.getResources(packageSearchPath);

            boolean traceEnabled = logger.isTraceEnabled();
            boolean debugEnabled = logger.isDebugEnabled();

            for (Resource resource : resources)
            {
                if (resource.isReadable())
                {
                    // 返回SimpleMetadataReader实例.
                    MetadataReader metadataReader = this.metadataReaderFactory.getMetadataReader(resource);

                    if (isCandidateComponent(metadataReader))
                    {
                        // 通过metaDataReader生成对应的使用实例
                        ScannedGenericBeanDefinition sbd = new ScannedGenericBeanDefinition(metadataReader);

                        if (isCandidateComponent(sbd))
                        {
                            candidates.add(sbd);
                        } else
                        {
                            if (debugEnabled)
                            {
                                logger.debug("因为不是具体的顶级类，被忽略资源文件: " + resource);
                            }
                        }
                    } else
                    {
                        if (traceEnabled)
                        {
                            logger.trace("不匹配任何Annotation筛选器，被忽略: " + resource);
                        }
                    }
                } else
                {
                    if (traceEnabled)
                    {
                        logger.trace("资源不可读，被忽略: " + resource);
                    }
                }
            }
        } catch (IOException ex)
        {
            throw new RuntimeException("在扫描类路径时I/O发生错误.", ex);
        }

        return candidates;
    }

    /**
     * 是不是候选组件。 // 读取外部配置.Spring中提供支持Annotation类型的比较/Aspet
     * 
     * @param metadataReader
     * @return
     * @throws IOException
     */
    protected boolean isCandidateComponent(MetadataReader metadataReader) throws IOException
    {
        //排除.
        for (TypeFilter tf : this.excludeFilters)
        {
            if (tf.match(metadataReader, this.metadataReaderFactory))
            {
                return false;
            }
        }
        
        for (TypeFilter tf : this.includeFilters)
        {
            if (tf.match(metadataReader, this.metadataReaderFactory))
            {
                logger.debug(metadataReader.getClassMetadata().getClassName()+"<-->"+metadataReader.getAnnotationMetadata().getAnnotationTypes());
                return true;
            }
        }
        return false;
    }

    /**
     * 是不是独立的类.
     * 
     * @param AnnotatedBeanDefinition
     * @return
     * @throws IOException
     */
    protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) throws IOException
    {
        return (beanDefinition.getMetadata().isConcrete() && beanDefinition.getMetadata().isIndependent());
    }

    /**
     * 将基础包转化为资源路径。
     * 
     * @param basePackage
     *            由用户指定的基础包
     * @return the pattern specification to be used for package searching
     */
    protected String resolveBasePackage(String basePackage)
    {
        return ClassUtils.convertClassNameToResourcePath(basePackage);
    }
}
