// File : TakeoffQueue.java
// Author : Yichuan Yin, yiny@wwu.edu
// Date: July 24, 2017
// Description : Program to prioritize airplane departures which request takeoff
// at different times from KSEA. Program expects a pre-ordered list of requests.
// (implements a priority queue using a min-heap, attemps full prioritization)

import java.util.*;  // for Scanner & Array
import java.io.*;    // for File

public class TakeoffQueue {
   
   // Flight class
   public static class Flight {
      public String name;  // name of flight
      public int order;    // order of request made
      public int val;      // value used for comparison

      // construct a Flight object given a name and a value for comparison
      public Flight(String name, int val) {
         this(name, -1, val);
      }
      
      // construct a Flight object given a name, an order number,
      // and a value for comparison
      public Flight(String name, int order, int val) {
         this.name = name;
         this.order = order;
         this.val = val;
      }
   }
   
   // main routine
   public static void main(String args[]) throws FileNotFoundException {
      
      // make Scanner for file input
      Scanner input = new Scanner(new File(args[0]));  
      // grab prioritization scheme
      String priorityScheme = args[1];
      // minHeap array to hold flights
      Flight[] minHeap = new Flight[1];
      
      switch (priorityScheme) {
      
         // simple prioritization based on order of takoff requests made
         case "1" :  simplePrioritization(minHeap, input);
                     break;
         
         // intermediate prioritization based on count of passengers
         case "2" :  intermediatePrioritization(minHeap, input);
                     break;
         
         // full prioritization based on passenger count and time of request
         case "3" :  fullPrioritization(minHeap, input);
                     break;
         
         default :   break;
      }     
   }
   
   // add flights to minHeap using simple prioritization, airplanes are granted
   // permission to take off based on the order in which the requests are made,
   // i.e. flight requests are "first come, first served"
   public static void simplePrioritization(Flight[] minHeap, Scanner input) {
      
      // order of request made
      int order = 1;
      
      // process every flight request from file
      while (input.hasNextLine()) {
         Scanner lineScan = new Scanner(input.nextLine());
         String name = lineScan.next();
         minHeap = addToHeap(minHeap, new Flight(name, order));
         order++;      
      }
      
      // print flights from heap
      while (minHeap.length > 1) {
         System.out.println(minHeap[1].name);
         minHeap = removeFromHeap(minHeap);
      }
   }
   
   // add flights to minHeap using intermediate prioritization, airplanes
   // are granted permission to take off based on number of passengers,
   // flights with more passengers have higher priority, for flights with
   // same passenger count, priority is determined by order of requests received
   public static void intermediatePrioritization(Flight[] minHeap, Scanner input) {
           
      // order of request made
      int order = 1;
      
      // process every flight request from file
      while (input.hasNextLine()) {
         Scanner lineScan = new Scanner(input.nextLine());
         String name = lineScan.next();
         lineScan.next();
         lineScan.next();
         lineScan.next();
         int passengerCount = lineScan.nextInt();
         minHeap = addToHeap(minHeap, new Flight(name, order, -passengerCount)); // negate for sorting
         order++;
      }
      
      // print flights from heap
      while (minHeap.length > 1) {
         System.out.println(minHeap[1].name + " " + -minHeap[1].val); // negate for printing
         minHeap = removeFromHeap(minHeap);
      }
   }
   
   // full prioritization, flights are cleared for takeoff whenever runway is empty,
   // otherwise, flights are added to the minHeap using intermediate prioritization,
   // if so, flights with more passeners have higher priority, and if flights have
   // the same passenger count, priority is determined by order of requests received
   // (assume time in p.m.)
   public static void fullPrioritization(Flight[] minHeap, Scanner input) {
         
      // start the clock at time 0 (12:00 p.m.)
      int currentTime = 0;
      // order of request made
      int order = 1;
      
      // process every flight request from file
      while (input.hasNextLine()) {
         Scanner lineScan = new Scanner(input.nextLine());
         String name = lineScan.next();
         lineScan.next();
         lineScan.next();
         int requestTime = timeFromString(lineScan.next());
         int passengerCount = lineScan.nextInt();
         Flight flight = new Flight(name, order, -passengerCount); // negate for sorting
         
         // while time permits (i.e. runway is empty)
         while (requestTime > currentTime) {       
            
            // relase flights for takeoff from the heap, update current time
            if (minHeap.length > 1) {
               currentTime = releaseFlight(minHeap[1], currentTime);
               minHeap = removeFromHeap(minHeap);              
            
            // heap is empty, update current time only
            } else {
               currentTime = requestTime;
            }                       
         }       
         
         // add new flight request to heap
         minHeap = addToHeap(minHeap, flight);
         
         order++;    
      }
      
      // clear any backlog of unreleased flights
      while (minHeap.length > 1) {
            currentTime = releaseFlight(minHeap[1], currentTime);
            minHeap = removeFromHeap(minHeap);
      }
   }
   
   // release a flight for takeoff by printing flight name and departure time,
   // return departure time (time of completion of takeoff roll)
   public static int releaseFlight(Flight flight, int currentTime) {
      int departureTime = currentTime + timeToTakeOff(-flight.val); // negate for printing
      System.out.printf("%s departed at %s\n", flight.name, timeToString(departureTime));
      return departureTime;
   }
   
   // return time duration of takeoff (in minutes) based on number of passengers
   public static int timeToTakeOff(int passengerCount) {
      return (passengerCount / 2 / 60) + (int)Math.ceil((double)(passengerCount / 2 % 60) / 60);
   }
   
   // given a String representation of time in "hour:minute" format, return time in minutes
   public static int timeFromString(String time) {
      return Integer.parseInt(time.substring(0, time.indexOf(":"))) % 12 * 60
         + Integer.parseInt(time.substring(time.indexOf(":") + 1));
   }
   
   // given time in minutes, return its String representation in "hour:minute" format
   public static String timeToString(int time) {      
      
      int hour = ((time / 60) + 11) % 12 + 1;
      int minute = time % 60;
      
      if (hour < 10 && minute < 10) {
         return "0" + hour + ":" + "0" + minute;
      } else if (hour < 10) {
         return "0" + hour + ":" + minute;
      } else if (minute < 10) {
         return hour + ":" + "0" + minute;
      } else {
         return hour + ":" + minute;
      }     
   }
   
   // add one flight to minHeap, return pointer to minHeap
   public static Flight[] addToHeap(Flight[] minHeap, Flight flight) {
      
      // enlarge heap by 1 slot
      minHeap = Arrays.copyOf(minHeap, minHeap.length + 1);     
      // place new flight at end of heap
      minHeap[minHeap.length - 1] = flight;
      
      // percolate up if value of parent is greater than child,
      // if values are the same, order number will be compared
      int i = minHeap.length - 1;
      while (hasParent(i) && ((flight.val < minHeap[parent(i)].val) || 
         (flight.val == minHeap[parent(i)].val && flight.order < minHeap[parent(i)].order))) {
         int parent = parent(i);
         swapElement(minHeap, parent, i);
         i = parent;
      }
      
      return minHeap;
   }
   
   // remove a flight from top of minHeap, return pointer to minHeap
   public static Flight[] removeFromHeap(Flight[] minHeap) {
      
      // replace first flight with last
      minHeap[1] = minHeap[minHeap.length - 1];
      // trim the heap (remove last flight)
      minHeap = Arrays.copyOfRange(minHeap, 0, minHeap.length - 1);
         
      // percolate down if value of child is less than parent,
      // if values are the same, order number will be compared
      int i = 1;
      int child = smallerChild(minHeap, i);
      while (child != -1 && ((minHeap[child].val < minHeap[i].val) ||
         (minHeap[child].val == minHeap[i].val && minHeap[child].order < minHeap[i].order))) {
         swapElement(minHeap, child, i);
         i = child;
         child = smallerChild(minHeap, i);
      } 
      
      return minHeap;
   }
   
   // given index of an element in the minHeap, return index
   // of the smaller child, return -1 if it has no children
   public static int smallerChild(Flight[] minHeap, int i) {
      
      // have two children
      if (leftChild(i) < minHeap.length && rightChild(i) < minHeap.length) {
         
         // left child is smaller
         if (minHeap[leftChild(i)].val < minHeap[rightChild(i)].val) {
            return leftChild(i);
            
         // right child is smaller
         } else if (minHeap[rightChild(i)].val < minHeap[leftChild(i)].val) {
            return rightChild(i);
            
         // children are equal, return children with lower order number
         } else if (minHeap[leftChild(i)].order < minHeap[rightChild(i)].order) {
            return leftChild(i);
         } else {
            return rightChild(i);
         }
      
      // have left child
      } else if (leftChild(i) < minHeap.length) {
         return leftChild(i);
      
      // have right child
      } else if (rightChild(i) < minHeap.length) {
         return rightChild(i);
      
      // have no children
      } else {
         return -1;
      }   
   }
   
   // given index of an element in heap, return index of left child
   public static int leftChild(int i) {
      return 2 * i;
   }
   
   // given index of an element in heap, return index of right child
   public static int rightChild(int i) {
      return 2 * i + 1;
   }
   
   // given index of an element in heap, return true if it has parent, false otherwise
   public static boolean hasParent(int i) {
      return i > 1;
   }
   
   // given index of an element in heap, return index of parent
   public static int parent(int i) {
      return i / 2;
   }
   
   // swap two elements in the minHeap given their indices
   public static void swapElement(Flight[] minHeap, int i, int j) {
      Flight temp = minHeap[i];
      minHeap[i] = minHeap[j];
      minHeap[j] = temp;
   }

}