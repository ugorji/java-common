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
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import net.ugorji.oxygen.util.StringUtils;

public class FSGroupManager implements GroupManager {
  private File file;
  private Map mapping = new HashMap();
  private long lastmod = -1;

  public void init(Properties p) throws Exception {
    String s = p.getProperty(PROPS_PREFIX + "group.file");
    s = StringUtils.replacePropertyReferencesInString(s, p);
    file = new File(s);
    sync();
  }

  public boolean isUserInGroup(String group, String user) throws Exception {
    return getMapForGroup(group).contains(user);
  }

  public String[] getUsersInGroup(String group) throws Exception {
    return (String[]) getMapForGroup(group).toArray(new String[0]);
  }

  public void addUserToGroup(String group, String user) throws Exception {
    List l = getMapForGroup(group);
    l.remove(user);
    l.add(user);
  }

  public void removeUserFromGroup(String group, String user) throws Exception {
    List l = getMapForGroup(group);
    l.remove(user);
  }

  public void removeGroup(String group) throws Exception {
    mapping.remove(group);
  }

  public void save(Properties metadata) throws Exception {
    ManagerUtils.saveMapForManagers(mapping, file, ",", ':');
  }

  public void close() {}

  public String[] getGroups() throws Exception {
    return (String[]) mapping.keySet().toArray(new String[0]);
  }

  public String[] getGroupsForUser(String username) throws Exception {
    HashSet roles = new HashSet();
    for (Iterator itr = mapping.keySet().iterator(); itr.hasNext(); ) {
      String group = (String) itr.next();
      Collection users = (Collection) mapping.get(group);
      if (users.contains(username)) {
        roles.add(group);
      }
    }
    return (String[]) roles.toArray(new String[0]);
  }

  private List getMapForGroup(String group) throws Exception {
    List l = (List) mapping.get(group);
    if (l == null) {
      l = new java.util.ArrayList();
      mapping.put(group, l);
    }
    return l;
  }

  public void sync() throws Exception {
    long lastmod2 = file.lastModified();
    if (lastmod2 > lastmod) {
      mapping.clear();
      ManagerUtils.loadMapForManagers(mapping, file, ",");
      lastmod = lastmod2;
    }
  }
}
