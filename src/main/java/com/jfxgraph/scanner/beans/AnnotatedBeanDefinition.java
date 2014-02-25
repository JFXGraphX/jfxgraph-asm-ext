package com.jfxgraph.scanner.beans;

import com.jfxgraph.scanner.type.AnnotationMetadata;

/**
 * AnnotatedBeanDefinition
 * @author    Albert
 * @version   $Id: AnnotatedBeanDefinition.java,v0.5 2013年10月27日 下午9:44:53 Albert Exp .
 * @since   3.1
 */
public interface AnnotatedBeanDefinition extends BeanDefinition
{
    /**
     * Obtain the annotation metadata (as well as basic class metadata)
     * for this bean definition's bean class.
     * @return the annotation metadata object (never {@code null})
     */
    AnnotationMetadata getMetadata();
}
