/* <<< COPYRIGHT START >>>
 * Copyright 2006-Present OxygenSoftwareLibrary.com
 * Licensed under the GNU Lesser General Public License.
 * http://www.gnu.org/licenses/lgpl.html
 *
 * @author: Ugorji Nwoke
 * <<< COPYRIGHT END >>>
 */

package net.ugorji.oxygen.util;

/**
 * Helper class, which takes a string, and can convert it to primitive types
 *
 * @author ugorji
 */
public class StringToPrimitiveConverter {
  public static int toInt(String s) {
    return Integer.parseInt(s);
  }

  public static boolean toBoolean(String s) {
    boolean b = false;
    if (s != null && s.length() > 0) {
      s = s.toLowerCase().trim();
      if ("yes".startsWith(s)
          || "true".startsWith(s)
          || ("on".startsWith(s) && s.length() > 1)
          || s.equals("1")) {
        b = true;
      }
    }
    // System.out.println("b = " + b);
    return b;
  }

  public static double toDouble(String s) {
    return Double.parseDouble(s);
  }

  public static char toChar(String s) {
    return s.charAt(0);
  }
}
