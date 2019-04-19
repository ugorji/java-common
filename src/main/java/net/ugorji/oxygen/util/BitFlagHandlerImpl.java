/* <<< COPYRIGHT START >>>
 * Copyright 2006-Present OxygenSoftwareLibrary.com
 * Licensed under the GNU Lesser General Public License.
 * http://www.gnu.org/licenses/lgpl.html
 *
 * @author: Ugorji Nwoke
 * <<< COPYRIGHT END >>>
 */

package net.ugorji.oxygen.util;

public class BitFlagHandlerImpl implements BitFlagHandler {
  protected int flags = 0;

  public boolean isFlagSet(int flag) {
    return OxygenUtils.isFlagSet(flags, flag);
  }

  public void setFlag(int flag) {
    flags = OxygenUtils.setFlag(flags, flag);
  }

  public void clearFlag(int flag) {
    flags = OxygenUtils.clearFlag(flags, flag);
  }
}
