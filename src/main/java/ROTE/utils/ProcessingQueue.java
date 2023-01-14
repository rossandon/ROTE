package ROTE.utils;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

public class ProcessingQueue {
    private final Queue<Object> queue = new ArrayBlockingQueue<>(1024);

    public ProcessingQueueItem dequeue() {
        var message = queue.poll();
        if (message == null)
            return null;
        return (ProcessingQueueItem)message;
    }

    public Future queue(Object obj) {
        var future = new CompletableFuture();
        var item = new ProcessingQueueItem(future, obj);
        queue.add(item);
        return future;
    }

    public class ProcessingQueueItem {
        private final CompletableFuture future;
        private final Object obj;

        public ProcessingQueueItem(CompletableFuture future, Object obj) {
            this.future = future;
            this.obj = obj;
        }

        public void setResult(Object result) {
            future.complete(result);
        }

        public void setException(Exception e) {
            future.completeExceptionally(e);
        }

        public Object getObject() {
            return obj;
        }
    }
}
