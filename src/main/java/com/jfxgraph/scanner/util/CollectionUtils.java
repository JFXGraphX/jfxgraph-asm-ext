/**
 * Copyright (c) 2012-2013,Epic-HUST Technology(Wuhan)Co.,Ltd. All Rights Reserved.
 */
package com.jfxgraph.scanner.util;

import java.util.Collection;
import java.util.Map;

/**
 * 集合相关的工具类.
 * @author    Albert
 * @version   $Id: CollectionUtils.java,v0.5 2013年10月27日 下午4:23:42 Albert Exp .
 * @since   3.1
 */
public abstract class CollectionUtils
{
    /**
     * Return {@code true} if the supplied Collection is {@code null} or empty.
     * Otherwise, return {@code false}.
     * 
     * @param collection
     *            the Collection to check
     * @return whether the given Collection is empty
     */
    public static boolean isEmpty(Collection<?> collection) {
        return (collection == null || collection.isEmpty());
    }

    /**
     * Return {@code true} if the supplied Map is {@code null} or empty.
     * Otherwise, return {@code false}.
     * 
     * @param map
     *            the Map to check
     * @return whether the given Map is empty
     */
    public static boolean isEmpty(Map<?, ?> map) {
        return (map == null || map.isEmpty());
    }
}
