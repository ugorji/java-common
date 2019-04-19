/* <<< COPYRIGHT START >>>
 * Copyright 2006-Present OxygenSoftwareLibrary.com
 * Licensed under the GNU Lesser General Public License.
 * http://www.gnu.org/licenses/lgpl.html
 *
 * @author: Ugorji Nwoke
 * <<< COPYRIGHT END >>>
 */

package net.ugorji.oxygen.util;

public class OxygenObjectArray {
  private Object[] oa = null;

  public OxygenObjectArray(Object[] obj) {
    oa = obj;
  }

  public int hashCode() {
    int x = 17;
    for (int i = 0; i < oa.length; i++) {
      x = x ^ oa[i].hashCode();
    }
    return x;
  }

  public boolean equals(Object o) {
    if (!(o instanceof OxygenObjectArray)) {
      System.out.println("OxygenObjectArray: not an OxygenObjectArray");
      return false;
    } else {
      Object[] o2 = ((OxygenObjectArray) o).get();
      if (o2.length != oa.length) {
        System.out.println("OxygenObjectArray: not same length");
        return false;
      } else {
        for (int i = 0; i < o2.length; i++) {
          if (!(oa[i].equals(o2[i]))) {
            System.out.println("OxygenObjectArray: not equal: " + oa[i] + " ... " + o2[i]);
            return false;
          }
        }
      }
    }
    return true;
  }

  public Object[] get() {
    return oa;
  }

  public Object get(int i) {
    return oa[i];
  }
}
