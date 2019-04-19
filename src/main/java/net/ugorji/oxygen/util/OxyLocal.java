/* <<< COPYRIGHT START >>>
 * Copyright 2006-Present OxygenSoftwareLibrary.com
 * Licensed under the GNU Lesser General Public License.
 * http://www.gnu.org/licenses/lgpl.html
 *
 * @author: Ugorji Nwoke
 * <<< COPYRIGHT END >>>
 */

package net.ugorji.oxygen.util;

import java.util.HashMap;

/**
 * We only use Class objects are keys here, as a Class can only put a copy of something here withing
 * the context of a Thread Request
 */
public class OxyLocal {
  private static ThreadLocal threadlocal = new ThreadLocal();

  public static void clear() {
    HashMap m = map0();
    m.clear();
    // threadlocal.set(null);
  }

  public static void set(Class c, Object o) {
    set0(key0(c), o);
  }

  public static Object get(Class c) {
    return get0(key0(c));
  }

  private static void set0(String key, Object o) {
    HashMap m = map0();
    if (o == null) {
      m.remove(key);
    } else {
      m.put(key, o);
    }
    // threadlocal.set(m);
  }

  private static Object get0(String key) {
    return map0().get(key);
  }

  private static HashMap map0() {
    HashMap m = (HashMap) threadlocal.get();
    if (m == null) {
      m = new HashMap();
      threadlocal.set(m);
    }
    return m;
  }

  private static String key0(Class c) {
    return c.getName();
  }
}

/*
  public static void set(String c, Object o) {
    set0(key0(c), o);
  }

  public static Object get(String c) {
    return get0(key0(c));
  }

  private static String key0(String s) {
    return s;
  }

*/
