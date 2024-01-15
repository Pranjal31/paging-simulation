import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Process extends Thread {
    String name;
    Integer numPages;
    long arrivalTime;   // in nanoseconds
    long serviceDuration; // in seconds
    int pid;
    int currReference = 0;
    List<Page> pagesInMemory;
    Memory mem;
    long hit = 0;
    long miss = 0;
    private volatile boolean running = true;

    // Data structures for Page Replacement
    Queue<Page> procQueue;   // Used for FIFO
    List<Page> procList;     // Used for LRU, LFU, MFU, and Random Pick
    Map<Page, Integer> pageFrequencyMap;  // Used for LFU and MFU

    public Process(String name, Integer numPages, long arrivalTime, long serviceDuration, Memory mem) {
        this.name = name;
        this.numPages = numPages;
        this.arrivalTime = arrivalTime;
        this.serviceDuration = serviceDuration;
        this.pagesInMemory = new ArrayList<>();
        int hash = name.hashCode();
        this.pid = (hash > 0 ? hash: -hash);
        this.mem = mem;
        for (int i = 0; i < numPages; i++) {
            this.pagesInMemory.add(new Page(i, this.pid));
        }
        // Initialize Data structures for Page Replacement
        this.procQueue = new LinkedList<>();
        this.procList = new ArrayList<>();
        this.pageFrequencyMap = new HashMap<>();
    }
    public String printProcQueue() {
        StringBuilder sb = new StringBuilder();
        for (Page pg : procQueue) {
            sb.append(pg.toString() + " ");
        }
        return sb.toString();
    }
    public String printProcList() {
        StringBuilder sb = new StringBuilder();
        for (Page pg : procList) {
            sb.append(pg.toString() + " ");
        }
        return sb.toString();
    }

    public String printFreqMap() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n");
        for (Map.Entry<Page, Integer> e : pageFrequencyMap.entrySet()) {
            sb.append("(Page = " + e.getKey().toString() + " Frequency = " + e.getValue().toString() + ")\n");
        }
        return sb.toString();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Page pg : pagesInMemory) {
            if (pg.inMemory == true) {
                sb.append(pg.toString() + " ");
            }
        }
        return "name = " + this.name + ", numPages = " + this.numPages + ", arrivalTime = " + this.arrivalTime + ", serviceDuration = " + this.serviceDuration + ", pid = " + this.pid + ", pagesInMemory = [ " + sb.toString() + "]";
    }

    public void stopProc() {
        running = false;
    }

    // Locality of Reference algorithm
    private int getDeltaUsingLOR() {
        Random rand = new Random();
        int r = rand.nextInt(10);

        if (r < 7) {
            return rand.nextInt(3) - 1;   // -1, 0, 1
        } else {
            int range1Start = -9;
            int range1End = -2;

            int range2Start = 2;
            int range2End = 9;
            int subRange =  rand.nextInt(2);
            if (subRange == 0) {
                return rand.nextInt(range1End - range1Start + 1) + range1Start;  // - 9 to -2
            } else {
                return rand.nextInt(range2End - range2Start + 1) + range2Start;  // 2 to 9
            }
        }
    }
    public Page getNextPageReference () {
        Page pageReference = null;
        int newReference = 0;

        /* Logic to control whether to use:
         *   a) LOR algorithm on Page Reference  i.e. abs(i) < 10   OR
         *   b) LOR algorithm on Page Reference Delta i.e. abs(delta(i)) < 10
         */

        if (Main.LOR_ON_PAGE == 1) {
            newReference = Math.abs(getDeltaUsingLOR());
        } else {
            newReference = currReference + getDeltaUsingLOR();
            while (newReference < 0) {
                newReference += numPages;
            }
        }

        currReference = newReference % numPages;
        pageReference = pagesInMemory.get(currReference);

        // Handling specific to Page Replacement algorithms
        if (this.mem.pageReplacementAlgorithm instanceof FIFOPageReplacement) {  // FIFO
            Main.logger.info(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSSSSS")) + " Reference to Page " + pageReference + " for " + this.name + " , procQueue = [ " + printProcQueue() + "]");
        } else if ((this.mem.pageReplacementAlgorithm instanceof RandomPickPageReplacement) || (this.mem.pageReplacementAlgorithm instanceof LRUPageReplacement)) {   // Random Pick or LRU
            Main.logger.info(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSSSSS")) + " Reference to Page " + pageReference + " for " + this.name + " , procList = [ " + printProcList() + "]");
        } else if ((this.mem.pageReplacementAlgorithm instanceof LFUPageReplacement) || (this.mem.pageReplacementAlgorithm instanceof MFUPageReplacement)) {  // LFU or MFU
            if (!pageFrequencyMap.containsKey(pageReference)) {
                pageFrequencyMap.put(pageReference, 1);
            } else {
                pageFrequencyMap.put(pageReference, pageFrequencyMap.get(pageReference) + 1);
            }
            Main.logger.info(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSSSSS")) + " Reference to Page " + pageReference + " for " + this.name  +" procList = [ " + printProcList() + "], \nFreqMap = [ " + printFreqMap() + "]\n");
        }
        return pageReference;
    }

    @Override
    public void run() throws RuntimeException {
        Main.logger.info(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSSSSS")) + " Starting job: " + this.toString());
         long currTime = 0;
        do {
            currTime = System.nanoTime();

            Page refPage = getNextPageReference();

            if (refPage.inMemory) {  // page already in memory
                Main.hitsArray[Main.iteration] = Main.hitsArray[Main.iteration] + 1;

                // Special handling for LRU only
                if (this.mem.pageReplacementAlgorithm instanceof LRUPageReplacement) {
                    if (procList.contains(refPage)) {
                        // remove from list and add at the end (most recently used)
                        procList.remove(refPage);
                        procList.add(refPage);
                    }
                }
            } else if (this.pagesInMemory.stream().filter(page -> page.inMemory == true).count() < 4) {   // Page fault: page not in memory, but space is available to bring it
                // assign free frame to process
                mem.assignFrame(this);
                Main.missesArray[Main.iteration] = Main.missesArray[Main.iteration] + 1;
            } else {    //  Page fault: page not in memory, and no space available.
                // perform page replacement
                mem.pageReplacementAlgorithm.replacePage(this,refPage);
                Main.missesArray[Main.iteration] = Main.missesArray[Main.iteration] + 1;
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {}
        } while ( (currTime - arrivalTime < (serviceDuration * (long)(Math.pow(10,9)))) && (running == true));

        // Work completed, release memory-resident pages
        while (!(this.pagesInMemory.isEmpty())) {
            Page pg = this.pagesInMemory.remove(0);

            try {
                this.mem.mutex.acquire();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            for (Page frame : mem.freeList) {
                if (frame.pid == this.pid) {
                    frame.pid = -1;   // marked free
                    frame.inMemory = false;
                }
            }
            this.mem.mutex.release();
        }
        Main.logger.info(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSSSSS")) + " Completed job: " + this.toString());

    }

}
