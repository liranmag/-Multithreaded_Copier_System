
//liran magnezi
//ID: 205973605

import java.io.File;

public class DiskSearcher {

    public static final int DIRECTORY_QUEUE_CAPACITY = 50;
    public static final int RESULTS_QUEUE_CAPACITY = 50;
    public static final int MILESTONES_QUEUE_CAPACITY = 10000;

    public DiskSearcher() {
    }

    public static void main(String[] args) throws Exception {

        long start = System.nanoTime();


        boolean isMilestones = Boolean.valueOf(args[0]);
        String extension = args[1];
        File root = new File(args[2]);
        File destination = new File(args[3]);
        int numOfSearchers = Integer.parseInt(args[4]);
        int numOfCopiers = Integer.parseInt(args[5]);

        SynchronizedQueue<File> directoryQueue = new SynchronizedQueue<File>(DIRECTORY_QUEUE_CAPACITY);
        SynchronizedQueue<File> resultsQueue = new SynchronizedQueue<File>(RESULTS_QUEUE_CAPACITY);
        SynchronizedQueue<String> milestonesQueue = null;
        if (isMilestones) {
            milestonesQueue = new SynchronizedQueue<String>(MILESTONES_QUEUE_CAPACITY);
        }


        Thread scouterThread = new Thread(new Scouter(0, directoryQueue, root, milestonesQueue, isMilestones));

        scouterThread.start();

        Thread[] searchers = new Thread[numOfSearchers];
        for (int i = 0; i < numOfSearchers; i++) {
            searchers[i] = new Thread(
                    new Searcher(i, extension, directoryQueue, resultsQueue, milestonesQueue, isMilestones));
            searchers[i].start();
        }
        Thread[] copiers = new Thread[numOfCopiers];
        for (int i = 0; i < numOfCopiers; i++) {
            copiers[i] = new Thread(
                    new Copier(i + numOfSearchers + 1, destination, resultsQueue, milestonesQueue, isMilestones));
            copiers[i].start();
        }

        scouterThread.join();

        for (Thread thread : searchers) {
            thread.join();
        }
        for (Thread thread : copiers) {
            thread.join();
        }
        if (isMilestones) {
            String  milestone;
            while (( milestone = milestonesQueue.dequeue()) != null) {
                System.out.println( milestone);
            }
        }
        long end = System.nanoTime();
        System.out.println("running time milisec" +
                ": " + (end - start) / 1000);
    }
}
