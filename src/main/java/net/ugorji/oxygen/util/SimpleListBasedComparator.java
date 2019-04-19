/* <<< COPYRIGHT START >>>
 * Copyright 2006-Present OxygenSoftwareLibrary.com
 * Licensed under the GNU Lesser General Public License.
 * http://www.gnu.org/licenses/lgpl.html
 *
 * @author: Ugorji Nwoke
 * <<< COPYRIGHT END >>>
 */

package net.ugorji.oxygen.util;

import java.util.Comparator;
import java.util.List;

/**
 * Simple Comparator, that re-orders things, based on ensuring that the contents of its list are put
 * first.
 *
 * @author ugorji
 */
public class SimpleListBasedComparator implements Comparator {
  private List list;

  public SimpleListBasedComparator(List list0) {
    list = list0;
  }

  public int compare(Object o1, Object o2) {
    int a = list.indexOf(o1);
    int b = list.indexOf(o2);
    int rtn = 0;
    if (a != -1 && b != -1) {
      rtn = a - b;
    } else if (a != -1 && b == -1) {
      rtn = 1;
    } else if (a == -1 && b != -1) {
      rtn = -1;
    } else {
      // rtn = ((comparable)a).compareTo((comparable)b);
      rtn = 0;
    }
    return rtn;
  }
}
