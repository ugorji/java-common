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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Takes a range like: 1-5,10,17-19,55- This class does not check to ensure that your range is good
 * So users must try to enter good ranges. A range only consists of positive values, and the values
 * are always in ascending order
 *
 * <p>As as example, the meaning of this is below: Sample: 1-5,10,17-19,55- Meaning: 1, 2, 3, 4, 5,
 * 10, 17, 18, 19, 55, 56, 57, 58, 59, 60, 61 ... Integer.MAX_VALUE
 *
 * <p>This class is NOT thread-safe
 */
public class OxygenIntRange implements Serializable {
  private int ptr = 0;
  private OIR[] oirs;
  // e.g. 1-5,6,7-9,20 OR
  public OxygenIntRange(String rangeStr) {
    String[] sa = rangeStr.split(",");
    oirs = new OIR[sa.length];
    List mylist = new ArrayList(sa.length);
    int lastmax = Integer.MIN_VALUE;
    OIR aoir = null;
    for (int i = 0; i < sa.length; i++) {
      if (StringUtils.isBlank(sa[i])) {
        continue;
      }
      aoir = new OIR();
      int idx = sa[i].indexOf('-', 1);
      if (idx == -1) {
        aoir.min = aoir.max = toInt(sa[i]);
      } else {
        aoir.min = toInt(sa[i].substring(0, idx));
        aoir.max = toInt(sa[i].substring(idx + 1));
      }
      if (aoir.min < 0 || aoir.max < 0 || aoir.min > aoir.max || aoir.max <= lastmax) {
        throw new RuntimeException();
      }
      lastmax = aoir.max;
      mylist.add(aoir);
    }
    oirs = (OIR[]) mylist.toArray(new OIR[0]);
  }

  public OxygenIntRange(int[] arr0) {
    int[] arr = new int[arr0.length];
    System.arraycopy(arr0, 0, arr, 0, arr.length);
    Arrays.sort(arr);
    oirs = new OIR[arr.length];
    for (int i = 0; i < arr.length; i++) {
      oirs[i] = new OIR();
      oirs[i].min = oirs[i].max = arr[i];
    }
  }

  public int[] getRanges() {
    int[] ii = new int[oirs.length * 2];
    int j = 0;
    for (int i = 0; i < oirs.length; i++) {
      ii[j++] = oirs[i].min;
      ii[j++] = oirs[i].max;
    }
    return ii;
  }

  public boolean find(int i, boolean doReset) {
    if (doReset) reset();
    boolean b = false;
    while (hasNext()) {
      int j = next();
      if (j == i) {
        b = true;
        break;
      } else if (j > i) {
        break;
      }
    }
    if (doReset) reset();
    return b;
  }

  public void reset() {
    ptr = 0;
    for (int i = 0; i < oirs.length; i++) {
      oirs[i].curr = Integer.MIN_VALUE;
    }
  }

  public int next() {
    boolean b = oirs[ptr].hasNext();
    if (!b && (ptr + 1) < oirs.length) {
      ptr++;
    }
    return oirs[ptr].next();
  }

  public boolean hasNext() {
    boolean b = oirs[ptr].hasNext();
    if (!b && (ptr + 1) < oirs.length) {
      b = oirs[ptr + 1].hasNext();
    }
    return b;
  }

  public String toString() {
    StringBuffer buf = new StringBuffer();
    for (int i = 0; i < oirs.length; i++) {
      if (i > 0) {
        buf.append(",");
      }
      buf.append(oirs[i].min);
      if (oirs[i].max != oirs[i].min) {
        buf.append("-").append(oirs[i].max);
      }
    }
    return buf.toString();
  }

  private static int toInt(String s) {
    int i = Integer.MAX_VALUE;
    if (!StringUtils.isBlank(s)) {
      i = Integer.parseInt(s);
    }
    return i;
  }

  private static class OIR {
    private int min = 0;
    private int max = 0;
    private int curr = Integer.MIN_VALUE;

    private int next() {
      if (curr < min) {
        curr = min;
      } else if (curr >= max) {
        throw new RuntimeException();
      } else {
        curr++;
      }
      return curr;
    }

    private boolean hasNext() {
      return (curr < max);
    }
  }
}
