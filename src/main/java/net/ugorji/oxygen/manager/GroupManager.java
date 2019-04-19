/* <<< COPYRIGHT START >>>
 * Copyright 2006-Present OxygenSoftwareLibrary.com
 * Licensed under the GNU Lesser General Public License.
 * http://www.gnu.org/licenses/lgpl.html
 *
 * @author: Ugorji Nwoke
 * <<< COPYRIGHT END >>>
 */

package net.ugorji.oxygen.manager;

public interface GroupManager extends OxyManager {
  boolean isUserInGroup(String group, String user) throws Exception;

  void addUserToGroup(String group, String user) throws Exception;

  void removeUserFromGroup(String group, String user) throws Exception;

  String[] getUsersInGroup(String group) throws Exception;

  String[] getGroupsForUser(String user) throws Exception;

  String[] getGroups() throws Exception;

  void removeGroup(String group) throws Exception;
}
