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
import java.lang.reflect.Array;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/*
 * This proxy class, only handles null appropriately for the following primitives
 * - integer, long, char, boolean (the common ones used in the Oxygen Codebase)
 */
public class OxygenProxy implements InvocationHandler, Serializable {
  public static Integer NULL_INTEGER = new Integer(0);
  public static Long NULL_LONG = new Long(0);
  public static Character NULL_CHAR = new Character('\0');

  protected Object target;
  protected Object proxy;

  public OxygenProxy(Object obj, Class[] interfaces) {
    target = obj;
    ClassLoader cl =
        ((obj == null)
            ? Thread.currentThread().getContextClassLoader()
            : obj.getClass().getClassLoader());
    proxy = Proxy.newProxyInstance(cl, interfaces, this);
  }

  public OxygenProxy(Object obj) {
    this(obj, obj.getClass().getInterfaces());
  }

  public Object getTarget() {
    return target;
  }

  public Object getProxy() {
    return proxy;
  }

  public Object invoke(Object proxy00, Method m, Object[] args) throws Exception {
    try {
      return doInvoke(m, args);
    } catch (InvocationTargetException e) {
      Throwable ee = e.getTargetException();
      if (ee instanceof Exception) throw (Exception) ee;
      throw e; // if underlying exception is a Throwable, just rethrow it
    }
  }

  protected Object doInvoke(Method m, Object[] args) throws Exception {
    return ((target == null) ? getNullReturnValue(m.getReturnType()) : m.invoke(target, args));
  }

  public static Object getNullReturnValue(Class c) {
    // System.out.println("c: " + c.getName() + " -- " + c);
    Object o = null;
    if (c == null) {
      o = null;
    } else if (c == Integer.TYPE) {
      o = NULL_INTEGER;
    } else if (c == Long.TYPE) {
      o = NULL_LONG;
    } else if (c == Boolean.TYPE) {
      o = Boolean.FALSE;
    } else if (c == Character.TYPE) {
      o = NULL_CHAR;
    } else if (c.isArray()) {
      o = Array.newInstance(c.getComponentType(), 0);
    }
    // System.out.println("getNullReturnValue: " + o);
    return o;
  }

  public static Object getTargetGivenProxy(Object proxy) {
    OxygenProxy op = (OxygenProxy) Proxy.getInvocationHandler(proxy);
    return op.getTarget();
  }
}
