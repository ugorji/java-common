/* <<< COPYRIGHT START >>>
 * Copyright 2006-Present OxygenSoftwareLibrary.com
 * Licensed under the GNU Lesser General Public License.
 * http://www.gnu.org/licenses/lgpl.html
 *
 * @author: Ugorji Nwoke
 * <<< COPYRIGHT END >>>
 */

package net.ugorji.oxygen.util;

/**
 * Guaranteed shutdown: - tries System.exit() and then Runtime.halt() if the former did not do the
 * job
 *
 * @author ugorji
 */
public class ShutdownHandler {

  private ShutdownHandler() {}

  public static void shutdown() {
    shutdown(0);
  }

  public static void shutdown(int exitnum) {
    shutdown(exitnum, 1000, 2500);
  }

  public static void shutdown(final int exitnum, final int sysexitwaitms, final int rthaltwaitms) {
    // System.out.println("Inside ShutdownHandler.shutdown()");
    // put into a thread, with a small sleep time,
    // so client calling shutdown does not get Unmarshal exception
    Thread exitThread =
        new Thread(OxygenUtils.topLevelThreadGroup(), "shutdown-handler-1") {
          public void run() {
            OxygenUtils.sleep(sysexitwaitms);
            // System.out.println("Calling System.exit(" + exitnum + ")");
            System.exit(exitnum);
          }
        };
    exitThread.setDaemon(true);
    exitThread.start();

    Thread exitThread2 =
        new Thread(OxygenUtils.topLevelThreadGroup(), "shutdown-handler-2") {
          public void run() {
            OxygenUtils.sleep(rthaltwaitms);
            // System.out.println("Seems System.exit did not do it ... calling
            // Runtime.getRuntime().halt(" + exitnum + ")");
            Runtime.getRuntime().halt(exitnum);
          }
        };
    exitThread2.setDaemon(true);
    exitThread2.start();
  }

  /** main method for testing */
  public static void main(String[] args) throws Exception {
    Thread t =
        new Thread(OxygenUtils.topLevelThreadGroup(), "shutdown-handler-3") {
          public void run() {
            System.out.println("Daemon thread sleeping for 15 seconds");
            for (; ; ) {}
          }
        };
    t.setDaemon(true);
    Runtime.getRuntime().addShutdownHook(t);
    Thread t2 =
        new Thread(OxygenUtils.topLevelThreadGroup(), "shutdown-handler-4") {
          public void run() {
            System.out.println("Non-daemon thread sleeping for 15 seconds");
            for (; ; ) {}
          }
        };
    t2.setDaemon(false);
    Runtime.getRuntime().addShutdownHook(t2);
    ShutdownHandler.shutdown();
  }
}
