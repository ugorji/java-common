/* <<< COPYRIGHT START >>>
 * Copyright 2006-Present OxygenSoftwareLibrary.com
 * Licensed under the GNU Lesser General Public License.
 * http://www.gnu.org/licenses/lgpl.html
 *
 * @author: Ugorji Nwoke
 * <<< COPYRIGHT END >>>
 */

package net.ugorji.oxygen.manager;

import java.util.Properties;
import net.ugorji.oxygen.util.Closeable;

public interface OxyManager extends Closeable {
  String PROPS_PREFIX = "net.ugorji.oxygen.manager.";

  void init(Properties p) throws Exception;

  void sync() throws Exception;

  void save(Properties saveMetadata) throws Exception;
}
