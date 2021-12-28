
//liran magnezi
//ID: 205973605

import java.io.*;

public class Copier implements Runnable {

    private int id;
    private File destination;
    private SynchronizedQueue<File> resultsQueue;
    private SynchronizedQueue<String> milestonesQueue;
    private boolean isMilestones;
    public static final int COPY_BUFFER_SIZE = 4096;

    public Copier(int id, File destination, SynchronizedQueue<File> resultsQueue,
                  SynchronizedQueue<String> milestonesQueue, boolean isMilestones) {
        this.id = id;
        this.destination = destination;
        this.resultsQueue = resultsQueue;
        this.milestonesQueue = milestonesQueue;
        this.isMilestones = isMilestones;
    }

    @Override
    public void run() {
        byte[] buffer = new byte[COPY_BUFFER_SIZE];
        int length;
        File filToCopy;
        InputStream inputStreamCopy = null;
        OutputStream OutputStreamTowrite = null;
        while ((filToCopy = this.resultsQueue.dequeue()) != null) {
            try {
                inputStreamCopy = new FileInputStream(filToCopy);
                String name = filToCopy.getName();
                OutputStreamTowrite = new FileOutputStream(new File(this.destination, name));
                if (this.isMilestones) {
                    this.milestonesQueue.registerProducer();
                    this.milestonesQueue.enqueue(
                            "Copier from thread id " + this.id + ": filToCopy named " + filToCopy.getName() + " was copied");
                    this.milestonesQueue.unregisterProducer();
                }
                while ((length = inputStreamCopy.read(buffer)) > 0) {
                    OutputStreamTowrite.write(buffer, 0, length);
                }
                inputStreamCopy.close();
                OutputStreamTowrite.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

