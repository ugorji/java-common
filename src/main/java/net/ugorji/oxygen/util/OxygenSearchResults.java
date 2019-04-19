/* <<< COPYRIGHT START >>>
 * Copyright 2006-Present OxygenSoftwareLibrary.com
 * Licensed under the GNU Lesser General Public License.
 * http://www.gnu.org/licenses/lgpl.html
 *
 * @author: Ugorji Nwoke
 * <<< COPYRIGHT END >>>
 */

package net.ugorji.oxygen.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Encapsulates the results of a search.
 *
 * @author ugorji
 */
public class OxygenSearchResults {

  private List results = new ArrayList();
  private String summary = "";

  public OxygenSearchResults() {}

  public void setSummary(String s) {
    summary = s;
  }

  /**
   * Add a result. The count is a Lucene representation of how highly the hit is regarded
   *
   * @param pagerep
   * @param count
   */
  public void addResult(String category, String pagerep, double count) {
    Entry entry = new Entry(category, pagerep, count);
    results.add(entry);
  }

  public String getSummary() {
    return summary;
  }

  public int getNumResults() {
    return results.size();
  }

  /**
   * Returns all the results (sorted) that were matched for this search
   *
   * @return
   */
  public Entry[] getResults() {
    Entry[] entries = (Entry[]) results.toArray(new Entry[0]);
    Arrays.sort(entries);
    return entries;
  }

  public static class Entry implements Comparable {
    private String category;
    private String page;
    private double score;

    public Entry(String category2, String pagerep2, double score2) {
      category = category2;
      page = pagerep2;
      score = score2;
    }

    public String getCategory() {
      return category;
    }

    public String getPage() {
      return page;
    }

    public double getScore() {
      return score;
    }

    public int compareTo(Object o2) {
      Entry e2 = (Entry) o2;
      double rtn = score - e2.score;
      if (rtn < 0.0) {
        return 1;
      } else if (rtn > 0.0) {
        return -1;
      } else {
        int ii = category.compareTo(e2.category);
        if (ii != 0) {
          return ii;
        } else {
          return page.compareTo(e2.page);
        }
      }
    }
  }
}
