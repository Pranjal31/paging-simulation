import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LRUPageReplacement implements PageReplacementAlgorithm {
    @Override
    public void replacePage(Process proc, Page newPage) throws RuntimeException {
        // Page is not in the queue, evict the least recently used page
        Page evictedPage = proc.procList.remove(0);  // first element is the least recently used element

        // Evict Page
        evictedPage.pid = -1;
        evictedPage.inMemory = false;

        // Insert new page in List
        newPage.inMemory = true;
        newPage.pid = proc.pid;
        proc.procList.add(newPage); // add new page at the end (most recently used)

        Main.logger.info(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSSSSS")) +
                " Page replacement: Evicting Page " + evictedPage.pageNumber +
                " for " + proc.name +
                " and inserting new Page " + newPage.pageNumber +
                ", procList = [ " + proc.printProcList() + "]");
    }
}
