/* <<< COPYRIGHT START >>>
 * Copyright 2006-Present OxygenSoftwareLibrary.com
 * Licensed under the GNU Lesser General Public License.
 * http://www.gnu.org/licenses/lgpl.html
 *
 * @author: Ugorji Nwoke
 * <<< COPYRIGHT END >>>
 */

package net.ugorji.oxygen.util;

public class SetURLConnectionTimeoutPlugin implements Plugin {
  public void init() throws Exception {}

  public void start() throws Exception {
    System.setProperty("sun.net.client.defaultConnectTimeout", String.valueOf(getReadTimeout()));
    System.setProperty("sun.net.client.defaultReadTimeout", String.valueOf(getConnectTimeout()));
  }

  public void close() {}

  protected long getReadTimeout() {
    return 10000l;
  }

  protected long getConnectTimeout() {
    return 10000l;
  }
}
