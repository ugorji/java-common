/* <<< COPYRIGHT START >>>
 * Copyright 2006-Present OxygenSoftwareLibrary.com
 * Licensed under the GNU Lesser General Public License.
 * http://www.gnu.org/licenses/lgpl.html
 *
 * @author: Ugorji Nwoke
 * <<< COPYRIGHT END >>>
 */

package net.ugorji.oxygen.util;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Allows us bundle a bunch of exception together.
 *
 * @author Ugorji
 */
public class ExceptionList extends Exception {
  private List throwables = new ArrayList();

  public ExceptionList() {
    super();
  }

  public ExceptionList(String message) {
    super(message);
  }

  public int numThrowables() {
    return throwables.size();
  }

  public void addThrowable(Throwable thr) {
    throwables.add(thr);
  }

  public void printStackTrace(PrintStream ps) {
    for (Iterator itr = throwables.iterator(); itr.hasNext(); ) {
      Throwable thr = (Throwable) itr.next();
      thr.printStackTrace(ps);
    }
  }

  public void printStackTrace(PrintWriter ps) {
    for (Iterator itr = throwables.iterator(); itr.hasNext(); ) {
      Throwable thr = (Throwable) itr.next();
      thr.printStackTrace(ps);
    }
  }
}
