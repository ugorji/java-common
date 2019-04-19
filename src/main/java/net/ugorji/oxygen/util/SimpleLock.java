/* <<< COPYRIGHT START >>>
 * Copyright 2006-Present OxygenSoftwareLibrary.com
 * Licensed under the GNU Lesser General Public License.
 * http://www.gnu.org/licenses/lgpl.html
 *
 * @author: Ugorji Nwoke
 * <<< COPYRIGHT END >>>
 */

package net.ugorji.oxygen.util;

import java.io.Serializable;

/**
 * A strict SimpleLock is one that - the Thread the acquired the lock is the only thread that can
 * release it - A thread has to wait for everyone to release the lock before it registers itself as
 * the owner of the lock For a non-strict SimpleLock - a call to holds will just ensure that someone
 * is holding onto the lock - anyone can release the lock (not just the thread that acquired it)
 */
public class SimpleLock implements Serializable, Closeable {
  protected boolean held = false;
  protected String holdMessage;

  public boolean isHeld() {
    return held;
  }

  public String getHoldMessage() {
    return holdMessage;
  }

  public synchronized void waitTillReleased() {
    if (held) {
      try {
        wait();
      } catch (InterruptedException exc) {
      }
      waitTillReleased();
    }
  }

  public synchronized void hold(String msg) {
    held = true;
    holdMessage = msg;
  }

  public synchronized void release() {
    forceRelease();
  }

  public synchronized void forceRelease() {
    if (held) {
      held = false;
      holdMessage = null;
      notifyAll();
    }
  }

  public void close() {
    forceRelease();
  }

  public static class Strict extends SimpleLock {
    private Thread currentLockHolder;

    public synchronized void hold(String msg) {
      Thread currThread = Thread.currentThread();
      if (held) {
        if (currThread != currentLockHolder) {
          waitTillReleased();
          hold(msg);
        }
        return;
      }
      currentLockHolder = currThread;
      // System.out.println("--- Now Hold (Strict) Lock: Thread.currentThread(): " + currThread);
      super.hold(msg);
    }

    public synchronized void release() {
      if (held) {
        // System.out.println("--- Try to release (Strict) Lock: Thread.currentThread(): " +
        // Thread.currentThread() + ", currentLockHolder: " + currentLockHolder);
        if (Thread.currentThread() != currentLockHolder) {
          throw new IllegalStateException(
              "This is a strict SimpleLock. Only the thread which holds the lock can release it.");
        }
        currentLockHolder = null;
        super.release();
      }
    }
  }
}
