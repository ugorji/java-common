/* <<< COPYRIGHT START >>>
 * Copyright 2006-Present OxygenSoftwareLibrary.com
 * Licensed under the GNU Lesser General Public License.
 * http://www.gnu.org/licenses/lgpl.html
 *
 * @author: Ugorji Nwoke
 * <<< COPYRIGHT END >>>
 */

package net.ugorji.oxygen.util;

import java.util.BitSet;

public class OxyBitMask {
  private char[] rep;

  public OxyBitMask(char[] rep0) {
    rep = rep0;
  }

  public char[] getCharArray(BitSet bs, char nonSetChar) {
    char[] repx = new char[rep.length];
    for (int i = 0; i < repx.length; i++) {
      repx[i] = (bs.get(i) ? rep[i] : nonSetChar);
    }
    return repx;
  }
}
