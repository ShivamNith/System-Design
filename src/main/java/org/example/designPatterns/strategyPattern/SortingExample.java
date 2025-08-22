package org.example.designPatterns.strategyPattern;

import java.util.Arrays;
import java.util.Random;

interface SortingStrategy {
    void sort(int[] array);
    String getAlgorithmName();
    String getTimeComplexity();
    String getSpaceComplexity();
}

class BubbleSortStrategy implements SortingStrategy {
    @Override
    public void sort(int[] array) {
        int n = array.length;
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (array[j] > array[j + 1]) {
                    int temp = array[j];
                    array[j] = array[j + 1];
                    array[j + 1] = temp;
                }
            }
        }
    }
    
    @Override
    public String getAlgorithmName() {
        return "Bubble Sort";
    }
    
    @Override
    public String getTimeComplexity() {
        return "O(n²)";
    }
    
    @Override
    public String getSpaceComplexity() {
        return "O(1)";
    }
}

class QuickSortStrategy implements SortingStrategy {
    @Override
    public void sort(int[] array) {
        quickSort(array, 0, array.length - 1);
    }
    
    private void quickSort(int[] array, int low, int high) {
        if (low < high) {
            int pi = partition(array, low, high);
            quickSort(array, low, pi - 1);
            quickSort(array, pi + 1, high);
        }
    }
    
    private int partition(int[] array, int low, int high) {
        int pivot = array[high];
        int i = (low - 1);
        
        for (int j = low; j < high; j++) {
            if (array[j] < pivot) {
                i++;
                int temp = array[i];
                array[i] = array[j];
                array[j] = temp;
            }
        }
        
        int temp = array[i + 1];
        array[i + 1] = array[high];
        array[high] = temp;
        
        return i + 1;
    }
    
    @Override
    public String getAlgorithmName() {
        return "Quick Sort";
    }
    
    @Override
    public String getTimeComplexity() {
        return "O(n log n) average, O(n²) worst";
    }
    
    @Override
    public String getSpaceComplexity() {
        return "O(log n)";
    }
}

class MergeSortStrategy implements SortingStrategy {
    @Override
    public void sort(int[] array) {
        mergeSort(array, 0, array.length - 1);
    }
    
    private void mergeSort(int[] array, int left, int right) {
        if (left < right) {
            int middle = left + (right - left) / 2;
            mergeSort(array, left, middle);
            mergeSort(array, middle + 1, right);
            merge(array, left, middle, right);
        }
    }
    
    private void merge(int[] array, int left, int middle, int right) {
        int n1 = middle - left + 1;
        int n2 = right - middle;
        
        int[] leftArray = new int[n1];
        int[] rightArray = new int[n2];
        
        System.arraycopy(array, left, leftArray, 0, n1);
        System.arraycopy(array, middle + 1, rightArray, 0, n2);
        
        int i = 0, j = 0, k = left;
        
        while (i < n1 && j < n2) {
            if (leftArray[i] <= rightArray[j]) {
                array[k++] = leftArray[i++];
            } else {
                array[k++] = rightArray[j++];
            }
        }
        
        while (i < n1) {
            array[k++] = leftArray[i++];
        }
        
        while (j < n2) {
            array[k++] = rightArray[j++];
        }
    }
    
    @Override
    public String getAlgorithmName() {
        return "Merge Sort";
    }
    
    @Override
    public String getTimeComplexity() {
        return "O(n log n)";
    }
    
    @Override
    public String getSpaceComplexity() {
        return "O(n)";
    }
}

class InsertionSortStrategy implements SortingStrategy {
    @Override
    public void sort(int[] array) {
        int n = array.length;
        for (int i = 1; i < n; i++) {
            int key = array[i];
            int j = i - 1;
            
            while (j >= 0 && array[j] > key) {
                array[j + 1] = array[j];
                j--;
            }
            array[j + 1] = key;
        }
    }
    
    @Override
    public String getAlgorithmName() {
        return "Insertion Sort";
    }
    
    @Override
    public String getTimeComplexity() {
        return "O(n²) worst, O(n) best";
    }
    
    @Override
    public String getSpaceComplexity() {
        return "O(1)";
    }
}

class SelectionSortStrategy implements SortingStrategy {
    @Override
    public void sort(int[] array) {
        int n = array.length;
        
        for (int i = 0; i < n - 1; i++) {
            int minIdx = i;
            for (int j = i + 1; j < n; j++) {
                if (array[j] < array[minIdx]) {
                    minIdx = j;
                }
            }
            
            int temp = array[minIdx];
            array[minIdx] = array[i];
            array[i] = temp;
        }
    }
    
    @Override
    public String getAlgorithmName() {
        return "Selection Sort";
    }
    
    @Override
    public String getTimeComplexity() {
        return "O(n²)";
    }
    
    @Override
    public String getSpaceComplexity() {
        return "O(1)";
    }
}

class DataSorter {
    private SortingStrategy sortingStrategy;
    
    public void setSortingStrategy(SortingStrategy strategy) {
        this.sortingStrategy = strategy;
    }
    
    public void sort(int[] data) {
        if (sortingStrategy == null) {
            throw new IllegalStateException("No sorting strategy set!");
        }
        
        System.out.println("\n=== Sorting with " + sortingStrategy.getAlgorithmName() + " ===");
        System.out.println("Time Complexity: " + sortingStrategy.getTimeComplexity());
        System.out.println("Space Complexity: " + sortingStrategy.getSpaceComplexity());
        
        if (data.length <= 20) {
            System.out.println("Original: " + Arrays.toString(data));
        }
        
        long startTime = System.nanoTime();
        sortingStrategy.sort(data);
        long endTime = System.nanoTime();
        
        if (data.length <= 20) {
            System.out.println("Sorted:   " + Arrays.toString(data));
        }
        
        System.out.println("Time taken: " + (endTime - startTime) / 1000 + " microseconds");
        System.out.println("Array sorted: " + isSorted(data));
    }
    
    public void performanceComparison(int arraySize) {
        SortingStrategy[] strategies = {
            new BubbleSortStrategy(),
            new SelectionSortStrategy(),
            new InsertionSortStrategy(),
            new QuickSortStrategy(),
            new MergeSortStrategy()
        };
        
        System.out.println("\n=== Performance Comparison ===");
        System.out.println("Array size: " + arraySize + " elements");
        System.out.println("Testing with random data...\n");
        
        int[] originalData = generateRandomArray(arraySize);
        
        for (SortingStrategy strategy : strategies) {
            int[] data = originalData.clone();
            
            System.out.println(strategy.getAlgorithmName() + ":");
            System.out.println("  Time Complexity: " + strategy.getTimeComplexity());
            System.out.println("  Space Complexity: " + strategy.getSpaceComplexity());
            
            long startTime = System.nanoTime();
            strategy.sort(data);
            long endTime = System.nanoTime();
            
            long timeTaken = (endTime - startTime) / 1000;
            System.out.println("  Time taken: " + timeTaken + " microseconds");
            System.out.println("  Sorted correctly: " + isSorted(data));
            System.out.println();
        }
    }
    
    public void bestWorstCaseDemo() {
        System.out.println("\n=== Best/Worst Case Demonstration ===");
        
        int size = 1000;
        int[] sortedArray = generateSortedArray(size);
        int[] reversedArray = generateReversedArray(size);
        int[] randomArray = generateRandomArray(size);
        
        SortingStrategy[] strategies = {
            new InsertionSortStrategy(),
            new QuickSortStrategy()
        };
        
        for (SortingStrategy strategy : strategies) {
            System.out.println("\n" + strategy.getAlgorithmName() + ":");
            
            int[] testSorted = sortedArray.clone();
            long startTime = System.nanoTime();
            strategy.sort(testSorted);
            long sortedTime = (System.nanoTime() - startTime) / 1000;
            
            int[] testReversed = reversedArray.clone();
            startTime = System.nanoTime();
            strategy.sort(testReversed);
            long reversedTime = (System.nanoTime() - startTime) / 1000;
            
            int[] testRandom = randomArray.clone();
            startTime = System.nanoTime();
            strategy.sort(testRandom);
            long randomTime = (System.nanoTime() - startTime) / 1000;
            
            System.out.println("  Already sorted: " + sortedTime + " μs");
            System.out.println("  Reverse sorted: " + reversedTime + " μs");
            System.out.println("  Random data:    " + randomTime + " μs");
        }
    }
    
    private boolean isSorted(int[] array) {
        for (int i = 0; i < array.length - 1; i++) {
            if (array[i] > array[i + 1]) {
                return false;
            }
        }
        return true;
    }
    
    private int[] generateRandomArray(int size) {
        Random random = new Random();
        int[] array = new int[size];
        for (int i = 0; i < size; i++) {
            array[i] = random.nextInt(1000);
        }
        return array;
    }
    
    private int[] generateSortedArray(int size) {
        int[] array = new int[size];
        for (int i = 0; i < size; i++) {
            array[i] = i;
        }
        return array;
    }
    
    private int[] generateReversedArray(int size) {
        int[] array = new int[size];
        for (int i = 0; i < size; i++) {
            array[i] = size - i;
        }
        return array;
    }
}

public class SortingExample {
    public static void main(String[] args) {
        System.out.println("=== Sorting Strategy Pattern Example ===");
        
        DataSorter sorter = new DataSorter();
        
        int[] smallArray = {64, 34, 25, 12, 22, 11, 90, 88, 45, 50};
        
        System.out.println("\n1. Demonstrating different sorting strategies:");
        
        sorter.setSortingStrategy(new BubbleSortStrategy());
        sorter.sort(smallArray.clone());
        
        sorter.setSortingStrategy(new QuickSortStrategy());
        sorter.sort(smallArray.clone());
        
        sorter.setSortingStrategy(new MergeSortStrategy());
        sorter.sort(smallArray.clone());
        
        System.out.println("\n2. Performance comparison with larger dataset:");
        sorter.performanceComparison(5000);
        
        System.out.println("\n3. Best/Worst case scenarios:");
        sorter.bestWorstCaseDemo();
    }
}