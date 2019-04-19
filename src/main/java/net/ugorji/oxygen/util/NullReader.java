/* <<< COPYRIGHT START >>>
 * Copyright 2006-Present OxygenSoftwareLibrary.com
 * Licensed under the GNU Lesser General Public License.
 * http://www.gnu.org/licenses/lgpl.html
 *
 * @author: Ugorji Nwoke
 * <<< COPYRIGHT END >>>
 */

package net.ugorji.oxygen.util;

import java.io.IOException;
import java.io.Reader;

/**
 * Provides no-op operations for a Writer object
 *
 * @author Ugorji Nwoke ugorji@gmail.com
 */
public class NullReader extends Reader {
  public static final NullReader SINGLETON = new NullReader();

  public void close() throws IOException {}

  public int read(char[] cbuf, int off, int len) throws IOException {
    return -1;
  }
}
