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
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;

/*
 * Not Yet Completed ... some issues to be worked out here
 * Make it abstract and private for now (so no-one uses it)
 */
abstract class OxygenResourceBundle extends ResourceBundle {
  private Map map = new HashMap();

  private OxygenResourceBundle(String basename, Locale locale) throws Exception {
    String bundleName = basename;
    String localeAsStr = locale.toString();
    if (!StringUtils.isBlank(localeAsStr)) {
      bundleName = bundleName + "_" + localeAsStr;
    }
    String resName = bundleName.replace('.', '/') + ".properties";
    for (Enumeration enum0 = Thread.currentThread().getContextClassLoader().getResources(resName);
        enum0.hasMoreElements(); ) {
      URL url = (URL) enum0.nextElement();
      InputStream is = url.openStream();
      try {
        Properties p = new Properties();
        p.load(is);
        map.putAll(p);
      } finally {
        CloseUtils.close(is);
      }
    }
  }

  public Object handleGetObject(String key) {
    return map.get(key);
  }

  public Enumeration getKeys() {
    return Collections.enumeration(map.keySet());
  }
}
