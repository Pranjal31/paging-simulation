import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

public class RandomPickPageReplacement implements PageReplacementAlgorithm {
    private Random random = new Random();

    @Override
    public void replacePage(Process proc, Page newPage) throws RuntimeException {
        // Get a random index to choose a page to evict
        int randomIndex = random.nextInt(proc.procList.size());

        // Remove the randomly chosen page
        Page evictedPage = proc.procList.remove(randomIndex);

        if (evictedPage == null) {
            // Handle error
            System.out.println("Error: No page to evict in RandomPickPageReplacement");
            return;
        }

        evictedPage.pid = -1;
        evictedPage.inMemory = false;

        // insert new page in Queue
        newPage.inMemory = true;
        newPage.pid = proc.pid;
        proc.procList.add(newPage);

       Main.logger.info(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSSSSS")) +
                " Page replacement: Evicting Page " + evictedPage.pageNumber +
                " for " + proc.name +
                " and inserting new Page " + newPage.pageNumber +
                ", procList = [ " + proc.printProcList() + "]");
    }
}
