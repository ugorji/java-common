/* <<< COPYRIGHT START >>>
 * Copyright 2006-Present OxygenSoftwareLibrary.com
 * Licensed under the GNU Lesser General Public License.
 * http://www.gnu.org/licenses/lgpl.html
 *
 * @author: Ugorji Nwoke
 * <<< COPYRIGHT END >>>
 */

package net.ugorji.oxygen.util;

import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import org.apache.bsf.BSFManager;

/**
 * A Plugin, which can be used to execute some arbitrary scripting code at start and close time. It
 * respectively executes the contents of net.ugorji.oxygen.plugin_bsf.$xxx_init_start.bsf and
 * net.ugorji.oxygen.plugin_bsf.$xxx_init_close.bsf ($xxx is either "pre" or "post"). This class is typically
 * sub-classed.
 *
 * @author ugorjid
 */
public class BSFPlugin implements Plugin {
  protected boolean pre = true;
  protected String lang = "beanshell";

  public void init() throws Exception {}

  public void start() throws Exception {
    exec("net.ugorji.oxygen.plugin_bsf." + (pre ? "pre" : "post") + "_init_start.bsf");
  }

  public void close() throws Exception {
    exec("net.ugorji.oxygen.plugin_bsf." + (pre ? "pre" : "post") + "_init_close.bsf");
  }

  private void exec(String rname) throws Exception {
    URL url = Thread.currentThread().getContextClassLoader().getResource(rname);
    if (url != null) {
      BSFManager manager = new BSFManager();
      Map m = getBeansToDeclare();
      if (m != null) {
        for (Iterator itr = m.entrySet().iterator(); itr.hasNext(); ) {
          Map.Entry me = (Map.Entry) itr.next();
          Object k = me.getKey();
          Object v = me.getValue();
          if (v != null) {
            manager.declareBean(k.toString(), v, v.getClass());
          }
        }
      }
      String content = OxygenUtils.getURLContents(url);
      manager.exec(lang, rname, 0, 0, content);
    }
  }

  protected Map getBeansToDeclare() {
    return null;
  }
}
