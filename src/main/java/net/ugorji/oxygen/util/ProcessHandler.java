/* <<< COPYRIGHT START >>>
 * Copyright 2006-Present OxygenSoftwareLibrary.com
 * Licensed under the GNU Lesser General Public License.
 * http://www.gnu.org/licenses/lgpl.html
 *
 * @author: Ugorji Nwoke
 * <<< COPYRIGHT END >>>
 */

package net.ugorji.oxygen.util;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Handles Processes, giving us back the output and error strings
 *
 * <p>Uses the params to determine what to do. Basically, it connects the input stream and output
 * stream to the either files or strings, waits for the process to end if desired, and returns.
 *
 * <p>Note: We *MUST* not close the streams that we drain to. Whoever passed us those streams know
 * what to do with them.
 *
 * @author Ugorji Nwoke
 * @version 1.0, Nov 3, 2001
 */
public class ProcessHandler {
  private Process p;
  private List drainers = new ArrayList();

  public ProcessHandler(Process p0) {
    p = p0;
  }

  public ProcessHandler drainStdOut(OutputStream os) {
    return register(new DrainerToOutputStream(p.getInputStream(), os));
  }

  public ProcessHandler drainStdOut(Writer w) {
    return register(new DrainerToWriter(p.getInputStream(), w));
  }

  public ProcessHandler drainStdErr(Writer w) {
    return register(new DrainerToWriter(p.getErrorStream(), w));
  }

  public ProcessHandler drainStdErr(OutputStream os) {
    return register(new DrainerToOutputStream(p.getErrorStream(), os));
  }

  public ProcessHandler passStdIn(Reader r) {
    return register(new DrainerFromReader(p.getOutputStream(), r));
  }

  public ProcessHandler waitTillDone() {
    // try {System.out.println("Trying to wait for"); p.waitFor(); System.out.println("done wait
    // for");} catch(InterruptedException iexc) { System.err.println(iexc); }
    while (true) {
      try {
        // System.out.println("Trying to wait for");
        p.waitFor();
        // System.out.println("done wait for");
        break;
      } catch (InterruptedException iexc) {
        System.err.println(iexc);
      }
    }
    for (Iterator itr = drainers.iterator(); itr.hasNext(); ) {
      Drainer d = (Drainer) itr.next();
      while (true) {
        try {
          // System.out.println("Trying to join run thread: " + d.getRunThread());
          d.getRunThread().join();
          break;
        } catch (InterruptedException iexc) {
          System.err.println(iexc);
        }
      }
    }
    return this;
  }

  public ProcessHandler check(int code, String err) {
    int exitcode = p.exitValue();
    if (exitcode != code) {
      throw new RuntimeException(
          "[Exit Code] Received: "
              + exitcode
              + " Expected: "
              + code
              + " [Received Standard Error] "
              + err);
    }
    return checkStdErr(err);
  }

  private ProcessHandler checkExitCode(int code) {
    return check(code, "");
  }

  private ProcessHandler checkStdErr(String s) {
    if (!StringUtils.isBlank(s)) {
      throw new RuntimeException("[Received Standard Error] " + s);
    }
    return this;
  }

  public InputStream manageInputStream(int code, StringWriter stw) {
    return this.new ProcessCleanupInputStream(code, stw);
  }

  private ProcessHandler register(Drainer d) {
    drainers.add(d);
    d.getRunThread().start();
    return this;
  }

  public static void handle(
      Process p, OutputStream stdout, Writer stderr, Reader stdin, boolean waitForProcessEnd)
      throws Exception {
    ProcessHandler ph =
        new ProcessHandler(p).drainStdOut(stdout).drainStdErr(stderr).passStdIn(stdin);
    if (waitForProcessEnd) ph.waitTillDone();
  }

  public static void handle(
      Process p, Writer stdout, Writer stderr, Reader stdin, boolean waitForProcessEnd)
      throws Exception {
    ProcessHandler ph =
        new ProcessHandler(p).drainStdOut(stdout).drainStdErr(stderr).passStdIn(stdin);
    if (waitForProcessEnd) ph.waitTillDone();
  }

  public static void handle(Process p, Writer stdouterr, Reader stdin, boolean waitForProcessEnd)
      throws Exception {
    ProcessHandler ph =
        new ProcessHandler(p).drainStdOut(stdouterr).drainStdErr(stdouterr).passStdIn(stdin);
    if (waitForProcessEnd) ph.waitTillDone();
  }

  public static void handle(
      Process p, OutputStream stdouterr, Reader stdin, boolean waitForProcessEnd) throws Exception {
    ProcessHandler ph =
        new ProcessHandler(p).drainStdOut(stdouterr).drainStdErr(stdouterr).passStdIn(stdin);
    if (waitForProcessEnd) ph.waitTillDone();
  }

  private static class Drainer implements Runnable {
    private Exception throwable;
    private Thread thr;

    protected void doRun() throws Exception {}

    protected void cleanup() {}

    public void run() {
      try {
        doRun();
      } catch (Exception exc) {
        throwable = exc;
        OxygenUtils.error(exc);
        // exc.printStackTrace();
      } finally {
        cleanup();
      }
    }

    public Thread getRunThread() {
      if (thr == null) {
        thr = new Thread(OxygenUtils.topLevelThreadGroup(), this);
        thr.setDaemon(true);
      }
      return thr;
    }
  }

  private static class DrainerToWriter extends Drainer {
    private Writer writer;
    private InputStream is;

    private DrainerToWriter(InputStream is0, Writer w0) {
      is = is0;
      writer = w0;
    }

    protected void doRun() throws Exception {
      int num = -1;
      InputStreamReader fr = new InputStreamReader(is);
      char[] fc = new char[1024];
      while ((num = fr.read(fc)) != -1) {
        if (writer != null) {
          synchronized (writer) {
            writer.write(fc, 0, num);
            writer.flush();
          }
        }
      }
    }

    protected void cleanup() {
      // CloseUtils.close(writer);
      CloseUtils.close(is);
    }
  }

  private static class DrainerToOutputStream extends Drainer {
    private OutputStream os;
    private InputStream is;

    private DrainerToOutputStream(InputStream is0, OutputStream os0) {
      is = is0;
      os = os0;
    }

    protected void doRun() throws Exception {
      int num = -1;
      byte[] fc = new byte[1024];
      while ((num = is.read(fc)) != -1) {
        if (os != null) {
          synchronized (os) {
            os.write(fc, 0, num);
            os.flush();
          }
        }
      }
    }

    protected void cleanup() {
      // CloseUtils.close(os);
      CloseUtils.close(is);
    }
  }

  private static class DrainerFromReader extends Drainer {
    private Reader reader;
    private OutputStream os;

    private DrainerFromReader(OutputStream os0, Reader r0) {
      reader = r0;
      os = os0;
    }

    protected void doRun() throws Exception {
      if (reader != null) {
        int num = -1;
        char[] fc = new char[1024];
        OutputStreamWriter osw = new OutputStreamWriter(os);
        while ((num = reader.read(fc)) != -1) {
          osw.write(fc, 0, num);
          osw.flush();
        }
      }
    }

    protected void cleanup() {
      CloseUtils.close(os);
      // CloseUtils.close(reader);
    }
  }

  private class ProcessCleanupInputStream extends FilterInputStream {
    private StringWriter err;
    private int exitcode = 0;

    public ProcessCleanupInputStream(int code, StringWriter err0) {
      super(p.getInputStream());
      exitcode = code;
      err = err0;
      drainStdErr(err);
    }

    public void close() throws IOException {
      super.close();
      waitTillDone();
      checkExitCode(exitcode);
      if (err != null) checkStdErr(err.toString());
    }
  }
}
