package com.pxccn.PxcDali2.common;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.function.IntFunction;
import java.util.function.Predicate;

public class ArrayUtil {
    public ArrayUtil() {
    }

    public static int indexOf(Object[] array, Object x) {
        for (int i = 0; i < array.length; ++i) {
            if (array[i] == x) {
                return i;
            }
        }

        return -1;
    }

    public static <T> T[] grow(T[] array, int length) {
        if (array.length < length) {
            Class<?> cls = array.getClass().getComponentType();
            int len = Math.max(array.length * 2, length);
            T[] temp = (T[]) Array.newInstance(cls, len);
            System.arraycopy(array, 0, temp, 0, array.length);
            array = temp;
        }

        return array;
    }

    public static <T> T[] put(T[] array, int index, T elem) {
        array = grow(array, index + 1);
        array[index] = elem;
        return array;
    }

    public static boolean remove(Object[] array, int size, Object elem) {
        for (int i = 0; i < size; ++i) {
            if (array[i] == elem) {
                if (i < array.length) {
                    System.arraycopy(array, i + 1, array, i, array.length - i - 1);
                }

                array[size - 1] = null;
                return true;
            }
        }

        return false;
    }

    public static <T> T[] addOne(T[] array, T elem) {
        Class<?> cls = array.getClass().getComponentType();
        T[] temp = (T[]) Array.newInstance(cls, array.length + 1);
        System.arraycopy(array, 0, temp, 0, array.length);
        temp[array.length] = elem;
        return temp;
    }

    public static <T> T[] removeOne(T[] array, T elem) {
        int len = array.length;

        for (int i = 0; i < len; ++i) {
            if (array[i] == elem) {
                return removeOne(array, i);
            }
        }

        throw new IllegalStateException();
    }

    public static <T> T[] removeOne(T[] array, int index) {
        Class<?> cls = array.getClass().getComponentType();
        T[] temp = (T[]) Array.newInstance(cls, array.length - 1);
        System.arraycopy(array, 0, temp, 0, index);
        System.arraycopy(array, index + 1, temp, index, array.length - index - 1);
        return temp;
    }

    public static <T> T[] toTop(T[] a, int index) {
        ArrayList<T> v = new ArrayList();

        for (int i = 0; i < a.length; ++i) {
            if (i != index) {
                v.add(a[i]);
            }
        }

        v.add(0, a[index]);
        return v.toArray(a);
    }

    public static <T> T[] toBottom(T[] a, int index) {
        ArrayList<T> v = new ArrayList();

        for (int i = 0; i < a.length; ++i) {
            if (i != index) {
                v.add(a[i]);
            }
        }

        v.add(a[index]);
        return v.toArray(a);
    }

    public static <T> T[] arrayFromCollection(Collection<T> collection, IntFunction<T[]> generator) {
        return collection.toArray(generator.apply(collection.size()));
    }

    public static <T> boolean testAny(T[] array, Predicate<T> predicate) {
        Objects.requireNonNull(array);
        Objects.requireNonNull(predicate);
        int var3 = array.length;

        for (T item : array) {
            if (predicate.test(item)) {
                return true;
            }
        }

        return false;
    }

    public static <T> boolean testAll(T[] array, Predicate<T> predicate) {
        Objects.requireNonNull(array);
        Objects.requireNonNull(predicate);
        if (array.length == 0) {
            return false;
        } else {
            int var3 = array.length;

            for (T item : array) {
                if (!predicate.test(item)) {
                    return false;
                }
            }

            return true;
        }
    }

    public static int indexOf(int[] array, int x) {
        for (int i = 0; i < array.length; ++i) {
            if (array[i] == x) {
                return i;
            }
        }

        return -1;
    }

    public static int[] grow(int[] array, int length) {
        if (array.length < length) {
            int len = Math.max(array.length * 2, length);
            int[] temp = new int[len];
            System.arraycopy(array, 0, temp, 0, array.length);
            array = temp;
        }

        return array;
    }

    public static int[] put(int[] array, int index, int elem) {
        array = grow(array, index + 1);
        array[index] = elem;
        return array;
    }

    public static boolean remove(int[] array, int size, int elem) {
        for (int i = 0; i < size; ++i) {
            if (array[i] == elem) {
                if (i < array.length) {
                    System.arraycopy(array, i + 1, array, i, array.length - i - 1);
                }

                array[size - 1] = -1;
                return true;
            }
        }

        return false;
    }

    public static int[] addOne(int[] array, int elem) {
        int[] temp = new int[array.length + 1];
        System.arraycopy(array, 0, temp, 0, array.length);
        temp[array.length] = elem;
        return temp;
    }

    public static int[] removeOne(int[] array, int elem) {
        int[] temp = new int[array.length - 1];
        int len = array.length;

        for (int i = 0; i < len; ++i) {
            if (array[i] == elem) {
                System.arraycopy(array, 0, temp, 0, i);
                System.arraycopy(array, i + 1, temp, i, len - i - 1);
                return temp;
            }
        }

        throw new IllegalStateException();
    }

    public static int[] removeOneIndex(int[] array, int index) {
        int[] temp = new int[array.length - 1];
        System.arraycopy(array, 0, temp, 0, index);
        System.arraycopy(array, index + 1, temp, index, array.length - index - 1);
        return temp;
    }

    public static void sort(int[] array) {
        int n = array.length;

        for (int incr = n / 2; incr >= 1; incr /= 2) {
            for (int i = incr; i < n; ++i) {
                int temp = array[i];

                int j;
                for (j = i; j >= incr && temp < array[j - incr]; j -= incr) {
                    array[j] = array[j - incr];
                }

                array[j] = temp;
            }
        }

    }

    public static int binarySearch(int[] array, int value) {
        int n = array.length;
        int lo = -1;
        int hi = n;

        while (hi - lo > 1) {
            int i = (hi + lo) / 2;
            int a = array[i];
            if (a > value) {
                hi = i;
            } else {
                if (a >= value) {
                    return i;
                }

                lo = i;
            }
        }

        return -1;
    }

    public static int[] orderCircular(int[] array, int next) {
        int len = array.length;
        int[] result = new int[len];
        int numOld = len - next;
        if (numOld > 0) {
            System.arraycopy(array, next, result, 0, numOld);
        }

        if (next > 0) {
            System.arraycopy(array, 0, result, numOld, next);
        }

        return result;
    }

    public static <T> T[] add(T[] a, T[] b) {
        Class<?> cls = a.getClass().getComponentType();
        T[] x = (T[]) Array.newInstance(cls, a.length + b.length);
        System.arraycopy(a, 0, x, 0, a.length);
        System.arraycopy(b, 0, x, a.length, b.length);
        return x;
    }

    public static String join(Object[] a, String delimiter) {
        if (a != null && a.length > 0) {
            StringBuilder str = new StringBuilder();

            for (int i = 0; i < a.length; ++i) {
                if (i > 0) {
                    str.append(delimiter);
                }

                str.append(a[i].toString());
            }

            return str.toString();
        } else {
            return "";
        }
    }
}
