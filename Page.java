public class Page {
    int pageNumber;
    int pid;
    boolean inMemory;


    public String toString() {
        return String.valueOf(pageNumber);
    }
    public Page(int pageNumber, int pid) {
        this.pageNumber = pageNumber;
        this.pid = pid;
        this.inMemory = false;
    }
}