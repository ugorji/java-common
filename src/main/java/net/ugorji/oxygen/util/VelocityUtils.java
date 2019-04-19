/* <<< COPYRIGHT START >>>
 * Copyright 2006-Present OxygenSoftwareLibrary.com
 * Licensed under the GNU Lesser General Public License.
 * http://www.gnu.org/licenses/lgpl.html
 *
 * @author: Ugorji Nwoke
 * <<< COPYRIGHT END >>>
 */

package net.ugorji.oxygen.util;

/*
import java.util.Properties;

import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.log.LogSystem;
*/
public class VelocityUtils {
  /*
    private static VelocityEngine ve;

    static {
      try {
        ve = new VelocityEngine();
        //ve.setProperty(VelocityEngine.RUNTIME_LOG_LOGSYSTEM, this);
        Properties p = new Properties();
        p.setProperty("resource.loader", "class");
        p.setProperty("directive.foreach.counter.initial.value", "0");
        p.setProperty("class.resource.loader.class",
                      "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        p.setProperty(RuntimeConstants.RUNTIME_LOG_LOGSYSTEM_CLASS,
                      VLogSystem.class.getName());
        ve.init(p);
      } catch(Throwable exc) {
        System.out.println("Error loading VelocityEngine: " + exc);
      }
    }

    public static VelocityEngine getVelocityEngine() {
      return ve;
    }

    public static class VLogSystem implements LogSystem {
      // no-op LogSystem functions
      public void init(RuntimeServices rs) {}
      public void logVelocityMessage(int level, String message) {}
    }
  */
}
