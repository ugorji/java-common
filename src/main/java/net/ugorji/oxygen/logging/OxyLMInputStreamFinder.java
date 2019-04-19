/* <<< COPYRIGHT START >>>
 * Copyright 2006-Present OxygenSoftwareLibrary.com
 * Licensed under the GNU Lesser General Public License.
 * http://www.gnu.org/licenses/lgpl.html
 *
 * @author: Ugorji Nwoke
 * <<< COPYRIGHT END >>>
 */

package net.ugorji.oxygen.logging;

import java.io.IOException;
import java.io.InputStream;

public interface OxyLMInputStreamFinder {
  public void init() throws IOException;

  public boolean hasNextInputStream();

  public InputStream nextInputStream() throws IOException;

  public void postInit() throws IOException;
}
