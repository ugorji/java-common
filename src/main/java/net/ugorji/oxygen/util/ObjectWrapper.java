/* <<< COPYRIGHT START >>>
 * Copyright 2006-Present OxygenSoftwareLibrary.com
 * Licensed under the GNU Lesser General Public License.
 * http://www.gnu.org/licenses/lgpl.html
 *
 * @author: Ugorji Nwoke
 * <<< COPYRIGHT END >>>
 */

package net.ugorji.oxygen.util;

public class ObjectWrapper {
  private Object o;

  public ObjectWrapper(Object _obj) {
    o = _obj;
  }

  public String toString() {
    return String.valueOf(o);
  }

  public String str() {
    return toString();
  }

  public Object obj() {
    return o;
  }

  public void setObject(Object _o) {
    o = _o;
  }
}
