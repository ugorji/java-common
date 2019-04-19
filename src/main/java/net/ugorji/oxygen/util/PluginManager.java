/* <<< COPYRIGHT START >>>
 * Copyright 2006-Present OxygenSoftwareLibrary.com
 * Licensed under the GNU Lesser General Public License.
 * http://www.gnu.org/licenses/lgpl.html
 *
 * @author: Ugorji Nwoke
 * <<< COPYRIGHT END >>>
 */

package net.ugorji.oxygen.util;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

public class PluginManager implements Closeable {
  private Map actions = new LinkedHashMap();

  public PluginManager(Properties p, String prefix) throws Exception {
    Properties pp = new Properties();
    OxygenUtils.extractProps(p, pp, prefix, true);
    for (Enumeration enum0 = pp.propertyNames(); enum0.hasMoreElements(); ) {
      String k = (String) enum0.nextElement();
      String v = pp.getProperty(k);
      Plugin w = null;
      if (OxygenConstants.NULL.equals(v)) {
        OxygenProxy opy = new OxygenProxy(null, new Class[] {Plugin.class});
        w = (Plugin) opy.getProxy();
      } else {
        w = (Plugin) OxygenUtils.getClass(v).newInstance();
      }
      w.init();
      actions.put(k, w);
    }
  }

  public void start() throws Exception {
    for (Iterator itr = actions.values().iterator(); itr.hasNext(); ) {
      Plugin p = (Plugin) itr.next();
      p.start();
    }
  }

  public void close() {
    for (Iterator itr = actions.values().iterator(); itr.hasNext(); ) {
      Plugin p = (Plugin) itr.next();
      CloseUtils.close(p);
    }
  }

  // public Plugin[] getAllPlugins() {
  //   return (Plugin[])actions.values().toArray(new Plugin[0]);
  // }

  // public Plugin getPlugin(String s) {
  //   return (Plugin)actions.get(s);
  // }

}
