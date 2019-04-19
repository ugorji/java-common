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
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import net.ugorji.oxygen.util.StringUtils;

/**
 * Stores in a file like this: ${username}.${key} = abcd|####|thanks|####|hello Username cannot have
 * spaces. Key can have spaces.
 */
public class FSUserPreferencesManager implements UserPreferencesManager {
  protected File file;
  protected Map mapping = new HashMap();
  protected long lastmod = -1;

  public void init(Properties p) throws Exception {
    String s = p.getProperty(PROPS_PREFIX + "userpreferences.file");
    s = StringUtils.replacePropertyReferencesInString(s, p);
    // System.out.println("FSUserPreferencesManager: userpreferences.file: " + s);
    // System.out.println("FSUserPreferencesManager: p: " + p);
    file = new File(s);
    sync();
  }

  public String[] getUsers() throws Exception {
    return (String[]) (mapping.keySet().toArray(new String[0]));
  }

  public Map getAll() throws Exception {
    return Collections.unmodifiableMap(mapping);
  }

  public Map getForKey(String key) throws Exception {
    Map m2 = new HashMap();
    for (Iterator itr = mapping.keySet().iterator(); itr.hasNext(); ) {
      String s = (String) itr.next();
      int idx = s.indexOf(".");
      if (idx != -1) {
        String s0 = s.substring(0, idx);
        String s1 = s.substring(idx + 1);
        if (key.equals(s1)) {
          m2.put(s0, mapping.get(s));
        }
      }
    }
    return m2;
  }

  public Map getForUser(String username) throws Exception {
    username = cleanUsername(username);
    Map m2 = new HashMap();
    for (Iterator itr = mapping.keySet().iterator(); itr.hasNext(); ) {
      String s = (String) itr.next();
      int idx = s.indexOf(".");
      if (idx != -1) {
        String s0 = s.substring(0, idx);
        String s1 = s.substring(idx + 1);
        if (username.equals(s0)) {
          m2.put(s1, mapping.get(s));
        }
      }
    }
    return m2;
  }

  public String[] getForUser(String username, String key) throws Exception {
    username = cleanUsername(username);
    Collection col = (Collection) mapping.get(username + "." + key);
    if (col == null) {
      return new String[0];
    }
    return (String[]) col.toArray(new String[0]);
  }

  public void setForUser(String username, String key, String[] values) throws Exception {
    username = cleanUsername(username);
    mapping.put(username + "." + key, Arrays.asList(values));
  }

  public void save(Properties metadata) throws Exception {
    ManagerUtils.saveMapForManagers(mapping, file, "|####|");
  }

  public void close() {}

  public void sync() throws Exception {
    long lastmod2 = file.lastModified();
    if (lastmod2 > lastmod) {
      mapping.clear();
      ManagerUtils.loadMapForManagers(mapping, file, "|####|");
      lastmod = lastmod2;
    }
  }

  private static String cleanUsername(String username) {
    return username.replace('.', '_');
  }
}
