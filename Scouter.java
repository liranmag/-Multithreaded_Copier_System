
//liran magnezi
//ID: 205973605

import java.io.File;

public class Scouter implements Runnable {

    private int id;
    private SynchronizedQueue<File> directoryQueue;
    private File root;
    private SynchronizedQueue<String> milestonesQueue;
    private boolean isMilestones;


    public Scouter(int id, SynchronizedQueue<File> directoryQueue, File root,
                   SynchronizedQueue<String> milestonesQueue, boolean isMilestones) {
        this.id = id;
        this.directoryQueue = directoryQueue;
        this.root = root;
        this.milestonesQueue = milestonesQueue;
        this.isMilestones = isMilestones;
    }

    @Override
    public void run() {
        this.directoryQueue.registerProducer();
        if (this.isMilestones) {
            this.milestonesQueue.registerProducer();
            milestonesQueue.enqueue("General, program has started the search");
            this.milestonesQueue.unregisterProducer();
        }
        if (this.root != null) {
            this.directoryQueue.enqueue(this.root);
            if (isMilestones) {
                this.milestonesQueue.enqueue(
                        "Scouter on thread id " + this.id + ": directory named " + this.root.toString() + " was scouted");
            }
            RecOnDir(this.root);
        }
        this.directoryQueue.unregisterProducer();
    }

    public void RecOnDir(File file) {
        if (isMilestones) {
            this.milestonesQueue.enqueue(
                    "Scouter on thread id " + this.id + ": directory named " + file.toString() + " was scouted");
        }
        File[] listFiles = file.listFiles();
        if (listFiles != null) {
            for (File file_to_check : listFiles) {
                if (file_to_check.isDirectory()) {
                    this.directoryQueue.enqueue(file_to_check);
                    RecOnDir(file_to_check);
                }
            }
        }
    }
}



