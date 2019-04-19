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
import java.util.List;

/**
 * Very Simplistic implementation. Only to be used by careful classes.
 *
 * @author Ugorji Nwoke ugorji@gmail.com
 * @version 1.0, Nov 3, 2001
 */
public class ListMap implements Serializable {
  private List keys = new ArrayList();
  private List values = new ArrayList();

  public Object getKey(int i) {
    return keys.get(i);
  }

  public Object getValue(int i) {
    return values.get(i);
  }

  /** Put in a key/value pair into the listmap */
  public void put(Object key, Object value) {
    if (key == null) return;
    keys.add(key);
    values.add(value);
  }

  /** Get all the values mapped to a key. If key passed in is null, return null. */
  public Object[] getValues(Object obj) {
    if (obj == null) return null;
    List list = new ArrayList();
    int sz = keys.size();
    for (int i = 0; i < sz; i++) {
      if (obj.equals(keys.get(i))) list.add(values.get(i));
    }
    return list.toArray();
  }

  /**
   * Get the first value mapped to a key. If key passed in is null, return null. If nothing is
   * found, return null.
   */
  public Object getValue(Object obj) {
    if (obj == null) return null;
    int sz = keys.size();
    for (int i = 0; i < sz; i++) {
      if (obj.equals(keys.get(i))) return (values.get(i));
    }
    return null;
  }

  /**
   * return the number of mappings in here. If abc is mapped to a, b, c, d, and e -> the size is 5.
   */
  public int size() {
    return Math.min(keys.size(), values.size());
  }

  public String toString() {
    // return super.toString();
    int sz = size();
    StringBuffer buf = new StringBuffer();
    buf.append("[");
    for (int i = 0; i < sz; i++) {
      buf.append(getKey(i)).append("=").append(getValue(i)).append(",");
    }
    buf.append("]");
    return buf.toString();
  }
}
