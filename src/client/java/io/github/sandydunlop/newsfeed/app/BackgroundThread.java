package io.github.sandydunlop.newsfeed.app;

public class BackgroundThread extends Thread {
    private final Runnable runnable;

    public BackgroundThread() {
        this.runnable = () -> {
            try {
                // Placeholder for background task
                System.out.println("Background thread: tick");
                Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
    }

    // public BackgroundThread(Runnable runnable) {
    //     this.runnable = runnable;
    // }

    @Override
    public void run() {
        try {
            runnable.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
