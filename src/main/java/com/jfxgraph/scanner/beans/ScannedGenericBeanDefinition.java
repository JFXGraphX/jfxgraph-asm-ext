package com.jfxgraph.scanner.beans;

import com.jfxgraph.scanner.reader.MetadataReader;
import com.jfxgraph.scanner.type.AnnotationMetadata;
import com.jfxgraph.scanner.util.Assert;

/**
 * ScannedGenericBeanDefinition。
 * 
 * @author Albert
 * @since 1.0
 */
public class ScannedGenericBeanDefinition  implements AnnotatedBeanDefinition
{
    private final AnnotationMetadata metadata;
    
    private String beanClassName;

    /**
     * Create a new ScannedGenericBeanDefinition for the class that the given MetadataReader describes.
     * 
     * @param metadataReader
     *            the MetadataReader for the scanned target class
     */
    public ScannedGenericBeanDefinition(MetadataReader metadataReader)
    {
        Assert.notNull(metadataReader, "MetadataReader must not be null");
        this.metadata = metadataReader.getAnnotationMetadata();
        setBeanClassName(this.metadata.getClassName());
    }

    /**
     * @see com.jfxgraph.scanner.beans.AnnotatedBeanDefinition#getMetadata()
     */
    @Override
    public AnnotationMetadata getMetadata()
    {
        return this.metadata;
    }

    @Override
    public String getBeanClassName()
    {
        return this.beanClassName;
    }

    @Override
    public void setBeanClassName(String beanClassName)
    {
        this.beanClassName = beanClassName;
    }
}
