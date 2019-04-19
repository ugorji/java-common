/* <<< COPYRIGHT START >>>
 * Copyright 2006-Present OxygenSoftwareLibrary.com
 * Licensed under the GNU Lesser General Public License.
 * http://www.gnu.org/licenses/lgpl.html
 *
 * @author: Ugorji Nwoke
 * <<< COPYRIGHT END >>>
 */

package net.ugorji.oxygen.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Encapsulates a method, and the arguments used to execute it.
 *
 * @author ugorji
 */
public class MethodEncapsulator {
  private Method _method;
  private Object _instance;
  private Object[] args;

  public MethodEncapsulator(Method _method, Object _instance, Object[] args) {
    this._method = _method;
    this._instance = _instance;
    this.args = args;
  }

  public Object execute() throws Exception {
    try {
      Object rtn = _method.invoke(_instance, args);
      return rtn;
    } catch (InvocationTargetException ite) {
      Throwable thr = ite.getTargetException();
      if (thr != null && thr instanceof Exception) {
        throw (Exception) thr;
      } else {
        throw ite;
      }
    }
  }

  public static MethodEncapsulator getMethodEncapsulator(
      Object _instance, String methodclassname, String methodname, Object[] args) throws Exception {
    return getMethodEncapsulator(
        _instance, OxygenUtils.getClass(methodclassname), methodname, args);
  }

  public static MethodEncapsulator getMethodEncapsulator(
      Object _instance, Class methodclazz, String methodname, Object[] args) throws Exception {
    Class[] paramtypes = new Class[args.length];
    for (int i = 0; i < args.length; i++) {
      paramtypes[i] = Object.class;
      if (args[i] != null) {
        paramtypes[i] = args[i].getClass();
      }
    }
    Method method = methodclazz.getMethod(methodname, paramtypes);
    return new MethodEncapsulator(method, _instance, args);
  }
}
