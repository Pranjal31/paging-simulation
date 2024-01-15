import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.logging.*;

public class Main {
    static final int NUM_PROCS = 150;
    static final int SIMULATION_DURATION = 60;  // in seconds
    static final int NUM_ITERATIONS = 5;

    /* Variable to control whether to use:
     *   a) LOR algorithm on Page Reference  i.e. abs(i) < 10   OR
     *   b) LOR algorithm on Page Reference Delta i.e. abs(delta(i)) < 10
     */
    static final int LOR_ON_PAGE = 0;  // For LOR algorithm on Page Reference Delta, change this value to 0


    static int iteration = 0;

    static int[] hitsArray = new int[NUM_ITERATIONS]; ;  // per run
    static int[] missesArray = new int[NUM_ITERATIONS]; ;  // per run

    static final Logger logger = Logger.getLogger(Main.class.getName());

    static enum PageReplacementAlgoEnum {
        FIFO_PAGE_REPLACEMENT,
        RANDOM_PICK,
        LRU,
        LFU,
        MFU
    }

    public static int getProcessSize(Random rand) {
        int[] sizes = {5, 11, 17, 31};
        return sizes[rand.nextInt(sizes.length)];
    }
    public static long getServiceDuration(Random rand){
        int[] durations = new int[10];
        for(int i =0; i < durations.length; i++) {
            durations[i] = i+1;
        }
        return durations[rand.nextInt(durations.length)];
    }

    public static void main(String[] args) throws InterruptedException, IOException {
        Queue<Process> jobQueue = new LinkedList<>();
        List<Process> runningProcs = new ArrayList<>();  // used for termination of processes when Simulation Duration expires
        PageReplacementAlgorithm prAlgo = null;
        Memory mem = null;
        // Create File handler
        FileHandler fileHandler = new FileHandler("simulation_log.txt");
        fileHandler.setFormatter(new SimpleFormatter());

        // set logger level
/*        logger.setLevel(Level.INFO);
        fileHandler.setLevel(Level.INFO);*/

        // add handler
        logger.addHandler(fileHandler);

        int numAlgos = PageReplacementAlgoEnum.values().length;

        double[] averageHitMissRatioArray = new double[numAlgos];
        int[] totalHitsArray = new int[numAlgos];
        int[] totalMissesArray = new int[numAlgos];

        PageReplacementAlgoEnum[] algos = PageReplacementAlgoEnum.values();


        //for (PageReplacementAlgoEnum algo: PageReplacementAlgoEnum.values()) {
        for (int algoIdx =0; algoIdx < numAlgos; algoIdx++) {
            PageReplacementAlgoEnum algo = algos[algoIdx];

            averageHitMissRatioArray[algoIdx] = 0;
            totalHitsArray[algoIdx] = 0;
            totalMissesArray[algoIdx] = 0;

           switch (algo) {
               case FIFO_PAGE_REPLACEMENT:
                   prAlgo = new FIFOPageReplacement();
                   break;
               case RANDOM_PICK:
                   prAlgo = new RandomPickPageReplacement();
                   break;
               case LRU:
                   prAlgo = new LRUPageReplacement();
                   break;
               case LFU:
                   prAlgo = new LFUPageReplacement();
                   break;
               case MFU:
                   prAlgo = new MFUPageReplacement();
                   break;
           }
           hitsArray = new int[5];
           missesArray = new int[5];
           for (iteration = 0; iteration < NUM_ITERATIONS; iteration++) {
               hitsArray[iteration] = 0;
               missesArray[iteration] = 0;

               // start iteration of Simulation for a specific Page Replacement Algorithm
               mem = new Memory(prAlgo);

               for (Integer i = 0; i < NUM_PROCS; i++) {
                   // create new process and add to job queue
                   Random randSize = new Random();
                   Random randDuration = new Random();
                   Process proc = new Process("Proc_" + i.toString(), getProcessSize(randSize), System.nanoTime(), getServiceDuration(randDuration), mem);

                   jobQueue.add(proc);
               }
               long startTime = System.currentTimeMillis();
               long currentTime = startTime;

               logger.info(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSSSSS")) + " Started Simulation (iteration = " + iteration + ") with " + algo.toString());
               while (((currentTime - startTime) < (SIMULATION_DURATION * Math.pow(10, 3)))) { // in milliseconds


                   while (!jobQueue.isEmpty()) {
                       mem.mutex.acquire();
                       int freePageCount = (int) mem.freeList.stream().filter(page -> page.pid == -1).count();

                       if (freePageCount >= 4 && !jobQueue.isEmpty()) {
                           Process scheduledProc = jobQueue.poll();
                           // assign initial frames to the process
                           mem.assignInitialFrames(scheduledProc);
                           scheduledProc.start();

                           runningProcs.add(scheduledProc);
                       }
                       mem.mutex.release();
                   }
                   currentTime = System.currentTimeMillis();
               }

               logger.info(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSSSSS")) + " Ending Simulation (iteration = " + iteration + ") with " + algo.toString() + " Hits = " + hitsArray[iteration] + ", Misses = " + missesArray[iteration] + ", Hit-Miss ratio = " + (double)(hitsArray[iteration])/missesArray[iteration]);

            /* terminate the running Processes
               (unscheduled processes won't be scheduled now anyway) */
               for (Process proc : runningProcs) {
                   proc.stopProc();
               }
           }
           // calculate hit-miss ratio average for all iterations specific to each Page Replacement Algorithm
/*           double averageHitMissRatio = 0;
           int totalHits = 0;
           int totalMisses = 0;*/
           for (int i = 0; i < NUM_ITERATIONS; i++) {
               totalHitsArray[algoIdx] += hitsArray[i];
               totalMissesArray[algoIdx] += missesArray[i];
           }
           averageHitMissRatioArray[algoIdx] = (double) totalHitsArray[algoIdx]/totalMissesArray[algoIdx];

           //System.out.println(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSSSSS")) + "\nEnd of Simulation statistics for " + algo.toString() + " : Average Hit-Miss ratio across all iterations = " + averageHitMissRatio + "\n");
       }
        for (int i = 0; i < numAlgos; i++) {
            System.out.println(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSSSSS")) + "\nEnd of Simulation statistics for " + (PageReplacementAlgoEnum.values())[i] + " : Hits = " + totalHitsArray[i] + ", Misses = " + totalMissesArray[i] + ", Average Hit-Miss ratio across all iterations = " + averageHitMissRatioArray[i] + "\n");
        }

    }

}