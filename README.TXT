Introduction
============
A simulation to explore Demand Paging and Page Replacement policies, assessing the effectiveness of various algorithms (FIFO, LRU, Second Chance) through meticulous analysis of hit/miss ratios.

Code Structure
==============

Process.java
------------
-Process class extends thread (Here are the process details given Proc_no, numPages, arrivalTime (in nano seconds), serviceDuration (in seconds generated randomly), pid, pagesInMemory).
-Two logics of LOR are implemented due to two different interpretations in the question.(This can be changes according to user through variable defined in main static final int LOR_ON_PAGE = 0; 
For LOR algorithm on Page Reference Delta, change this value to 0.)
*LOR algorithm on Page Reference Delta i.e. abs(delta(i)) < 10
*LOR algorithm on Page Reference  i.e. abs(i) <= 10

Memory.java
------------
-free page list created, initially with 100 pages, each 1 MB is created.
-Initialized free pages : pid -1 representing free frame.
-Defined logic for indicating when page requested by process not in frame(Page fault: Page not in memory).


Main.java
----------
-This is the driver class used for entireSimulation.
- LOR_ON_PAGE  variable defined when its value is 1 it uses LOR algorithm on Page Reference  else it uses LOR algorithm on Page Reference Delta.
-Simulation duration for each Page replacement Algorithm is 1 min(60 seconds) and this runs for 5 times for each Page replacement algorithm.

Page.java
----------
-This class defines page in the simulation.
-Initially InMemory status is false.

PageReplacementAlgorithm.java
-----------------------------
-It is an interface.
-Page replacement Algorithms follows this modularity.


Page Replacement Algorithms 
---------------------------
-Algorithms defined in different Classes: FIFOPageReplacement, LRUPageReplacement, MFUPageReplacement, LFUPageReplacement, RandomPickPageReplacement different Java files present.

Simulation Assumptions
=========================
-The main memory has 100 MB of space and that page sizes are 1 MB.
-Processes have random and evenly distributed sizes of 5, 11, 17, and 31 MB.
-Processes have random and evenly distributed service duration of 1 to 10 seconds.
-Free pages are structured as a linked list, and when a process needs a free page we pick the page from the head of the free linked list.
-A job at the head of the Job Queue is taken out and assigned memory to run if there is at least 4 free pages that can be assigned to that job (i.e., initially only 25 jobs will run. When a Job runs, we need initially one page (page-0) and then the process will make a memory reference to another page in that process and we need to page-in the referenced page from disk if the page is not already in memory. Once jobs are in memory, the processes run independently and simultaneously for their duration.

Simulation duration and Program run details:
==============================================.
-For each Page replacement algorithm simulation runs for 60 seconds and we run this simulation for five times to take average of Hit/Miss ratio.
-Final output Contains average Hit/Miss Ratio of five runs for each Page Replacement Algorithm.
-Log files contains detailed logs and Hit/ Miss ratio post each simulation.

Results
========

Case 1: LOR algorithm on Page Reference Delta i.e. abs(delta(i)) < 10
---------------------------------------------------------------------------------------------------
Sample output 1 : End of Simulation statistics for FIFO_PAGE_REPLACEMENT : Hits = 8213, Misses = 8073, Average Hit-Miss ratio across all iterations = 1.0173417564721912

2023-12-07 09:17:59.507564000
End of Simulation statistics for RANDOM_PICK : Hits = 7969, Misses = 8375, Average Hit-Miss ratio across all iterations = 0.9515223880597015

2023-12-07 09:17:59.507657000
End of Simulation statistics for LRU : Hits = 8154, Misses = 8080, Average Hit-Miss ratio across all iterations = 1.009158415841584

2023-12-07 09:17:59.507684000
End of Simulation statistics for LFU : Hits = 7628, Misses = 8589, Average Hit-Miss ratio across all iterations = 0.8881127022936314

2023-12-07 09:17:59.507711000
End of Simulation statistics for MFU : Hits = 7718, Misses = 8666, Average Hit-Miss ratio across all iterations = 0.8906069697669051

Best: FIFO, Worst:LFU

Sample Output 2 : 

2023-12-07 08:45:34.469724000
End of Simulation statistics for FIFO_PAGE_REPLACEMENT : Hits = 8256, Misses = 8220, Average Hit-Miss ratio across all iterations = 1.0043795620437956

2023-12-07 08:45:34.483075000
End of Simulation statistics for RANDOM_PICK : Hits = 8352, Misses = 8248, Average Hit-Miss ratio across all iterations = 1.0126091173617846

2023-12-07 08:45:34.483162000
End of Simulation statistics for LRU : Hits = 8160, Misses = 8117, Average Hit-Miss ratio across all iterations = 1.0152975237156585

2023-12-07 08:45:34.483198000
End of Simulation statistics for LFU : Hits = 7721, Misses = 8485, Average Hit-Miss ratio across all iterations = 0.9099587507365939

2023-12-07 08:45:34.483225000
End of Simulation statistics for MFU : Hits = 8235, Misses = 8416, Average Hit-Miss ratio across all iterations = 0.9784933460076045


Best: LRU  Worst : LFU


Sample Output 3:
2023-12-07 12:45:37.773862000
End of Simulation statistics for FIFO_PAGE_REPLACEMENT : Hits = 8005, Misses = 8273, Average Hit-Miss ratio across all iterations = 0.9676054635561465

2023-12-07 12:45:37.878408000
End of Simulation statistics for RANDOM_PICK : Hits = 8187, Misses = 8280, Average Hit-Miss ratio across all iterations = 0.988768115942029

2023-12-07 12:45:37.878586000
End of Simulation statistics for LRU : Hits = 8461, Misses = 8034, Average Hit-Miss ratio across all iterations = 1.0531491162559123

2023-12-07 12:45:37.878654000
End of Simulation statistics for LFU : Hits = 7604, Misses = 8570, Average Hit-Miss ratio across all iterations = 0.8872812135355893

2023-12-07 12:45:37.878697000
End of Simulation statistics for MFU : Hits = 7879, Misses = 8459, Average Hit-Miss ratio across all iterations = 0.9314339756472396

Best LRU Worst LFU

Case 2: LOR algorithm on Page Reference  i.e. abs(i) <= 10  
-------------------------------------------------------------
Sample Output-1 
2023-12-07 10:19:40.611967000
End of Simulation statistics for FIFO_PAGE_REPLACEMENT : Hits = 11818, Misses = 6304, Average Hit-Miss ratio across all iterations = 1.8746827411167513

2023-12-07 10:19:40.636004000
End of Simulation statistics for RANDOM_PICK : Hits = 12269, Misses = 6051, Average Hit-Miss ratio across all iterations = 2.027598744009255

2023-12-07 10:19:40.636083000
End of Simulation statistics for LRU : Hits = 13453, Misses = 5785, Average Hit-Miss ratio across all iterations = 2.4373638720829734

2023-12-07 10:19:40.636109000
End of Simulation statistics for LFU : Hits = 13453, Misses = 5363, Average Hit-Miss ratio across all iterations = 2.3084840574305427

2023-12-07 10:19:40.636130000
End of Simulation statistics for MFU : Hits = 10090, Misses = 8365, Average Hit-Miss ratio across all iterations = 1.206216377764495


Process finished with exit code 0

Best LRU , Worst MFU


Discussion
==========
- Out of the 5 different simulations, I could see varied results. Sometimes, LRU was the best and MFU the worst. Other times, FIFO was better then random pick, and sometimes LFU was the worst. Which algorithm is the best can not be determined. The variation in performance across different outputs or simulations can be attributed to the nature of the workload and the specific characteristics of each simulation. Page replacement algorithms are designed to handle different scenarios and workloads, and their effectiveness can vary based on the access patterns of the application.
