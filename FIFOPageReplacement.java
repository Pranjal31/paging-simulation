import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FIFOPageReplacement implements PageReplacementAlgorithm {
    @Override
        public void replacePage(Process proc, Page newPage) throws RuntimeException {
        // evict Page
        Page evictedPage = proc.procQueue.poll();
        if (evictedPage == null) {
            System.out.println("Error: No page to evict in FIFOPageReplacement");
        }
        evictedPage.pid = -1;
        evictedPage.inMemory = false;

        // insert new page in Queue
        newPage.inMemory = true;
        newPage.pid = proc.pid;
        proc.procQueue.offer(newPage);

        Main.logger.info(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSSSSS")) + " Page replacement: Evicting Page " + evictedPage.pageNumber +
                " for " + proc.name +
                " and inserting new Page " + newPage.pageNumber +
                ", procQueue = [ " + proc.printProcQueue() + "]");
    }
}


