// File : SortCompare.java
// Author : Yichuan Yin, yiny@wwu.edu
// Description : non object-oriented implementation of Insertion Sort, Merge Sort,
// Quick Sort, and Lease Significant Digit (LSD) Radix Sort. Program tallies and
// displays the number of comparisons between each array entries when sorting an
// array of size specified by the user. (Developed from ideas and pseudocode
// presented in class by Filip Jagozinski)

import java.util.*;  // for Random, Scanner, Arrays, Queue

public class SortCompare {
   
   // base of enumeration for radix sort
   public static final int RADIX_BASE = 10;
   // max absolute value for integer in the array
   public static final int INT_RANGE = 99;
   // max length of array to be printed
   public static final int PRINT_LENGTH = 20;
   
   // array to hold count of comparisons performed for each sort
   public static final int[] COMPARISON_COUNT = {0,0,0,0};
   // array index for merge sort comparison count
   public static final int MERGE_SORT = 0;
   // array index for quick sort comparison count
   public static final int INSERTION_SORT = 1;
   // array index for insertion sort comparison count
   public static final int QUICK_SORT = 2;
   // array index for radix sort comparison count
   public static final int RADIX_SORT = 3;
   
   // main routine
   public static void main(String[] args) {
      
      // print the header
      System.out.println("Input Params");
      System.out.println("============");
      
      // prompt the user for the number of array entries desired  
      Scanner input = new Scanner(System.in);    
      System.out.print("How many entries? ");
      int numOfEntries = input.nextInt();    
      
      // prompt the user for the sorting method(s) desired
      System.out.print("Which sort [m,i,q,r,all]? ");
      String sortMethod = input.next();
      System.out.println();
      
      // generate a deep copy of the array with random integer entries
      int[] arrayDeepCopy = new int[numOfEntries];
      Random rand = new Random();
      for (int i = 0; i < arrayDeepCopy.length; i++) {
         arrayDeepCopy[i] = rand.nextInt(INT_RANGE + 1) + rand.nextInt(INT_RANGE + 1) - INT_RANGE;
      }
      
      // sort the array with the user-selected method(s)
      sortArray(arrayDeepCopy, sortMethod);
   }
   
   // sort a copy of the deep copy array using merge sort, insertion sort,
   // quick sort, or radix sort depending on the given sort method,
   // print the unsorted and sorted array if it's not empty or longer than PRINT_LENGTH,
   // display the number of comparisons performed for each sort method
   public static void sortArray(int[] arrayDeepCopy, String sortMethod) {
      
      int[] array = Arrays.copyOf(arrayDeepCopy, arrayDeepCopy.length);
      
      boolean isPrintable = array.length > 0 && array.length < PRINT_LENGTH;
      
      switch (sortMethod) {
         case "m":   System.out.println("merge sort");
                     System.out.println("==========");
                     printArray(isPrintable, "Unsorted", array);
                     array = mergeSort(array);
                     System.out.println("Num Comparisons: " + COMPARISON_COUNT[MERGE_SORT]);
                     printArray(isPrintable, "Sorted", array);
                     System.out.println();
                     break;
         
         case "i":   System.out.println("insertion sort");
                     System.out.println("==============");
                     printArray(isPrintable, "Unsorted", array);
                     insertionSort(array);
                     System.out.println("Num Comparisons: " + COMPARISON_COUNT[INSERTION_SORT]);
                     printArray(isPrintable, "Sorted", array);
                     System.out.println();
                     break;
         
         case "q":   System.out.println("quick sort");
                     System.out.println("==========");
                     printArray(isPrintable, "Unsorted", array);
                     quickSort(array);
                     System.out.println("Num Comparisons: " + COMPARISON_COUNT[QUICK_SORT]);
                     printArray(isPrintable, "Sorted", array);
                     System.out.println();
                     break;
         
         case "r":   System.out.println("radix sort");
                     System.out.println("==========");
                     printArray(isPrintable, "Unsorted", array);
                     radixSort(array);
                     System.out.println("Num Comparisons: " + COMPARISON_COUNT[RADIX_SORT]);
                     printArray(isPrintable, "Sorted", array);
                     System.out.println();
                     break;
         
         case "all": sortArray(arrayDeepCopy, "m");
                     sortArray(arrayDeepCopy, "i");
                     sortArray(arrayDeepCopy, "q");
                     sortArray(arrayDeepCopy, "r");
                     break;
         
         default :   break;
      }
      
   }
   
   // merge sort the array recursively by dividing the array into two halves
   // until the array is divided into one element arrays, sorting is done
   // when two arrays are merged at the end of each recursive call, array is
   // sorted when an array of the same length as the original array is returned,
   // this is not an in-situ sort, thus returning a pointer to the sorted array,
   // the original given array remain unmodified
   public static int[] mergeSort(int[] array) {
         if (array.length == 1 || array.length == 0) {
            return array;
         } else {
            return mergeTwoSortedArray(
               mergeSort(Arrays.copyOfRange(array, 0, array.length / 2)), 
               mergeSort(Arrays.copyOfRange(array, array.length / 2, array.length)));
         }
   }
   
   // merge two sorted array into a single sorted array, return the result array
   public static int[] mergeTwoSortedArray(int[] array1, int[] array2) {
      
      // new array to hold the sorted elements from array1 and array2
      int[] resultArray = new int[array1.length + array2.length];
      
      // indices for array1 and array2, respectively
      int i = 0;
      int j = 0;
      
      while (i + j < resultArray.length) {
         
         // before reaching the end of array1 or array2,
         // add elements (small to large) from array1 and array2 to the result array,
         // increment the comparison count between entries from array1 and array2
         while (i < array1.length && j < array2.length) {
            if (array1[i] < array2[j]) {
               resultArray[i + j] = array1[i];
               i++;
            } else {
               resultArray[i + j] = array2[j];
               j++;
            }
            COMPARISON_COUNT[MERGE_SORT]++;
         }
         
         // add any unused elements from array1 or array2 to the result array
         if (i < array1.length) {
            resultArray[i + j] = array1[i];
            i++;
         } else {
            resultArray[i + j] = array2[j];
            j++;
         }
      }
      
      return resultArray;
   }
   
   // insertion sort the array iteratively, increment the comparison count
   // for each comparison performed between entries of the array
   public static void insertionSort(int[] array) {
      for (int i = 1; i < array.length; i++) {
         int j = i;
         while (j > 0 && array[j - 1] > array[j]) {
            swapElement(array, j - 1, j);
            j--;
            COMPARISON_COUNT[INSERTION_SORT]++;
         }
         COMPARISON_COUNT[INSERTION_SORT]++;
      }       
   }
   
   // public caller in a recursive quick sort public/private method pair,
   // enter the recursive quict sort with indices covering the whole array
   public static void quickSort(int[] array) {
      quickSort(array, 0, array.length - 1);
   }
   
   // quick sort an array recursively, p and r denote the first and last index
   // of an array segment being investigated for partition, continue to examine
   // array segments to the left and right of pivot by calling this method
   // until the array is sorted (when p and r refer to the same array entry)
   private static void quickSort(int[] array, int p, int r) {
      if (p < r) {
         int pivotIndex = partition(array, p, r);
         quickSort(array, p, pivotIndex - 1);
         quickSort(array, pivotIndex + 1, r);
      }
   }
   
   // partition the array segment from index p to index r so that the elements
   // to the left of the pivot are not larger than the elements to the right of
   // the pivot, increment the comparison count between array entries and the
   // pivot value, return the pivot index
   public static int partition(int[] array, int p, int r) {
      int i = p - 1;
      // choose last element of the array segment as pivot value
      int pivotValue = array[r];
      for (int j = p; j < r; j++) {
         if (array[j] <= pivotValue) {
            i++;
            swapElement(array, i, j);
         }
         COMPARISON_COUNT[QUICK_SORT]++;
      }
      swapElement(array, i + 1, r);
      return i + 1;
   }
   
   // least significant digit radix sort the array iteratively,
   // use an array of Queues for auxiliary storage
   public static void radixSort(int[] array) {
      
      // make an array of bins for negative, 0, and positive digits in a base
      Queue<Integer>[] radixBin = new Queue[RADIX_BASE + RADIX_BASE - 1];
      for (int i = 0; i < radixBin.length; i++) {
         radixBin[i] = new LinkedList<Integer>();
      }
      
      // needed function variables
      int digitsLeft;
      int powerOfBase = 0;
      
      do {
      
         digitsLeft = 0;
         
         // put array entries into radix bins correspinding to the significant digit being examined
         for (int i = 0; i < array.length; i++) {
            int num = array[i];
            int significantDigit = num / (int)Math.pow(RADIX_BASE, powerOfBase) % RADIX_BASE;
            // binNumber is offset by (RADIX_BASE - 1) to accommodate negative numbers
            int binNumber = significantDigit + (RADIX_BASE - 1);
            digitsLeft = Math.max(Math.abs(num / (int)Math.pow(RADIX_BASE, powerOfBase + 1)), digitsLeft);
            radixBin[binNumber].add(num);
         }
         
         // take array entries from the radix bins and put them back into the array
         int i = 0;
         for (int j = 0; j < radixBin.length; j++) {
            Queue<Integer> currentBin = radixBin[j];
            while (!currentBin.isEmpty()) {
               array[i] = currentBin.remove();
               i++;
            }
         }
         
         powerOfBase++;
         
      // repeat the loop until all digits from all array entries are examined
      } while (digitsLeft != 0);
      
   }
   
   // print elements of array if it's printable per the given boolean value,
   // display its sorted status given a String representing its sorted status
   public static void printArray(Boolean isPrintable, String sortedStatus, int[] array) {
      if (isPrintable) {
         System.out.print(sortedStatus + " array: ");
         int i = 0;
         while (i < array.length - 1) {
            System.out.print(array[i] + " ");
            i++;
         }
         System.out.println(array[i]);
      }
   }
   
   // swap two elements in an array given the indices of the entries to be swapped
   public static void swapElement(int[] array, int i, int j) {
      int temp = array[i];
      array[i] = array[j];
      array[j] = temp;
   }

}