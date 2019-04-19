/* <<< COPYRIGHT START >>>
 * Copyright 2006-Present OxygenSoftwareLibrary.com
 * Licensed under the GNU Lesser General Public License.
 * http://www.gnu.org/licenses/lgpl.html
 *
 * @author: Ugorji Nwoke
 * <<< COPYRIGHT END >>>
 */

package net.ugorji.oxygen.manager;

public interface UserPasswordManager extends OxyManager {
  void setPassword(String username, char[] passwd) throws Exception;

  char[] getEncryptedPassword(String username) throws Exception;

  boolean checkPassword(String username, char[] passwd) throws Exception;
}
