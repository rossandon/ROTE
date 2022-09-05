package Utils;

public class AutoResetEvent {
    private final Object monitor = new Object();
    private volatile boolean open = false;

    public AutoResetEvent(boolean open) {
        this.open = open;
    }

    public void waitOne(long timeoutInMs) throws InterruptedException {
        synchronized (monitor) {
            while (open == false) {
                monitor.wait(timeoutInMs);
            }
            open = false; // close for other
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
