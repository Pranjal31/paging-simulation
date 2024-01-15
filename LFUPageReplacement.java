import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.PriorityQueue;

public class LFUPageReplacement implements PageReplacementAlgorithm {
    // Helper method to get the LFU page from the process
    private Page getLFUPage(Process proc) {
        // Min heap
        PriorityQueue<Page> priorityQueue = new PriorityQueue<>((p1, p2) -> {
            int freq1 = proc.pageFrequencyMap.getOrDefault(p1, 0);
            int freq2 = proc.pageFrequencyMap.getOrDefault(p2, 0);
            return Integer.compare(freq1, freq2);
        });

        priorityQueue.addAll(proc.procList);

        return priorityQueue.poll();
    }
    @Override
    public void replacePage(Process proc, Page newPage) throws RuntimeException {
        // Evict the least frequently used Page
        Page evictedPage = getLFUPage(proc);
        proc.procList.remove(evictedPage);

        // Evict Page
        evictedPage.pid = -1;
        evictedPage.inMemory = false;

        // Insert new page in List
        newPage.inMemory = true;
        newPage.pid = proc.pid;
        proc.procList.add(newPage);

        Main.logger.info(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSSSSS")) +
                " Page replacement: Evicting Page " + evictedPage.pageNumber +
                " for " + proc.name +
                " and inserting new Page " + newPage.pageNumber +
                ", procList = [ " + proc.printProcList() + "]" +
                ", \nfreqMap = [ " + proc.printFreqMap() + "]\n");
    }


}
