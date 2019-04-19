/* <<< COPYRIGHT START >>>
 * Copyright 2006-Present OxygenSoftwareLibrary.com
 * Licensed under the GNU Lesser General Public License.
 * http://www.gnu.org/licenses/lgpl.html
 *
 * @author: Ugorji Nwoke
 * <<< COPYRIGHT END >>>
 */

package net.ugorji.oxygen.manager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import net.ugorji.oxygen.util.CloseUtils;
import net.ugorji.oxygen.util.StringUtils;

public class ManagerUtils {

  static void saveMapForManagers(Map mapping, File file, String colsep) throws Exception {
    saveMapForManagers(mapping, file, colsep, '=');
  }

  static void saveMapForManagers(Map mapping, File file, String colsep, char filesep)
      throws Exception {
    Properties p = getPropertiesMapForManagers(mapping, colsep);
    FileOutputStream fos = new FileOutputStream(file);

    if (filesep == '=') {
      p.store(fos, null);
      CloseUtils.close(fos);
    } else {
      PrintWriter pw = new PrintWriter(new OutputStreamWriter(fos));
      for (Iterator itr = p.entrySet().iterator(); itr.hasNext(); ) {
        Map.Entry entry = (Map.Entry) itr.next();
        pw.println(entry.getKey().toString() + filesep + " " + entry.getValue());
      }
      pw.flush();
      CloseUtils.close(pw);
    }
  }

  static Properties getPropertiesMapForManagers(Map mapping, String sep) throws Exception {
    Properties p = new Properties();
    for (Iterator itr = mapping.keySet().iterator(); itr.hasNext(); ) {
      String key = (String) itr.next();
      String val = null;
      if (sep == null) {
        val = (String) mapping.get(key);
      } else {
        val = StringUtils.toString((Collection) mapping.get(key), sep);
      }
      p.setProperty(key, val);
    }
    return p;
  }

  /**
   * If sep == null, then put as string. else put as a list. return either Map<String, String> or
   * Map<String, List>
   */
  static void loadMapForManagers(Map mapping, File file, String sep) throws Exception {
    Properties p = new Properties();
    if (file.exists()) {
      InputStream is = new FileInputStream(file);
      p.load(is);
      CloseUtils.close(is);
    }
    for (Enumeration enum0 = p.propertyNames(); enum0.hasMoreElements(); ) {
      String key = (String) enum0.nextElement();
      if (sep == null) {
        mapping.put(key, (String) p.getProperty(key));
      } else {
        List l = StringUtils.tokenize((String) p.getProperty(key), sep);
        mapping.put(key, l);
      }
    }
  }
}
