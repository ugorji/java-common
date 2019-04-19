/* <<< COPYRIGHT START >>>
 * Copyright 2006-Present OxygenSoftwareLibrary.com
 * Licensed under the GNU Lesser General Public License.
 * http://www.gnu.org/licenses/lgpl.html
 *
 * @author: Ugorji Nwoke
 * <<< COPYRIGHT END >>>
 */

package net.ugorji.oxygen.util;

public class JEECloseUtils {
  public static final void close(javax.jms.Connection conn) {
    try {
      if (conn != null) conn.close();
    } catch (Throwable exc) {
      // SysLog.error(Utils.class, "Error closing JMS Connection", exc);
    }
  }

  public static final void close(javax.jms.Session arg) {
    try {
      if (arg != null) arg.close();
    } catch (Throwable exc) {
      // SysLog.error(Utils.class, "Error closing JMS Session", exc);
    }
  }

  public static final void close(javax.jms.QueueRequestor arg) {
    try {
      if (arg != null) arg.close();
    } catch (Throwable exc) {
      // SysLog.error(Utils.class, "Error closing JMS QueueRequestor", exc);
    }
  }

  public static final void closeJMS(javax.jms.Session session, javax.jms.Connection conn) {
    close(session);
    close(conn);
  }
}
