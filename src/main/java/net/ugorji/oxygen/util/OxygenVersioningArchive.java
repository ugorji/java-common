/* <<< COPYRIGHT START >>>
 * Copyright 2006-Present OxygenSoftwareLibrary.com
 * Licensed under the GNU Lesser General Public License.
 * http://www.gnu.org/licenses/lgpl.html
 *
 * @author: Ugorji Nwoke
 * <<< COPYRIGHT END >>>
 */

package net.ugorji.oxygen.util;

import java.io.InputStream;
import java.io.Serializable;
import java.util.Date;
import java.util.Map;

public interface OxygenVersioningArchive extends Serializable {
  String METADATA_DIRECTORY = ".OXYVER";

  boolean exists() throws Exception;

  Map getDescriptions() throws Exception;

  Map getDescriptions(OxygenIntRange range) throws Exception;

  void deleteVersions(OxygenIntRange range) throws Exception;

  String getDescription(int rev) throws Exception;

  InputStream getInputStream(int rev) throws Exception;

  int getHeadVersion() throws Exception;

  void addNewVersion(InputStream is, String logmsg) throws Exception;

  Date getDate(int rev) throws Exception;
}
