package shared.utils;

public class ManualResetEvent {
    private final Object monitor = new Object();
    private volatile boolean open;

    public ManualResetEvent(boolean open) {
        this.open = open;
    }

    public void waitOne(long timeoutInMs) throws InterruptedException {
        synchronized (monitor) {
            while (!open) {
                monitor.wait(timeoutInMs);
            }
        }

    }

    public void set() {
        synchronized (monitor) {
            open = true;
            monitor.notify(); // open one
        }
    }

    public void reset() {//close stop
        open = false;
    }
}
