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
import java.util.Properties;
import java.util.logging.LogManager;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class OxyLogManager extends LogManager {
  private boolean readConfigurationWasCalled = false;
  private boolean failonerror = true;
  private boolean allowReset = true;
  private Properties oxyprops = null;

  public OxyLogManager(boolean allowReset, boolean failonerror) {
    super();
    this.allowReset = allowReset;
    this.failonerror = failonerror;
    oxyprops = new Properties();
  }

  public OxyLogManager() {
    this(true, false);
  }

  /** subclasses can override this */
  protected OxyLMInputStreamFinder getInputStreamFinder() {
    return new OxyLMInputStreamFinderImpl();
  }

  public void readConfiguration() throws IOException, SecurityException {
    // System.out.println("readConfiguration()");
    if (!allowReset && readConfigurationWasCalled) {
      return;
    }
    checkAccess();
    reset();
    // read the configuration
    OxyLMInputStreamFinder isfinder = getInputStreamFinder();
    isfinder.init();
    while (isfinder.hasNextInputStream()) {
      // System.out.println("Loading first IS");
      InputStream is = isfinder.nextInputStream();
      OxyLMXMLParser parser = new OxyLMXMLParser();
      try {
        SAXParserFactory fact = SAXParserFactory.newInstance();
        SAXParser sp = fact.newSAXParser();
        sp.parse(is, parser);
      } catch (IOException ioe) {
        if (failonerror) {
          throw ioe;
        } else {
          System.err.println("Handled IOException: " + ioe);
        }
      } catch (Exception exc) {
        exc.printStackTrace();
        if (failonerror) {
          throw new IOException("Error parsing file: " + exc);
        } else {
          System.err.println("Error parsing file: Handled Exception: " + exc);
        }
      }
    }
    isfinder.postInit();
    readConfigurationWasCalled = true;
  }

  /** do nothing - throw exception since we do not support a properties file format */
  public void readConfiguration(InputStream is) throws IOException, SecurityException {
    throw new IOException("This implementation does not support readConfiguration(InputStream)");
  }

  public void reset() {
    if (allowReset) {
      super.reset();
      oxyprops.clear();
    }
  }

  /** re-implement this, because it is called across the whole logging package */
  public String getProperty(String name) {
    return oxyprops.getProperty(name);
  }

  protected void setProperty(String name, String value) {
    oxyprops.setProperty(name, value);
  }

  protected Properties getProperties() {
    return oxyprops;
  }
}
