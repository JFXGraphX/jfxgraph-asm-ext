package com.jfxgraph.scanner.util;

import static java.lang.String.format;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import com.jfxgraph.scanner.util.Assert;
import com.jfxgraph.scanner.util.StringUtils;

/**
 * 注解属性集合。
 * 
 * @author Albert
 * @version $Id: AnnotationAttributes.java,v0.5 2013年10月27日 下午4:13:03 Albert Exp .
 * @since 3.1
 */
public class AnnotationAttributes extends LinkedHashMap<String, Object>
{
    private static final long serialVersionUID = -3867748830073758556L;

    /**
     * Create a new, empty {@link AnnotationAttributes} instance.
     */
    public AnnotationAttributes()
    {
    }

    /**
     * Create a new, empty {@link AnnotationAttributes} instance with the given initial capacity to optimize
     * performance.
     * 
     * @param initialCapacity
     *            initial size of the underlying map
     */
    public AnnotationAttributes(int initialCapacity)
    {
        super(initialCapacity);
    }

    /**
     * Create a new {@link AnnotationAttributes} instance, wrapping the provided map and all its key/value pairs.
     * 
     * @param map
     *            original source of annotation attribute key/value pairs to wrap
     * @see #fromMap(Map)
     */
    public AnnotationAttributes(Map<String, Object> map)
    {
        super(map);
    }

    /**
     * Return an {@link AnnotationAttributes} instance based on the given map; if the map is already an
     * {@code AnnotationAttributes} instance, it is casted and returned immediately without creating any new instance;
     * otherwise create a new instance by wrapping the map with the {@link #AnnotationAttributes(Map)} constructor.
     * 
     * @param map
     *            original source of annotation attribute key/value pairs
     */
    public static AnnotationAttributes fromMap(Map<String, Object> map)
    {
        if (map == null)
        {
            return null;
        }

        if (map instanceof AnnotationAttributes)
        {
            return (AnnotationAttributes) map;
        }

        return new AnnotationAttributes(map);
    }

    public String getString(String attributeName)
    {
        return doGet(attributeName, String.class);
    }

    public String[] getStringArray(String attributeName)
    {
        return doGet(attributeName, String[].class);
    }

    public boolean getBoolean(String attributeName)
    {
        return doGet(attributeName, Boolean.class);
    }

    @SuppressWarnings("unchecked")
    public <N extends Number> N getNumber(String attributeName)
    {
        return (N) doGet(attributeName, Integer.class);
    }

    @SuppressWarnings("unchecked")
    public <E extends Enum<?>> E getEnum(String attributeName)
    {
        return (E) doGet(attributeName, Enum.class);
    }

    @SuppressWarnings("unchecked")
    public <T> Class<? extends T> getClass(String attributeName)
    {
        return doGet(attributeName, Class.class);
    }

    public Class<?>[] getClassArray(String attributeName)
    {
        return doGet(attributeName, Class[].class);
    }

    public AnnotationAttributes getAnnotation(String attributeName)
    {
        return doGet(attributeName, AnnotationAttributes.class);
    }

    public AnnotationAttributes[] getAnnotationArray(String attributeName)
    {
        return doGet(attributeName, AnnotationAttributes[].class);
    }

    @SuppressWarnings("unchecked")
    private <T> T doGet(String attributeName, Class<T> expectedType)
    {
        Assert.hasText(attributeName, "attributeName must not be null or empty");
        Object value = this.get(attributeName);
        Assert.notNull(value, format("Attribute '%s' not found", attributeName));
        Assert.isAssignable(expectedType, value.getClass(),
                format("Attribute '%s' is of type [%s], but [%s] was expected. Cause: ", attributeName, value.getClass().getSimpleName(), expectedType.getSimpleName()));
        return (T) value;
    }

    public String toString()
    {
        Iterator<Map.Entry<String, Object>> entries = entrySet().iterator();
        StringBuilder sb = new StringBuilder("{");
        while (entries.hasNext())
        {
            Map.Entry<String, Object> entry = entries.next();
            sb.append(entry.getKey());
            sb.append('=');
            sb.append(valueToString(entry.getValue()));
            sb.append(entries.hasNext() ? ", " : "");
        }
        sb.append("}");
        return sb.toString();
    }

    private String valueToString(Object value)
    {
        if (value == this)
        {
            return "(this Map)";
        }
        if (value instanceof Object[])
        {
            return "[" + StringUtils.arrayToCommaDelimitedString((Object[]) value) + "]";
        }
        return String.valueOf(value);
    }
}
