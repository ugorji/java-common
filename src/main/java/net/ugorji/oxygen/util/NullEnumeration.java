/* <<< COPYRIGHT START >>>
 * Copyright 2006-Present OxygenSoftwareLibrary.com
 * Licensed under the GNU Lesser General Public License.
 * http://www.gnu.org/licenses/lgpl.html
 *
 * @author: Ugorji Nwoke
 * <<< COPYRIGHT END >>>
 */

package net.ugorji.oxygen.util;

import java.util.Enumeration;

public class NullEnumeration implements Enumeration {
  public boolean hasMoreElements() {
    return false;
  }

  public Object nextElement() {
    return null;
  }
}
