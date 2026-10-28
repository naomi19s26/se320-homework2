package edu.drexel.se320;

// Hamcrest
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertThrows;

import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.Assume;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.Provide;
import net.jqwik.api.Tuple;
import net.jqwik.api.Tuple.Tuple2;
import net.jqwik.api.constraints.IntRange;
import net.jqwik.api.constraints.Size;


public class PropertyTests extends BinarySearchBase {
    @Provide
    Arbitrary<Integer[]> sortedIntegerArrays() {
        return Arbitraries.integers()
            .between(0, 100)
            .list()
            .ofMinSize(1)
            .ofMaxSize(30)
            .map(list -> {
                Integer[] array = list.toArray(new Integer[0]);
                Arrays.sort(array);
                return array;
            });
    }

    @Provide
    Arbitrary<Tuple2<Integer[], Integer>> sortedArrayWithTargetDuplicate() {
        return sortedIntegerArrays().map(array -> {
            if (array.length >= 2) {
                array[1] = array[0];
                Arrays.sort(array);
            }
            int target = 0;
            if (array.length > 0){
                target = array[0];
            }
            return Tuple.of(array, target);
        });
    }

    @Property
    boolean testTargetExistsInArray(
            @ForAll("sortedIntegerArrays") Integer[] array,
            @ForAll @IntRange(min = 0, max = 29) int index) {
        Assume.that(array.length > 0);
        int target = array[index % array.length];
        int result = find(array, target);
        return result >= 0 && result < array.length && array[result].equals(target);
    }

    @Property
    boolean testTargetDoesntExistInArray(
            @ForAll("sortedIntegerArrays") Integer[] array,
            @ForAll @IntRange(min = 200, max = 300) int target) {
        Assume.that(array.length > 0);
        assertThrows(NoSuchElementException.class, () -> find(array, target));
        return true;
    }

    @Property
    boolean testTargetIsTheFirstElementInTheArray(@ForAll("sortedIntegerArrays") Integer[] array) {
        Assume.that(array.length > 0);
        int target = array[0];
        int result = find(array, target);
        return result >= 0 && array[result].equals(target);
    }

    @Property
    boolean testTargetIsTheLastElementInTheArray(@ForAll("sortedIntegerArrays") Integer[] array) {
        Assume.that(array.length > 0);
        int target = array[array.length - 1];
        int result = find(array, target);
        return result >= 0 && array[result].equals(target);
    }

    @Property
    boolean testTargetIsInFirstHalfOfTheArray(
            @ForAll("sortedIntegerArrays") Integer[] array,
            @ForAll @IntRange(min = 0, max = 15) int randomIndex) {
        Assume.that(array.length >= 2);
        int middleIndex = array.length / 2;
        int targetIndex = randomIndex % middleIndex;
        int target = array[targetIndex];
        int result = find(array, target);
        return result >= 0 && result < array.length && array[result].equals(target);
    }

    @Property
    boolean testTargetIsInSecondHalfOfArray(
            @ForAll("sortedIntegerArrays") Integer[] array,
            @ForAll @IntRange(min = 0, max = 15) int randomIndex) {
        Assume.that(array.length >= 2);
        int midIdx = array.length / 2;
        Assume.that(midIdx < array.length);
        int rangeSize = array.length - midIdx;
        int targetIdx = midIdx + (randomIndex% rangeSize);
        int target = array[targetIdx];

        int result = find(array, target);
        return result >= 0 && result < array.length && array[result].equals(target);
    }

    @Property
    boolean testTargetIsUniqueInArrayAndContainsDuplicateElements(
            @ForAll @IntRange(min = 10, max = 20) int duplicateValue,
            @ForAll @Size(min = 5, max = 20) List<@IntRange(min = 0, max = 100) Integer> list) {
        list.set(0, duplicateValue);
        list.set(1, duplicateValue);
        Integer[] array = list.toArray(new Integer[0]);
        Arrays.sort(array);
        boolean foundUniqueValue = false;
        int target = -1;
        for (int i = 0; i < array.length; i++) {
            boolean isDuplicate = (i > 0 && array[i].equals(array[i-1])) || (i < array.length - 1 && array[i].equals(array[i+1]));
            if (!isDuplicate) {
                target = array[i];
                foundUniqueValue = true;
                break;
            }
        }
        Assume.that(foundUniqueValue);
        int result = find(array, target);
        return result >= 0 && array[result].equals(target);
    }


    @Property
    boolean testTargetIsDuplicateElement(@ForAll("sortedArrayWithTargetDuplicate") Tuple2<Integer[], Integer> input) {
        Integer[] array = input.get1();
        int target = input.get2();
        Assume.that(array.length > 0);
        Assume.that(Arrays.asList(array).contains(target));
        int result = find(array, target);
        return result >= 0 && result < array.length && array[result].equals(target);
    }

    @Property
    boolean testEmptyArrayBehavior(@ForAll int target) {
        Integer[] arr = new Integer[0];
        assertThrows(IllegalArgumentException.class, () -> find(arr, target));
        return true;
    }

    @Property
    boolean testsingleElementInArray(
            @ForAll @IntRange(min = 0, max = 100) int element,
            @ForAll @IntRange(min = 0, max = 100) int target) {
        Integer[] array = {element};
        if (element == target){
            return find(array, target)==0;
        } else {
            assertThrows(NoSuchElementException.class, () -> find(array, target));
            return true;
        }
    }
}

