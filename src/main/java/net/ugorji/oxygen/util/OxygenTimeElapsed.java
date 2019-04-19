/* <<< COPYRIGHT START >>>
 * Copyright 2006-Present OxygenSoftwareLibrary.com
 * Licensed under the GNU Lesser General Public License.
 * http://www.gnu.org/licenses/lgpl.html
 *
 * @author: Ugorji Nwoke
 * <<< COPYRIGHT END >>>
 */

package net.ugorji.oxygen.util;

public class OxygenTimeElapsed {
  public long days;
  public long hours;
  public long minutes;
  public long seconds;
  public long milliseconds;

  public static final int MILLISECOND_PRECISION = 1;
  public static final int SECOND_PRECISION = 2;
  public static final int MINUTES_PRECISION = 3;
  public static final int HOURS_PRECISION = 4;
  public static final int DAYS_PRECISION = 5;

  public void reset() {
    days = hours = minutes = seconds = milliseconds = 0;
  }

  public void reset(long startTime, long endTime, int precision) throws IllegalArgumentException {
    if (endTime < startTime) {
      throw new IllegalArgumentException(
          "The endTime must be after the startTime: " + startTime + ", " + endTime);
    } else if (precision < MILLISECOND_PRECISION || precision > DAYS_PRECISION) {
      throw new IllegalArgumentException("The precision passed is invalid: " + precision);
    }
    reset();

    milliseconds = endTime - startTime;

    if (precision == MILLISECOND_PRECISION) return;

    seconds = milliseconds / 1000;
    milliseconds = milliseconds % 1000;

    if (precision == SECOND_PRECISION) return;

    minutes = seconds / 60;
    seconds = seconds % 60;

    if (precision == MINUTES_PRECISION) return;

    hours = minutes / 60;
    minutes = minutes % 60;

    if (precision == HOURS_PRECISION) return;

    days = hours / 24;
    hours = hours % 24;

    if (precision == DAYS_PRECISION) return;

    throw new RuntimeException(
        "The precision passed was invalid. We should never get here. Object in inconsistent state");
  }
}
