/* <<< COPYRIGHT START >>>
 * Copyright 2006-Present OxygenSoftwareLibrary.com
 * Licensed under the GNU Lesser General Public License.
 * http://www.gnu.org/licenses/lgpl.html
 *
 * @author: Ugorji Nwoke
 * <<< COPYRIGHT END >>>
 */

package net.ugorji.oxygen.util;

import java.util.Properties;

/**
 * This is an abstraction, which allows the concept to be used within different things.
 *
 * @author ugorjid
 */
public interface OxygenEngine extends Closeable {
  public String getProperty(String s, String defValue);

  public Properties getProperties();

  public Object getAttribute(Object s);

  public void setAttribute(Object s, Object o);

  public void clearAttributes();
}
