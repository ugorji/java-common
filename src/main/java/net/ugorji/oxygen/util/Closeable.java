/* <<< COPYRIGHT START >>>
 * Copyright 2006-Present OxygenSoftwareLibrary.com
 * Licensed under the GNU Lesser General Public License.
 * http://www.gnu.org/licenses/lgpl.html
 *
 * @author: Ugorji Nwoke
 * <<< COPYRIGHT END >>>
 */

package net.ugorji.oxygen.util;

public interface Closeable {
  /**
   * Sometimes, things close, and have problems while closing We should let the person calling
   * close, take care of that exception CloseUtils.close(Closeable) is a convenience method which
   * calls close, and logs an error if it happens.
   *
   * @throws Exception
   */
  public void close() throws Exception;
}
