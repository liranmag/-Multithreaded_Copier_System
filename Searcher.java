
//liran magnezi
//ID: 205973605

import java.io.File;

public class Searcher implements Runnable {

    private int id;
    private String extension;
    private SynchronizedQueue<File> directoryQueue;
    private SynchronizedQueue<File> resultsQueue;
    private SynchronizedQueue<String> milestonesQueue;
    private boolean isMilestones;

    public Searcher(int id, String extension, SynchronizedQueue<File> directoryQueue,
            SynchronizedQueue<File> resultsQueue, SynchronizedQueue<String> milestonesQueue, boolean isMilestones) {
        this.id = id;
        this.extension = extension;
        this.directoryQueue = directoryQueue;
        this.resultsQueue = resultsQueue;
        this.milestonesQueue = milestonesQueue;
        this.isMilestones = isMilestones;
    }

    @Override
    public void run() {
        this.resultsQueue.registerProducer();
        File dir;
        while ((dir = this.directoryQueue.dequeue()) != null) {
            File[] listFiles = dir.listFiles();
            if (listFiles == null) {
                continue;
            }
            for (File file_to_check : listFiles) {
                if (file_to_check.getName().endsWith(this.extension)) {
                    if (this.isMilestones) {
                        this.milestonesQueue.registerProducer();
                        this.milestonesQueue.enqueue(
                                "Searcher on thread id " + this.id + ": file_to_check named " + file_to_check.getName() + " was found");
                        this.milestonesQueue.unregisterProducer();
                    }
                    this.resultsQueue.enqueue(file_to_check);
                }
            }
        }
        this.resultsQueue.unregisterProducer();
    }

}
