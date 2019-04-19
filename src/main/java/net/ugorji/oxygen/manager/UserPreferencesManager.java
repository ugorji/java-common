/* <<< COPYRIGHT START >>>
 * Copyright 2006-Present OxygenSoftwareLibrary.com
 * Licensed under the GNU Lesser General Public License.
 * http://www.gnu.org/licenses/lgpl.html
 *
 * @author: Ugorji Nwoke
 * <<< COPYRIGHT END >>>
 */

package net.ugorji.oxygen.manager;

import java.util.Map;

public interface UserPreferencesManager extends OxyManager {
  String[] getUsers() throws Exception;

  Map getAll() throws Exception;

  Map getForKey(String key) throws Exception;

  Map getForUser(String username) throws Exception;

  String[] getForUser(String username, String key) throws Exception;

  void setForUser(String username, String key, String[] values) throws Exception;
}
