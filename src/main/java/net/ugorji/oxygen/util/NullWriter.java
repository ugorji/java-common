/* <<< COPYRIGHT START >>>
 * Copyright 2006-Present OxygenSoftwareLibrary.com
 * Licensed under the GNU Lesser General Public License.
 * http://www.gnu.org/licenses/lgpl.html
 *
 * @author: Ugorji Nwoke
 * <<< COPYRIGHT END >>>
 */

package net.ugorji.oxygen.util;

import java.io.Writer;

/**
 * Provides no-op operations for a Writer object
 *
 * @author Ugorji Nwoke ugorji@gmail.com
 * @version 1.0, Nov 3, 2001
 */
public class NullWriter extends Writer {
  public static final NullWriter SINGLETON = new NullWriter();

  public void close() {}

  public void flush() {}

  public void write(char[] cbuf) {}

  public void write(char[] cbuf, int off, int len) {}

  public void write(int c) {}

  public void write(String str) {}

  public void write(String str, int off, int len) {}
}
