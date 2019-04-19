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
 * Simple Counter which is not thread-safe.
 *
 * @author ugorjid
 */
public class SimpleInt implements Serializable {
  private int i;

  public int get() {
    return i;
  }

  public int increment() {
    return ++i;
  }

  public int decrement() {
    return --i;
  }

  public int set(int j) {
    i = j;
    return i;
  }

  public String toString() {
    return String.valueOf(i);
  }
}
