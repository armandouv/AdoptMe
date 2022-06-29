package com.adoptme.pets;

import java.util.Comparator;
import java.util.List;

/**
 * This class implements arbitrary lists sorting using the QuickSort algorithm. A Comparator must be
 * provided for the type of elements in the list.
 */
public class Sorting {
    public static <T> void sort(List<T> list, Comparator<T> comparator) {
        quickSort(list, comparator, 0, list.size() - 1);
    }

    private static <T> void quickSort(List<T> list, Comparator<T> comparator, int low, int high) {
        if (low < high) {
            // Partition list with respect to a random element
            int pivot = partition(list, comparator, low, high);

            // The partitioned element is at the right place in the list. Sort recursively both halves.
            quickSort(list, comparator, low, pivot - 1);
            quickSort(list, comparator, pivot + 1, high);
        }
    }

    private static <T> int partition(List<T> list, Comparator<T> comparator, int low, int high) {
        // Pick the last element as the element to partition.
        T pivot = list.get(high);
        int i = low - 1;

        // Partition
        for (int j = low; j < high; j++) {
            if (isLessThanOrEqual(list.get(j), pivot, comparator)) {
                i++;

                T tmp = list.get(i);
                list.set(i, list.get(j));
                list.set(j, tmp);
            }
        }

        T tmp = list.get(i + 1);
        list.set(i + 1, list.get(high));
        list.set(high, tmp);

        return i + 1;
    }

    private static <T> boolean isLessThanOrEqual(T e1, T e2, Comparator<T> comparator) {
        int compareValue = comparator.compare(e1, e2);
        return compareValue <= 0;
    }
}
