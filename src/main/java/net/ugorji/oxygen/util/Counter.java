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
 * Simple Counter with synchronization built-in, to allow us handle things like reference counting,
 * etc.
 *
 * @author ugorjid
 */
public class Counter implements Serializable {
  private int i;
  private int threshhold = 0;

  public Counter() {
    this(0, 0);
  }

  public Counter(int start, int threshhold0) {
    i = start;
    threshhold = threshhold0;
  }

  public int getThreshold() {
    return threshhold;
  }

  public int get() {
    return i;
  }

  public synchronized int increment() {
    return set(i + 1);
  }

  public synchronized int decrement() {
    return set(i - 1);
  }

  public synchronized int set(int j) {
    i = j;
    if (i == threshhold) {
      notifyAll();
    }
    return i;
  }

  public synchronized void waitTillThreshhold() {
    if (i == threshhold) return;
    try {
      wait();
    } catch (InterruptedException exc) {
    }
    waitTillThreshhold();
  }
}
