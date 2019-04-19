/* <<< COPYRIGHT START >>>
 * Copyright 2006-Present OxygenSoftwareLibrary.com
 * Licensed under the GNU Lesser General Public License.
 * http://www.gnu.org/licenses/lgpl.html
 *
 * @author: Ugorji Nwoke
 * <<< COPYRIGHT END >>>
 */

package net.ugorji.oxygen.util;

import java.io.File;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.ugorji.oxygen.extensions.jdk.OxySimpleLogFormatter;

/**
 * Initializes the JDK logging mechanism, setting the log information to go into
 * $tmpdir/jdkloggingplugin.log
 *
 * @author ugorji
 */
public class JdkLoggingPlugin implements Plugin {
  private FileHandler logHdlr = null;
  private boolean oldUseParentHandlers;

  protected File logdir = new File(System.getProperty("java.io.tmpdir"));
  protected String logfilepattern = "jdkloggingplugin.log";
  protected boolean append = false;
  protected boolean useParentHandlers = false;
  protected String levelString = "INFO";

  public void init() throws Exception {
    doInit();
  }

  protected void doInit() throws Exception {
    String lp =
        StringUtils.trim(logdir.getAbsolutePath().replace('\\', '/'), '/', false, true)
            + "/"
            + logfilepattern;
    System.out.println("Configuring application log messages to go to: " + lp);
    OxygenUtils.info("Configuring application log messages to go to: " + lp);
    logdir.mkdirs();
    logHdlr = new FileHandler(lp, append);
    logHdlr.setFormatter(new OxySimpleLogFormatter());
    logHdlr.setLevel(Level.parse(levelString));
    // OxygenUtils.info("hello");
    // OxygenUtils.info(new Exception());
    Logger lg = Logger.getLogger(OxygenConstants.LOGGER_BASE_PREFIX);
    // do this, so that this logger only sends log messages to my handler
    oldUseParentHandlers = lg.getUseParentHandlers();
    lg.setUseParentHandlers(useParentHandlers);
    lg.addHandler(logHdlr);
    lg.log(Level.INFO, "Welcome");
  }

  public void start() {}

  public void close() {
    if (logHdlr != null) {
      Logger lg = Logger.getLogger(OxygenConstants.LOGGER_BASE_PREFIX);
      lg.removeHandler(logHdlr);
      lg.setUseParentHandlers(oldUseParentHandlers);
      logHdlr.close();
      logHdlr = null;
    }
  }
}
