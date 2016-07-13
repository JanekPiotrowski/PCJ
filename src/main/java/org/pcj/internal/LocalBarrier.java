/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.pcj.internal;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.pcj.PCJ;
import org.pcj.PcjFuture;
import org.pcj.PcjRuntimeException;

/**
 *
 * @author faramir
 */
public class LocalBarrier implements PcjFuture<Void> {

    private final int round;
    private final Object lock;
    private final Bitmask localBarrierBitmask;
    private final Bitmask localBarrierMaskBitmask;
    private boolean done;

    public LocalBarrier(int round, Bitmask localBitmask) {
        this.round = round;
        this.lock = new Object();
        this.localBarrierBitmask = new Bitmask(localBitmask.getSize());
        this.localBarrierMaskBitmask = new Bitmask(localBitmask);

        this.done = false;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof LocalBarrier == false) {
            return false;
        }
        return ((LocalBarrier) obj).round == this.round;
    }

    @Override
    public int hashCode() {
        return round;
    }

    public int getRound() {
        return round;
    }

    public void set(int index) {
        localBarrierBitmask.set(index);
    }

    public boolean isSet() {
        return localBarrierBitmask.isSet(localBarrierMaskBitmask);
    }

    public void signalAll() {
        synchronized (lock) {
            done = true;
            lock.notifyAll();
        }
    }

    @Override
    public boolean isDone() {
        synchronized (lock) {
            return done;
        }
    }

    @Override
    public Void get() throws PcjRuntimeException {
        System.err.println(round + "round -> done: " + done);
        synchronized (lock) {
            while (done == false) {
                try {
                    lock.wait();
                } catch (InterruptedException ex) {
                    throw new PcjRuntimeException(ex);
                }
            }
        }
        return null;
    }

    @Override
    public Void get(long timeout, TimeUnit unit) throws TimeoutException, PcjRuntimeException {
        long nanosTimeout = unit.toNanos(timeout);
        final long deadline = System.nanoTime() + nanosTimeout;

        synchronized (lock) {
            while (done == false) {
                if (nanosTimeout <= 0L) {
                    throw new TimeoutException("Not done yet.");
                }
                try {
                    lock.wait(nanosTimeout / 1_000_000, (int) (nanosTimeout % 1_000_000));
                } catch (InterruptedException ex) {
                    throw new PcjRuntimeException(ex);
                }
                nanosTimeout = deadline - System.nanoTime();
            }
        }
        return null;
    }
}
