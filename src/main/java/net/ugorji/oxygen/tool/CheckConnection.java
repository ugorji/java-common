/* <<< COPYRIGHT START >>>
 * Copyright 2006-Present OxygenSoftwareLibrary.com
 * Licensed under the GNU Lesser General Public License.
 * http://www.gnu.org/licenses/lgpl.html
 *
 * @author: Ugorji Nwoke
 * <<< COPYRIGHT END >>>
 */

package net.ugorji.oxygen.tool;

import java.net.Socket;

/**
 * Tries to make a connection to a server, at a specific host and port Throw an exception if a
 * connection could not be made in a specified time
 *
 * @author Ugorji Nwoke ugorji@gmail.com
 * @version 1.0, May 16, 2001
 */
public class CheckConnection {

  /**
   * Tries to make a connection to a server, at a specific host and port Throw an exception if a
   * connection could not be made in a specified time
   */
  public static boolean waitForSocketAccept(String host, int port, long timeoutms)
      throws Exception {
    log(
        "Attempting connection to "
            + host
            + " at port "
            + port
            + " with timeout of "
            + timeoutms
            + "ms");
    long sleeptime = 500;

    long start = System.currentTimeMillis();
    int retry = 0;
    long elapsed = 0;
    Socket socket = null;
    try {
      while (true) {
        try {
          socket = new Socket(host, port);
          elapsed = System.currentTimeMillis() - start;
          log("successfully got connection after " + (elapsed / 1000) + " seconds");
          socket.close();
          return true;
        } catch (Exception e) {
          // log ("could not get connection - may retry");
          elapsed = System.currentTimeMillis() - start;
          if (elapsed > timeoutms) {
            log(
                "Timeout Period of "
                    + (timeoutms / 1000)
                    + " seconds elapsed - could not get connection");
            throw new Exception(
                "TimedOut after " + (timeoutms / 1000) + " seconds - Could not connect to Server");
          }
          // this is bad it means the server isn't there
          retry++;
          try {
            Thread.sleep(sleeptime);
          } // try every 500 ms
          catch (InterruptedException ie) {
          }
        }
      }
    } finally {
      try {
        socket.close();
      } catch (Exception se) {
      }
    }
  }

  /**
   * Tries to make a connection to a server, at a specific host and port, in a given time. Usage:
   * java CheckConnection <host> <port> <timeout seconds> e.g. Usage: java CheckConnection localhost
   * 80 10 Return with an exit code of -1, if a connection could not be made.
   */
  public static void main(String[] args) {
    int listenPort = 80;
    String listenAddress = "localhost";
    int timeoutsec = 60;
    if (args.length > 0) {
      listenAddress = args[0];
    }
    if (args.length > 1) {
      try {
        listenPort = Integer.parseInt(args[1]);
      } catch (Exception exc) {
      }
    }
    if (args.length > 2) {
      try {
        timeoutsec = Integer.parseInt(args[2]);
      } catch (Exception exc) {
      }
    }

    long ltimeoutms = (long) (1000l * timeoutsec);
    try {
      boolean connected = waitForSocketAccept(listenAddress, listenPort, ltimeoutms);
      if (connected) System.exit(0);
      else System.exit(-1);
    } catch (Exception exc) {
    }
    System.exit(-1);
  }

  public static void log(String s) {
    System.out.println(s);
  }
}
