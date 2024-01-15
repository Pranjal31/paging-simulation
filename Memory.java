import java.util.*;
import java.util.concurrent.Semaphore;

public class Memory {
    static final int MEM_SIZE = 100; // 100 MB
    static final int PAGE_SIZE = 1; // 1 MB
    static final int MIN_PAGES = 4;

    List<Page> freeList = new ArrayList<>();
    Semaphore mutex = new Semaphore(1);
    PageReplacementAlgorithm pageReplacementAlgorithm;

    public Memory (PageReplacementAlgorithm prAlgo) {
        this.pageReplacementAlgorithm = prAlgo;

        // Initialize free pages
        for(int i = 0; i < MEM_SIZE/PAGE_SIZE; i++) {
            Page page = new Page(i, -1); // -1 represents a free frane
            freeList.add(page);
        }
    }
    private static int getFrameDelta() {
        Random rand = new Random();
        return rand.nextInt(100);
    }
    private Page getNextFreeFrame() throws RuntimeException {
        int idx = 0;
        Page frame = freeList.get(idx++);  // get frame from head of the list

        while ((frame != null) && (frame.pid != -1)) {
            frame = freeList.get(idx++);
        }

        if (frame == null) {
            throw new RuntimeException();
        }
        return frame; // free frame
    }
    public void assignFrame(Process proc) {
        // Assign Page from proc's virtual memory
        Page pg =  proc.getNextPageReference();
        while ((pg != null) && (pg.inMemory == true)) {
            Main.hitsArray[Main.iteration] = Main.hitsArray[Main.iteration] + 1;
            pg =  proc.getNextPageReference();
        }
        if (pg == null) {
            throw new RuntimeException();
        }
        // Page fault: this page is not in memory
        Main.missesArray[Main.iteration] = Main.missesArray[Main.iteration] + 1;

        pg.inMemory = true;
        pg.pid = proc.pid;

        // insert into fifoQueue
        proc.procQueue.add(pg);

        // insert into rpList
        proc.procList.add(pg);

        // Corresponding bookkeeping for free frames
        try{
            Page frame = getNextFreeFrame();

            frame.pid = proc.pid;
            frame.inMemory = true;
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }
    public void assignInitialFrames(Process proc) {
        for(int i = 0; i < MIN_PAGES; i++) {
            assignFrame(proc);
        }
    }
}
