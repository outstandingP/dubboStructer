/**
 * Copyright 2016 Nikita Koksharov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.redisson.pubsub;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

/**
 * 
 * @author Nikita Koksharov
 *
 */
public class AsyncSemaphore {

    private int counter;
    private final Set<Runnable> listeners = new LinkedHashSet<Runnable>();

    public AsyncSemaphore(int permits) {
        counter = permits;
    }
    
    public void acquireUninterruptibly() {
        final CountDownLatch latch = new CountDownLatch(1);
        acquire(new Runnable() {
            @Override
            public void run() {
                latch.countDown();
            }
        });
        
        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public int queueSize() {
        synchronized (this) {
            return listeners.size();
        }
    }
    
    public void acquire(Runnable listener) {
        boolean run = false;
        
        synchronized (this) {
            if (counter == 0) {
                listeners.add(listener);
                return;
            }
            if (counter > 0) {
                counter--;
                run = true;
            }
        }
        
        if (run) {
            listener.run();
        }
    }
    
    public boolean remove(Runnable listener) {
        synchronized (this) {
            return listeners.remove(listener);
        }
    }

    public int getCounter() {
        return counter;
    }
    
    public void release() {
        Runnable runnable = null;
        
        synchronized (this) {
            counter++;
            Iterator<Runnable> iter = listeners.iterator();
            if (iter.hasNext()) {
                runnable = iter.next();
                iter.remove();
            }
        }
        
        if (runnable != null) {
            acquire(runnable);
        }
    }
    
}
