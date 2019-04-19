/* <<< COPYRIGHT START >>>
 * Copyright 2006-Present OxygenSoftwareLibrary.com
 * Licensed under the GNU Lesser General Public License.
 * http://www.gnu.org/licenses/lgpl.html
 *
 * @author: Ugorji Nwoke
 * <<< COPYRIGHT END >>>
 */

// package weblogic.qa.taskmgr;

package net.ugorji.oxygen.tool;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Copies information from one Database to another.
 *
 * @author Ugorji Nwoke ugorji@gmail.com
 * @version 1.0, August 3, 2001
 */
public class DBCopy {
  // >>>>>>>>>>>>>>>> static stuff <<<<<<<<<<<<<<<<<<<<<
  private static final Date logdate = new Date();
  private static final SimpleDateFormat logdateFmt = new SimpleDateFormat("HH:mm:ss:SSS");

  private Connection fromConn;
  private Connection toConn;

  private static String HELP_MESSAGE = null;

  static {
    String lsep = System.getProperty("line.separator");
    HELP_MESSAGE =
        "Usage: "
            + lsep
            + "java DBCopy \\"
            + lsep
            + "  [-dbDriver <aDriverClassName>]... \\"
            + lsep
            + "   -fromConnURL <aDBUrl> \\"
            + lsep
            + "  [-fromConnProp <key> <value>]... \\"
            + lsep
            + "   -toConnURL <aDBUrl> \\"
            + lsep
            + "  [-toConnProp <key> <value>]... \\"
            + lsep
            + "   -dbTable <old_table_name> <new_table_name> \\"
            + lsep
            + "  [-colmapping <old_col> <new_col>]... "
            + lsep
            + "";
  }

  public DBCopy(Connection fromConn1, Connection toConn1) throws Exception {
    this.fromConn = fromConn1;
    this.toConn = toConn1;
  }

  public void copyTables(List dbTables) throws Exception {
    for (Iterator itr = dbTables.iterator(); itr.hasNext(); ) {
      DBTableInfo dbti = (DBTableInfo) itr.next();
      copyTable(dbti);
    }
  }

  public void copyTable(DBTableInfo dbti) throws Exception {
    String tmp = null;
    log("copyTable:" + dbti.fromTbl + " to " + dbti.toTbl + " - started");
    String fromQuery = "Select * from " + dbti.fromTbl;
    Statement stmt = fromConn.createStatement();
    ResultSet rs = stmt.executeQuery(fromQuery);
    log("resultset obtained");
    ResultSetMetaData rsmd = rs.getMetaData();
    int numberOfColumns = rsmd.getColumnCount();
    List cols = new ArrayList(numberOfColumns);
    List colTypes = new ArrayList(numberOfColumns);
    for (int i = 1; i <= numberOfColumns; i++) {
      cols.add(rsmd.getColumnName(i).toUpperCase());
      colTypes.add(new Integer(rsmd.getColumnType(i)));
    }

    /*
       List cols_To = new ArrayList ();
       List colTypes_To = new ArrayList ();
       DatabaseMetaData dbmd = toConn.getMetaData ();
       ResultSet rs2 = dbmd.getColumns (null, null, dbti.toTbl, "%");
       while (rs2.next () ) {
         String colName1 = rs2.getString ("COLUMN_NAME");
         if (cols_To.contains ( colName1 ) ) {
    log ("Contains: " + colName1);
    continue;
         }
         cols_To.add (colName1);
         colTypes_To.add (new Integer ( rs2.getInt ("DATA_TYPE") ) );
       }
       rs2.close ();
       */
    Statement stmt2 = toConn.createStatement();
    ResultSet rs2 = stmt2.executeQuery("select * from " + dbti.toTbl + " where 1 = 0");
    ResultSetMetaData rsmd2 = rs2.getMetaData();
    int numberOfColumns2 = rsmd2.getColumnCount();
    List cols2 = new ArrayList(numberOfColumns2);
    List colTypes2 = new ArrayList(numberOfColumns2);
    for (int i = 1; i <= numberOfColumns2; i++) {
      cols2.add(rsmd2.getColumnName(i).toUpperCase());
      colTypes2.add(new Integer(rsmd2.getColumnType(i)));
    }
    rs2.close();
    stmt2.close();

    // log (cols2.toString ());
    // log ("numberOfColumns2: " + numberOfColumns2);
    // log ("cols2.size(): " + cols2.size());

    String toQuery = getPrepStmtString_To(dbti.toTbl, cols2);
    log("toQuery: " + toQuery);
    PreparedStatement tostmt = toConn.prepareStatement(toQuery);
    int numrowsupdated = 0;
    while (rs.next()) {
      for (int i = 1; i <= numberOfColumns2; i++) {
        String colName_To = ((String) cols2.get(i - 1)).toUpperCase();
        int colType_To = ((Integer) colTypes2.get(i - 1)).intValue();
        String colName_From = (String) dbti.colmapping.get(colName_To);
        if (colName_From == null) colName_From = colName_To;
        // log ("colmapping of: to: " + colName_To + " from: " + colName_From);
        if (cols.contains(colName_From)) {
          Object obj_To = rs.getObject(colName_From);
          if (obj_To == null) {
            tostmt.setNull(i, colType_To);
          } else {
            tostmt.setObject(i, obj_To);
          }
        } else {
          tostmt.setNull(i, colType_To);
        }
        // System.out.print ("-");
      }
      tostmt.executeUpdate();
      numrowsupdated++;
      if ((numrowsupdated % 50) == 0) System.out.print("+");
    }
    log("closing statememts / resultset");
    tostmt.close();
    rs.close();
    stmt.close();
    log("copyTable:" + dbti.fromTbl + " to " + dbti.toTbl + " - ended");
  }

  private String getPrepStmtString_To(String tableName, List cols) {
    StringBuffer buf = new StringBuffer();
    buf.append("insert into ").append(tableName).append(" (");
    Iterator itr = cols.iterator();
    if (itr.hasNext()) {
      buf.append(itr.next());
      while (itr.hasNext()) {
        buf.append(", ").append(itr.next());
      }
    }
    buf.append(" )").append(" values (");
    itr = cols.iterator();
    if (itr.hasNext()) {
      buf.append("?");
      itr.next();
      while (itr.hasNext()) {
        buf.append(", ").append("?");
        itr.next();
      }
    }
    buf.append(" )");
    String toQuery = buf.toString();
    return toQuery;
  }

  private String getStmtString_To(String tableName, List cols, List rowinfo) {
    StringBuffer buf = new StringBuffer();
    buf.append("insert into ").append(tableName).append(" (");
    Iterator itr = cols.iterator();
    if (itr.hasNext()) {
      buf.append(itr.next());
      while (itr.hasNext()) {
        buf.append(", ").append(itr.next());
      }
    }
    buf.append(" )").append(" values (");
    itr = rowinfo.iterator();
    if (itr.hasNext()) {
      buf.append("'").append(dbEscape((String) itr.next())).append("'");
      while (itr.hasNext()) {
        buf.append(", ").append("'").append(dbEscape((String) itr.next())).append("'");
      }
    }
    buf.append(" )");
    String toQuery = buf.toString();
    return toQuery;
  }

  public static void log(Object msg) {
    logdate.setTime(System.currentTimeMillis());
    System.out.println("[" + logdateFmt.format(logdate) + "] " + msg);
    if (msg instanceof Throwable) {
      Throwable exc = (Throwable) msg;
      System.out.print(" --> ");
      exc.printStackTrace(System.out);
    }
  }

  /** This takes a String and replaces all occurrences of old with neww */
  public static String replaceInString(String in, String old, String neww) {
    int b = 0;
    if (in == null
        || old == null
        || neww == null
        || (b = in.indexOf(old)) == -1
        || old.length() == 0) {
      return in;
    }
    int oldLength = old.length();

    StringBuffer mod = new StringBuffer(in.length());
    mod.append(in.substring(0, b));

    int e = in.indexOf(old, b + oldLength);
    while (e != -1) {
      mod.append(neww);
      mod.append(in.substring(b + oldLength, e));
      b = e;
      e = in.indexOf(old, b + oldLength);
    }
    mod.append(neww);
    if (b < in.length()) {
      mod.append(in.substring(b + oldLength));
    }
    return mod.toString();
  }

  /** This takes a String and replaces all the single quotes with a two single quotes ... */
  public static String dbEscape(String in) {
    if (in == null) return "";
    return (replaceInString(in, "'", "''"));
  }

  private static void addProp(String propstr, Map m) {
    String key = "", value = "";
    int indexOfEquals = propstr.indexOf("=");
    if (indexOfEquals == -1) {
      key = propstr;
    } else {
      key = propstr.substring(0, indexOfEquals);
      value = propstr.substring(indexOfEquals + 1);
    }
    m.put(key, value);
  }

  public static void main(String[] args) throws Exception {
    if (args.length == 0) {
      System.out.println(HELP_MESSAGE);
      return;
    }

    Connection fromConn = null;
    Connection toConn = null;
    try {
      String fromConnURL = null;
      String toConnURL = null;
      List dbDrivers = new ArrayList();
      List dbTables = new ArrayList();
      Properties fromConnProp = new Properties();
      Properties toConnProp = new Properties();
      for (int i = 0; i < args.length; i++) {
        if (args[i].equals("-dbDriver")) {
          dbDrivers.add(args[++i]);
        } else if (args[i].equals("-fromConnURL")) {
          fromConnURL = args[++i];
        } else if (args[i].equals("-fromConnProp")) {
          fromConnProp.put(args[++i], args[++i]);
        } else if (args[i].equals("-toConnURL")) {
          toConnURL = args[++i];
        } else if (args[i].equals("-toConnProp")) {
          toConnProp.put(args[++i], args[++i]);
        } else if (args[i].equals("-dbTable")) {
          DBTableInfo dbti = new DBTableInfo();
          dbti.fromTbl = args[++i];
          dbti.toTbl = args[++i];
          while (args[i + 1].equals("-colmapping")) {
            i++;
            dbti.colmapping.put(args[++i].toUpperCase(), args[++i].toUpperCase());
          }
          log("dbti: " + dbti);
          dbTables.add(dbti);
        }
      }
      log("dbDrivers = " + dbDrivers);
      log("fromConnURL = " + fromConnURL);
      log("fromConnProp = " + fromConnProp);
      log("toConnURL = " + toConnURL);
      log("toConnProp = " + toConnProp);
      log("dbTables = " + dbTables);
      for (Iterator itr = dbDrivers.iterator(); itr.hasNext(); ) {
        String dbDriver = (String) itr.next();
        Class.forName(dbDriver);
      }
      fromConn = DriverManager.getConnection(fromConnURL, fromConnProp);
      toConn = DriverManager.getConnection(toConnURL, toConnProp);

      DBCopy dbc = new DBCopy(fromConn, toConn);
      dbc.copyTables(dbTables);
    } finally {
      log("closing fromConn ...");
      try {
        if (fromConn != null) fromConn.close();
      } catch (Exception exc) {
      }
      log("closing toConn ... ");
      try {
        if (toConn != null) toConn.close();
      } catch (Exception exc) {
      }
      log("ALL DONE");
    }
  }

  public static class DBTableInfo {
    public String fromTbl = null;
    public String toTbl = null;
    public Map colmapping = new HashMap();

    public String toString() {
      return fromTbl + " --> " + toTbl + " --> " + colmapping;
    }
  }
}
