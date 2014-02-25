package com.jfxgraph.scanner.util;

import java.lang.reflect.Array;
import java.util.Arrays;

/**
 * ObjectUtils
 * 
 * @author Albert
 * @since 1.0
 */
public abstract class ObjectUtils
{
    private static final int INITIAL_HASH = 7;
    private static final int MULTIPLIER = 31;

    private static final String EMPTY_STRING = "";
    private static final String NULL_STRING = "null";

    /**
     * Determine whether the given array is empty: i.e. {@code null} or of zero length.
     * 
     * @param array
     *            the array to check
     */
    public static boolean isEmpty(Object[] array)
    {
        return (array == null || array.length == 0);
    }

    /**
     * Append the given object to the given array, returning a new array consisting of the input array contents plus the
     * given object.
     * 
     * @param array
     *            the array to append to (can be {@code null})
     * @param obj
     *            the object to append
     * @return the new array (of the same component type; never {@code null})
     */
    public static <A, O extends A> A[] addObjectToArray(A[] array, O obj)
    {
        Class<?> compType = Object.class;
        if (array != null)
        {
            compType = array.getClass().getComponentType();
        } else if (obj != null)
        {
            compType = obj.getClass();
        }
        int newArrLength = (array != null ? array.length + 1 : 1);
        @SuppressWarnings("unchecked")
        A[] newArr = (A[]) Array.newInstance(compType, newArrLength);
        if (array != null)
        {
            System.arraycopy(array, 0, newArr, 0, array.length);
        }
        newArr[newArr.length - 1] = obj;
        return newArr;
    }

    /**
     * Return a String representation of the specified Object.
     * <p>
     * Builds a String representation of the contents in case of an array. Returns {@code "null"} if {@code obj} is
     * {@code null}.
     * 
     * @param obj
     *            the object to build a String representation for
     * @return a String representation of {@code obj}
     */
    public static String nullSafeToString(Object obj)
    {
        if (obj == null)
        {
            return NULL_STRING;
        }
        if (obj instanceof String)
        {
            return (String) obj;
        }
        if (obj instanceof Object[])
        {
            return nullSafeToString((Object[]) obj);
        }
        if (obj instanceof boolean[])
        {
            return nullSafeToString((boolean[]) obj);
        }
        if (obj instanceof byte[])
        {
            return nullSafeToString((byte[]) obj);
        }
        if (obj instanceof char[])
        {
            return nullSafeToString((char[]) obj);
        }
        if (obj instanceof double[])
        {
            return nullSafeToString((double[]) obj);
        }
        if (obj instanceof float[])
        {
            return nullSafeToString((float[]) obj);
        }
        if (obj instanceof int[])
        {
            return nullSafeToString((int[]) obj);
        }
        if (obj instanceof long[])
        {
            return nullSafeToString((long[]) obj);
        }
        if (obj instanceof short[])
        {
            return nullSafeToString((short[]) obj);
        }
        String str = obj.toString();
        return (str != null ? str : EMPTY_STRING);
    }

    // ---------------------------------------------------------------------
    // Convenience methods for content-based equality/hash-code handling
    // ---------------------------------------------------------------------

    /**
     * Determine if the given objects are equal, returning {@code true} if both are {@code null} or {@code false} if
     * only one is {@code null}.
     * <p>
     * Compares arrays with {@code Arrays.equals}, performing an equality check based on the array elements rather than
     * the array reference.
     * 
     * @param o1
     *            first Object to compare
     * @param o2
     *            second Object to compare
     * @return whether the given objects are equal
     * @see java.util.Arrays#equals
     */
    public static boolean nullSafeEquals(Object o1, Object o2)
    {
        if (o1 == o2)
        {
            return true;
        }
        if (o1 == null || o2 == null)
        {
            return false;
        }
        if (o1.equals(o2))
        {
            return true;
        }
        if (o1.getClass().isArray() && o2.getClass().isArray())
        {
            if (o1 instanceof Object[] && o2 instanceof Object[])
            {
                return Arrays.equals((Object[]) o1, (Object[]) o2);
            }
            if (o1 instanceof boolean[] && o2 instanceof boolean[])
            {
                return Arrays.equals((boolean[]) o1, (boolean[]) o2);
            }
            if (o1 instanceof byte[] && o2 instanceof byte[])
            {
                return Arrays.equals((byte[]) o1, (byte[]) o2);
            }
            if (o1 instanceof char[] && o2 instanceof char[])
            {
                return Arrays.equals((char[]) o1, (char[]) o2);
            }
            if (o1 instanceof double[] && o2 instanceof double[])
            {
                return Arrays.equals((double[]) o1, (double[]) o2);
            }
            if (o1 instanceof float[] && o2 instanceof float[])
            {
                return Arrays.equals((float[]) o1, (float[]) o2);
            }
            if (o1 instanceof int[] && o2 instanceof int[])
            {
                return Arrays.equals((int[]) o1, (int[]) o2);
            }
            if (o1 instanceof long[] && o2 instanceof long[])
            {
                return Arrays.equals((long[]) o1, (long[]) o2);
            }
            if (o1 instanceof short[] && o2 instanceof short[])
            {
                return Arrays.equals((short[]) o1, (short[]) o2);
            }
        }
        return false;
    }

    /**
     * Return as hash code for the given object; typically the value of {@code {@link Object#hashCode()} . If the
     * object is an array, this method will delegate to any of the {@code nullSafeHashCode} methods for arrays in this
     * class. If the object is {@code null}, this method returns 0.
     * 
     * @see #nullSafeHashCode(Object[])
     * @see #nullSafeHashCode(boolean[])
     * @see #nullSafeHashCode(byte[])
     * @see #nullSafeHashCode(char[])
     * @see #nullSafeHashCode(double[])
     * @see #nullSafeHashCode(float[])
     * @see #nullSafeHashCode(int[])
     * @see #nullSafeHashCode(long[])
     * @see #nullSafeHashCode(short[])
     */
    public static int nullSafeHashCode(Object obj)
    {
        if (obj == null)
        {
            return 0;
        }
        if (obj.getClass().isArray())
        {
            if (obj instanceof Object[])
            {
                return nullSafeHashCode((Object[]) obj);
            }
            if (obj instanceof boolean[])
            {
                return nullSafeHashCode((boolean[]) obj);
            }
            if (obj instanceof byte[])
            {
                return nullSafeHashCode((byte[]) obj);
            }
            if (obj instanceof char[])
            {
                return nullSafeHashCode((char[]) obj);
            }
            if (obj instanceof double[])
            {
                return nullSafeHashCode((double[]) obj);
            }
            if (obj instanceof float[])
            {
                return nullSafeHashCode((float[]) obj);
            }
            if (obj instanceof int[])
            {
                return nullSafeHashCode((int[]) obj);
            }
            if (obj instanceof long[])
            {
                return nullSafeHashCode((long[]) obj);
            }
            if (obj instanceof short[])
            {
                return nullSafeHashCode((short[]) obj);
            }
        }
        return obj.hashCode();
    }

    /**
     * Return a hash code based on the contents of the specified array. If {@code array} is {@code null}, this method
     * returns 0.
     */
    public static int nullSafeHashCode(Object[] array)
    {
        if (array == null)
        {
            return 0;
        }
        int hash = INITIAL_HASH;
        int arraySize = array.length;
        for (int i = 0; i < arraySize; i++)
        {
            hash = MULTIPLIER * hash + nullSafeHashCode(array[i]);
        }
        return hash;
    }

    /**
     * Return a hash code based on the contents of the specified array. If {@code array} is {@code null}, this method
     * returns 0.
     */
    public static int nullSafeHashCode(boolean[] array)
    {
        if (array == null)
        {
            return 0;
        }
        int hash = INITIAL_HASH;
        int arraySize = array.length;
        for (int i = 0; i < arraySize; i++)
        {
            hash = MULTIPLIER * hash + hashCode(array[i]);
        }
        return hash;
    }

    /**
     * Return a hash code based on the contents of the specified array. If {@code array} is {@code null}, this method
     * returns 0.
     */
    public static int nullSafeHashCode(byte[] array)
    {
        if (array == null)
        {
            return 0;
        }
        int hash = INITIAL_HASH;
        int arraySize = array.length;
        for (int i = 0; i < arraySize; i++)
        {
            hash = MULTIPLIER * hash + array[i];
        }
        return hash;
    }

    /**
     * Return a hash code based on the contents of the specified array. If {@code array} is {@code null}, this method
     * returns 0.
     */
    public static int nullSafeHashCode(char[] array)
    {
        if (array == null)
        {
            return 0;
        }
        int hash = INITIAL_HASH;
        int arraySize = array.length;
        for (int i = 0; i < arraySize; i++)
        {
            hash = MULTIPLIER * hash + array[i];
        }
        return hash;
    }

    /**
     * Return a hash code based on the contents of the specified array. If {@code array} is {@code null}, this method
     * returns 0.
     */
    public static int nullSafeHashCode(double[] array)
    {
        if (array == null)
        {
            return 0;
        }
        int hash = INITIAL_HASH;
        int arraySize = array.length;
        for (int i = 0; i < arraySize; i++)
        {
            hash = MULTIPLIER * hash + hashCode(array[i]);
        }
        return hash;
    }

    /**
     * Return a hash code based on the contents of the specified array. If {@code array} is {@code null}, this method
     * returns 0.
     */
    public static int nullSafeHashCode(float[] array)
    {
        if (array == null)
        {
            return 0;
        }
        int hash = INITIAL_HASH;
        int arraySize = array.length;
        for (int i = 0; i < arraySize; i++)
        {
            hash = MULTIPLIER * hash + hashCode(array[i]);
        }
        return hash;
    }

    /**
     * Return a hash code based on the contents of the specified array. If {@code array} is {@code null}, this method
     * returns 0.
     */
    public static int nullSafeHashCode(int[] array)
    {
        if (array == null)
        {
            return 0;
        }
        int hash = INITIAL_HASH;
        int arraySize = array.length;
        for (int i = 0; i < arraySize; i++)
        {
            hash = MULTIPLIER * hash + array[i];
        }
        return hash;
    }

    /**
     * Return a hash code based on the contents of the specified array. If {@code array} is {@code null}, this method
     * returns 0.
     */
    public static int nullSafeHashCode(long[] array)
    {
        if (array == null)
        {
            return 0;
        }
        int hash = INITIAL_HASH;
        int arraySize = array.length;
        for (int i = 0; i < arraySize; i++)
        {
            hash = MULTIPLIER * hash + hashCode(array[i]);
        }
        return hash;
    }

    /**
     * Return a hash code based on the contents of the specified array. If {@code array} is {@code null}, this method
     * returns 0.
     */
    public static int nullSafeHashCode(short[] array)
    {
        if (array == null)
        {
            return 0;
        }
        int hash = INITIAL_HASH;
        int arraySize = array.length;
        for (int i = 0; i < arraySize; i++)
        {
            hash = MULTIPLIER * hash + array[i];
        }
        return hash;
    }

    /**
     * Return the same value as {@link Boolean#hashCode()} .
     * 
     * @see Boolean#hashCode()
     */
    public static int hashCode(boolean bool)
    {
        return bool ? 1231 : 1237;
    }

    /**
     * Return the same value as {@link Double#hashCode()} .
     * 
     * @see Double#hashCode()
     */
    public static int hashCode(double dbl)
    {
        long bits = Double.doubleToLongBits(dbl);
        return hashCode(bits);
    }

    /**
     * Return the same value as {@link Float#hashCode()} .
     * 
     * @see Float#hashCode()
     */
    public static int hashCode(float flt)
    {
        return Float.floatToIntBits(flt);
    }

    /**
     * Return the same value as {@link Long#hashCode()} .
     * 
     * @see Long#hashCode()
     */
    public static int hashCode(long lng)
    {
        return (int) (lng ^ (lng >>> 32));
    }
}
