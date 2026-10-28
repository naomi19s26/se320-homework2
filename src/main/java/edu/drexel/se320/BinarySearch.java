package edu.drexel.se320;

import java.util.NoSuchElementException;

public class BinarySearch {

    // DO NOT MODIFY THIS SIGNATURE
    // This includes the protected modifier; the autograder currently relies
    // on a combination of overloading and visibility hacks to swap out your
    // code at runtime to test your test suite.
    protected static <T extends Comparable<T>> int binarySearchImplementation(T[] array, T elem) {
	// TODO: Copy over your HW1 implementation of binary search here
         if(array == null || elem == null){
            throw new IllegalArgumentException("Array and target element should not be null");
        }
        int length = array.length;
        if(length == 0){
            throw new IllegalArgumentException("Array should not be empty");
        }
        int left = 0;
        int right = length -1;
        while(left <= right){
            int index = left + (right - left)/2;
            if(array[index] == null){
                throw new IllegalArgumentException("Array values should not be null");
            }
            int difference = array[index].compareTo(elem);
            if(difference == 0){
                return index;
            } else if (difference < 0) {
                left = index + 1;
            } else {
                right = index - 1;
            }

        }
        throw new NoSuchElementException("Value not found in array");
    }

}
