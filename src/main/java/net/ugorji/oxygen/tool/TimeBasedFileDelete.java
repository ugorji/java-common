/* <<< COPYRIGHT START >>>
 * Copyright 2006-Present OxygenSoftwareLibrary.com
 * Licensed under the GNU Lesser General Public License.
 * http://www.gnu.org/licenses/lgpl.html
 *
 * @author: Ugorji Nwoke
 * <<< COPYRIGHT END >>>
 */

package net.ugorji.oxygen.tool;

import java.io.File;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Recurses a directory, deleting the files which are older than a certain amount of time, or all
 * the empty directories, as it traverses the tree.
 *
 * @author Ugorji Nwoke ugorji@gmail.com
 * @version 1.0, Mar 3, 2001
 */
public class TimeBasedFileDelete {
  private static String HELP_MESSAGE = null;

  public File dir = new File(".");
  public long diff = Long.MAX_VALUE;
  public boolean doDelete = false;
  public boolean debug = false;

  private final long currtime = System.currentTimeMillis();

  private final Date logdate = new Date();

  private final SimpleDateFormat logdateFmt = new SimpleDateFormat("HH:mm:ss:SSS");

  // set the HELP MESSAGE
  static {
    String lsep = System.getProperty("line.separator");
    HELP_MESSAGE =
        "Usage: "
            + lsep
            + "java [-Ddebug=] [-Ddelete=] TimeBasedFileDelete <dir> <timediff> "
            + lsep
            + "  timediff format: 1ms, 1s, 1m, 1h, 1d";
  }

  /** Starts the process of recursing the top directory and looking for files to delete */
  public void run() throws Exception {
    if (!(dir.exists() && dir.isDirectory())) {
      throw new Exception(
          "The directory specified either does not exist "
              + "or is not a directory: "
              + dir.getAbsolutePath());
    }
    log("        dir: " + dir.getAbsolutePath());
    log("  time-diff: " + diff);
    recurse(dir);
  }

  /** recursing a particular directory, and looking for files to delete */
  public void recurse(File file) throws Exception {
    if (file.isFile()) {
      delete(file);
    } else {
      // if empty, try to delete
      File[] files = file.listFiles();
      if (!(files == null || files.length == 0)) {
        for (int i = 0; i < files.length; i++) {
          recurse(files[i]);
        }
      }
      files = file.listFiles();
      if (files == null || files.length == 0) {
        delete(file);
      }
    }
  }

  /** Actually delete the file if older than the diff time */
  private void delete(File file) throws Exception {
    long lastModifiedTime = file.lastModified();
    if ((currtime - lastModifiedTime) > diff) {
      try {
        boolean fileDeleted = false;
        if (doDelete) fileDeleted = file.delete();
        if (fileDeleted) log("    deleted: " + file.getAbsolutePath());
        else log("not deleted: " + file.getAbsolutePath());
      } catch (Exception exc) {
        log("  exception: " + file.getAbsolutePath());
        log(exc, System.err);
      }
    } else {
      if (debug) log("  too young: " + file.getAbsolutePath(), System.err);
    }
  }

  /**
   * Logs a message to the PrintWriter specified If msg is a throwable, it prints the stack trace to
   * the printwriter
   */
  public void log(Object msg, PrintWriter pw) {
    logdate.setTime(System.currentTimeMillis());
    pw.println("[" + logdateFmt.format(logdate) + "] " + msg);
    if (msg instanceof Throwable) {
      Throwable exc = (Throwable) msg;
      pw.print(" --> ");
      exc.printStackTrace(pw);
    }
    pw.flush();
  }

  /**
   * Logs a message to the PrintStream specified If msg is a throwable, it also prints the stack
   * trace to the PrintStream
   */
  public void log(Object msg, PrintStream ps) {
    PrintWriter pw = new PrintWriter(ps);
    log(msg, pw);
  }

  /** Same as calling log (msg, System.out); */
  public void log(Object msg) {
    log(msg, System.out);
  }

  /**
   * Parse command line parameters and create an instance of TimeBasedFileDelete which tries to
   * delete the old files
   */
  public static void main(String[] args) throws Exception {
    if (args.length < 2) {
      System.out.println(HELP_MESSAGE);
      System.exit(0);
    }

    TimeBasedFileDelete tbfd = new TimeBasedFileDelete();

    tbfd.debug = "true".equals(System.getProperty("debug"));
    String tmpstr = System.getProperty("delete");
    if (tmpstr != null) tbfd.doDelete = "true".equals(tmpstr);
    tbfd.dir = new File(args[0]);
    tbfd.diff = 1l;

    String diffstr = args[1];
    int diffstrlen = diffstr.length();
    if (diffstr.endsWith("ms")) {
      tbfd.diff = Long.parseLong(diffstr.substring(0, diffstrlen - 2));
    } else if (diffstr.endsWith("s")) {
      tbfd.diff = Long.parseLong(diffstr.substring(0, diffstrlen - 1));
      tbfd.diff = tbfd.diff * 1000;
    } else if (diffstr.endsWith("m")) {
      tbfd.diff = Long.parseLong(diffstr.substring(0, diffstrlen - 1));
      tbfd.diff = tbfd.diff * 1000 * 60;
    } else if (diffstr.endsWith("h")) {
      tbfd.diff = Long.parseLong(diffstr.substring(0, diffstrlen - 1));
      tbfd.diff = tbfd.diff * 1000 * 60 * 60;
    } else if (diffstr.endsWith("d")) {
      tbfd.diff = Long.parseLong(diffstr.substring(0, diffstrlen - 1));
      tbfd.diff = tbfd.diff * 1000 * 60 * 60 * 24;
    } else {
      tbfd.diff = Long.parseLong(diffstr);
    }

    tbfd.run();
  }
}
