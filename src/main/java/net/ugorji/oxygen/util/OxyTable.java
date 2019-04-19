/* <<< COPYRIGHT START >>>
 * Copyright 2006-Present OxygenSoftwareLibrary.com
 * Licensed under the GNU Lesser General Public License.
 * http://www.gnu.org/licenses/lgpl.html
 *
 * @author: Ugorji Nwoke
 * <<< COPYRIGHT END >>>
 */

package net.ugorji.oxygen.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class OxyTable implements Cloneable {
  private static List emptyList = new ArrayList(0); // Arrays.asList(new Object[]{});

  private int mainindex = 0;
  private String description;
  private int colCount;

  // private Object internalobj;

  private List<String> colNames;
  private ArrayList<List> data = new ArrayList(16);

  public OxyTable(String[] headers) {
    colNames = Arrays.asList(headers);
    colCount = headers.length;
    // super(headers, 0);
  }

  public String getColumnName(int index) {
    if (index >= colNames.size()) return "";
    return colNames.get(index);
  }

  public int getColumnCount() {
    return colCount;
  }

  public int getRowCount() {
    return data.size();
  }

  public Object getValueAt(int rowIndex, int colIndex) {
    return data.get(rowIndex).get(colIndex);
  }

  // public void setInternalObject(Object o) {
  //   internalobj = o;
  // }

  // public Object getInternalObject() {
  //   return internalobj;
  // }

  public void setMainIndex(int i) {
    mainindex = i;
  }

  public int getMainIndex() {
    return mainindex;
  }

  public void setDescription(String desc) {
    description = desc;
  }

  public String getDescription() {
    return description;
  }

  public void addRow(Object[] rowData) {
    addRow(Arrays.asList(rowData));
  }

  public void addRow(List rowData) {
    if (colCount < rowData.size()) {
      colCount = rowData.size();
    }
    data.add(rowData);
  }

  public void insertRow(int row, List rowData) {
    if (colCount < rowData.size()) {
      colCount = rowData.size();
    }
    while (row > data.size()) {
      data.add(emptyList);
    }
    data.add(row, rowData);
  }

  public OxyTable copy() throws Exception {
    return (OxyTable) super.clone();
  }

  public void sort(Comparator comp) {
    data.sort(comp);
  }

  public void sort(int colindex) {
    sort(new WLScTblComp(colindex));
  }

  public void sort() {
    sort(mainindex);
  }

  public String toString() {
    StringWriter stw = new StringWriter();
    PrintWriter pw = new PrintWriter(stw);
    printTo(pw, true);
    pw.println("OxyTable: numrows=" + getRowCount() + " numcols=" + getColumnCount());
    pw.flush();
    return stw.toString();
    // return "OxyTable: numrows=" + numrows + " numcols=" + numcols;
  }

  private int getMaxColWidth(int colindex) {
    int maxwidth = 0;
    int rowcount = getRowCount();
    for (int i = 0; i < rowcount; i++) {
      Object rowelem = getValueAt(i, colindex);
      if (rowelem != null) {
        maxwidth = Math.max(maxwidth, rowelem.toString().length());
      }
    }
    return maxwidth;
  }

  public void printTo(PrintWriter pw, boolean dosort) {
    if (description != null) {
      pw.println(description);
    }

    int numrows = getRowCount();
    int numcols = getColumnCount();
    int[] colwidths = new int[numcols];
    int maxlinelen = 10;
    for (int i = 0; i < numcols; i++) {
      int maxlen = getMaxColWidth(i);
      maxlen = Math.max(maxlen, getColumnName(i).length());
      colwidths[i] = maxlen + 4;
      maxlinelen += colwidths[i];
    }

    StringBuffer buf = null;
    // print headers
    buf = new StringBuffer(maxlinelen).append("  ");
    for (int i = 0; i < numcols; i++) {
      String colname = getColumnName(i);
      if (i == (numcols - 1)) {
        buf.append(colname);
      } else {
        buf.append(StringUtils.padToLength(colname, colwidths[i]));
      }
    }
    int maxheaderlinelen = buf.length() + 4;
    pw.println(buf);
    pw.flush();
    // print line under headers
    buf = new StringBuffer(maxlinelen);
    for (int i = 0; i < maxheaderlinelen; i++) {
      buf.append("-");
    }
    pw.println(buf);
    pw.flush();
    // print each row
    for (int i0 = 0; i0 < numrows; i0++) {
      buf = new StringBuffer(maxlinelen).append("  ");
      for (int i = 0; i < numcols; i++) {
        Object s = getValueAt(i0, i);
        if (i == (numcols - 1)) {
          buf.append(s);
        } else {
          buf.append(StringUtils.padToLength(s, colwidths[i]));
        }
      }
      pw.println(buf);
      pw.flush();
    }
    if (numrows > 0) {
      // print line under headers again
      buf = new StringBuffer(maxlinelen);
      for (int i = 0; i < maxheaderlinelen; i++) {
        buf.append("-");
      }
      pw.println(buf);
      pw.flush();
    }
  }

  public static OxyTable parseFrom(Object obj) throws Exception {
    Collection col = null;
    if (obj == null) {
      col = new ArrayList();
    } else if (obj instanceof Map) {
      col = ((Map) obj).entrySet();
    } else if (obj instanceof Collection) {
      col = (Collection) obj;
    } else if (obj instanceof Object[]) {
      col = Arrays.asList((Object[]) obj);
    } else {
      throw new Exception(
          "To convert to OxyTable, parameter must be one of: null, Map, Collection, Object[]");
    }

    String[] headers = new String[] {};
    OxyTable tabl = new OxyTable(headers);
    // tabl.setInternalObject(obj);
    for (Iterator itr = col.iterator(); itr.hasNext(); ) {
      Object[] row = new Object[headers.length];
      Object obj0 = itr.next();
      // System.out.println(obj0.getClass().getName());
      if (obj0 instanceof Map.Entry) {
        row = new Object[2];
        row[0] = ((Map.Entry) obj0).getKey();
        row[1] = ((Map.Entry) obj0).getValue();
      } else if (obj0 instanceof Object[]) {
        row = ((Object[]) obj0);
      } else if (obj0 instanceof Collection) {
        row = ((Collection) obj0).toArray();
      } else {
        row = new Object[1];
        row[0] = obj0;
      }
      tabl.addRow(row);
    }
    return tabl;
  }

  public static class WLScTblComp implements Comparator {
    private int mainindex = 0;

    public WLScTblComp(int mainindex0) {
      mainindex = mainindex0;
    }

    public int compare(Object o1, Object o2) {
      if (o1 == null && o2 == null) {
        return 0;
      } else if (o1 == null && o2 != null) {
        return -1;
      } else if (o1 != null && o2 == null) {
        return 1;
      } else if (o1 instanceof List && o2 instanceof List) {
        List oa1 = (List) o1;
        List oa2 = (List) o2;
        Object oa11 = oa1.get(mainindex);
        Object oa22 = oa2.get(mainindex);
        return compare(oa11, oa22);
      } else if (o1 instanceof Comparable && o2 instanceof Comparable) {
        return ((Comparable) o1).compareTo(((Comparable) o2));
      } else {
        return String.valueOf(o1).compareTo(String.valueOf(o2));
      }
    }
  }
}

/*

  public void printTo(PrintWriter pw) {
    int[] colwidths = new int[numcols];
    int maxlinelen = 10;
    for(int i = 0; i < numcols; i++) {
      int maxlen =  StringUtils.getMaxKeyLength(cols[i]);
      maxlen = Math.max(maxlen, headers[i].length());
      colwidths[i] = maxlen + 4;
      maxlinelen += colwidths[i];
    }

    StringBuffer buf = null;
    // print headers
    buf = new StringBuffer(maxlinelen).append("  ");
    for(int i = 0; i < numcols; i++) {
      if(i == (numcols - 1)) {
        buf.append(headers[i]);
      } else {
        buf.append(StringUtils.padToLength(headers[i], colwidths[i]));
      }
    }
    int maxheaderlinelen = buf.length() + 4;
    pw.println(buf);
    pw.flush();
    // print line under headers
    buf = new StringBuffer(maxlinelen);
    for(int i = 0; i < maxheaderlinelen; i++) {
      buf.append("-");
    }
    pw.println(buf);
    pw.flush();
    // print each row
    for(int i0 = 0; i0 < numrows; i0++) {
      buf = new StringBuffer(maxlinelen).append("  ");
      for(int i = 0; i < numcols; i++) {
        Object s = (Object)cols[i].get(i0);
        if(i == (numcols - 1)) {
          buf.append(s);
        } else {
          buf.append(StringUtils.padToLength(s, colwidths[i]));
        }
      }
      pw.println(buf);
      pw.flush();
    }
  }

*/
