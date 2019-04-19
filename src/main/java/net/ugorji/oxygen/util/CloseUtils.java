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
import java.util.zip.ZipFile;
import javax.naming.Context;
import javax.naming.NamingEnumeration;

/**
 * Contains helper functions for closing different objects, and handling any exceptions thrown at
 * the time.
 *
 * @author ugorji
 */
public class CloseUtils {
  public static final void close(Closeable os) {
    try {
      if (os != null) os.close();
    } catch (Throwable exc) {
      OxygenUtils.error("CloseUtils: Error closing OutputStream", exc);
    }
  }

  public static final void close(ZipFile os) {
    try {
      if (os != null) os.close();
    } catch (Throwable exc) {
      OxygenUtils.error("CloseUtils: Error closing OutputStream", exc);
    }
  }

  public static final void close(java.io.Writer os) {
    try {
      if (os != null) os.close();
    } catch (Throwable exc) {
      OxygenUtils.error("CloseUtils: Error closing OutputStream", exc);
    }
  }

  public static final void close(java.io.OutputStream os) {
    try {
      if (os != null) os.close();
    } catch (Throwable exc) {
      OxygenUtils.error("CloseUtils: Error closing OutputStream", exc);
    }
  }

  public static final void close(java.io.InputStream is) {
    try {
      if (is != null) is.close();
    } catch (Throwable exc) {
      OxygenUtils.error("CloseUtils: Error closing InputStream", exc);
    }
  }

  public static final void close(java.io.Reader r) {
    try {
      if (r != null) r.close();
    } catch (Throwable exc) {
      OxygenUtils.error("CloseUtils: Error closing InputStream", exc);
    }
  }

  public static final void close(Context ctx) {
    try {
      if (ctx != null) ctx.close();
    } catch (Throwable exc) {
      OxygenUtils.error("CloseUtils: Error closing Context", exc);
    }
  }

  public static final void close(NamingEnumeration arg) {
    try {
      if (arg != null) arg.close();
    } catch (Throwable exc) {
      OxygenUtils.error("CloseUtils: Error closing NamingEnumeration", exc);
    }
  }

  public static final void close(java.sql.Connection arg) {
    try {
      if (arg != null) arg.close();
    } catch (Throwable exc) {
      OxygenUtils.error("CloseUtils: Error closing JDBC Connection", exc);
    }
  }

  public static final void close(java.sql.Statement arg) {
    try {
      if (arg != null) arg.close();
    } catch (Throwable exc) {
      OxygenUtils.error("CloseUtils: Error closing JDBC Statement", exc);
    }
  }

  public static final void close(java.sql.ResultSet arg) {
    try {
      if (arg != null) arg.close();
    } catch (Throwable exc) {
      OxygenUtils.error("CloseUtils: Error closing JDBC ResultSet", exc);
    }
  }

  public static final void close(Object obj) {
    try {
      if (obj != null) {
        Method meth = obj.getClass().getMethod("close", new Class[0]);
        if (meth != null) {
          meth.invoke(obj, new Object[0]);
        }
      }
    } catch (InvocationTargetException ite) {
      OxygenUtils.error("CloseUtils: Error closing Object", ite.getTargetException());
    } catch (Throwable exc) {
      OxygenUtils.error("CloseUtils: Error closing Object", exc);
    }
  }

  public static final void closeJDBC(
      java.sql.ResultSet rs, java.sql.Statement stmt, java.sql.Connection conn) {
    close(rs);
    close(stmt);
    close(conn);
  }
}
