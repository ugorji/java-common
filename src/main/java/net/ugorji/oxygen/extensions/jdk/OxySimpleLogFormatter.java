/* <<< COPYRIGHT START >>>
 * Copyright 2006-Present OxygenSoftwareLibrary.com
 * Licensed under the GNU Lesser General Public License.
 * http://www.gnu.org/licenses/lgpl.html
 *
 * @author: Ugorji Nwoke
 * <<< COPYRIGHT END >>>
 */

package net.ugorji.oxygen.extensions.jdk;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class OxySimpleLogFormatter extends Formatter {

  private static DateFormat fmt =
      DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM);
  private static String LINE_SEP = System.getProperty("line.separator");
  // static {
  //  System.out.println("--- " + System.getProperty("java.class.path"));
  // }

  public String format(LogRecord record) {
    StringBuffer sb = new StringBuffer();
    Date dat = new Date(record.getMillis());
    sb.append("[");
    sb.append(fmt.format(dat));
    sb.append("] ");
    sb.append(record.getLevel().getLocalizedName());
    sb.append(": ");
    sb.append(formatMessage(record));
    sb.append(LINE_SEP);
    if (record.getThrown() != null) {
      sb.append(toString(record.getThrown()));
    }
    return sb.toString();
  }

  private static String toString(Throwable thr) {
    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);
    thr.printStackTrace(pw);
    pw.flush();
    sw.flush();
    return sw.toString();
  }
}
