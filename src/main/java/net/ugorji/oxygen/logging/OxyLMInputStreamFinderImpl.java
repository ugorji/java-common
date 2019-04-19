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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class OxyLMInputStreamFinderImpl implements OxyLMInputStreamFinder {
  private List files = null;
  private Iterator itr = null;

  public void init() throws IOException {
    files = new ArrayList();
    files.add("oxy-logging-configuration.xml");
    itr = files.iterator();
  }

  public boolean hasNextInputStream() {
    return itr.hasNext();
  }

  public InputStream nextInputStream() throws IOException {
    String fname = (String) itr.next();
    ClassLoader cl = Thread.currentThread().getContextClassLoader();
    InputStream is = cl.getResourceAsStream(fname);
    if (is == null) {
      throw new IOException("No resource available for: " + fname);
    }
    return is;
  }

  public void postInit() throws IOException {}
}
