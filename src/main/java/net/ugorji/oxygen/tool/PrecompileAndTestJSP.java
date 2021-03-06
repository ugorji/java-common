/* <<< COPYRIGHT START >>>
 * Copyright 2006-Present OxygenSoftwareLibrary.com
 * Licensed under the GNU Lesser General Public License.
 * http://www.gnu.org/licenses/lgpl.html
 *
 * @author: Ugorji Nwoke
 * <<< COPYRIGHT END >>>
 */

package net.ugorji.oxygen.tool;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Precompiles all the jsp and tell us which are broken <xmp>To see Usage Information: java
 * PrecompileAndTestJSP</xmp> fName is the list of files with the paths of JSP files to precompile
 * host and port are the server host and port respectively
 *
 * <p>Currently, this is only geared towards FORM-based authenticated sites
 *
 * @author Ugorji Nwoke ugorji@gmail.com
 * @version 1.0, August 3, 2001
 */
public class PrecompileAndTestJSP {
  private static String HELP_MESSAGE = null;

  public boolean DEBUG = false;
  public List jspFiles;
  public String host;
  public int port;
  public String username;
  public String password;
  public String cookieString;
  public String formAuthTarget = "/j_security_check";
  public String testpage = "/index.html";
  public boolean isFormAuth = false;
  public boolean isBasicAuth = false;

  // set the HELP MESSAGE
  static {
    String lsep = System.getProperty("line.separator");
    HELP_MESSAGE =
        "Usage: "
            + lsep
            + "java [-D...=] PrecompileAndTestJSP <fName> "
            + lsep
            + "Defaults:"
            + lsep
            + "  -Dhost=localhost"
            + lsep
            + "  -Dport=80"
            + lsep
            + "  -Dusername="
            + lsep
            + "  -Dpassword="
            + lsep
            + "  -Dformauthtarget=/j_security_check"
            + lsep
            + "  -testpage=index.html"
            + lsep
            + "  -Disformauth=true"
            + lsep
            + "  -Disbasicauth=true"
            + lsep
            + "  File should be of the form below:"
            + lsep
            + "#---------------------------------------------"
            + lsep
            + "/console/index.jsp"
            + lsep
            + "/console/standards/home.jsp"
            + lsep
            + "#---------------------------------------------"
            + lsep
            + "";
  }

  public String toString() {
    String s = host + ":" + port + " [user/pass=" + username + "/" + password + "]";
    return s;
  }

  public void run() throws Exception {
    Socket s = new Socket(host, port);
    if (isFormAuth) sendLoginRequestFormBasedAuth(s);
    else if (isBasicAuth) sendLoginRequestBasicAuth(s);
    else sendRequestTestPage(s);

    cookieString = getCookieString(s);
    s.close();

    if (DEBUG) System.err.println(cookieString);
    List badFiles = new ArrayList();
    List goodFiles = new ArrayList();
    int numGood = 0;
    int numBad = 0;

    int sz = jspFiles.size();
    for (int i = 0; i < sz; i++) {
      String file = null;
      try {
        file = (String) jspFiles.get(i);
        int respCode = precompilepage(file);
        if (respCode == 200) {
          System.out.println("Good: " + file + " --- Got RespCode " + respCode);
          goodFiles.add(file);
          numGood++;
        } else {
          System.out.println("Error: " + file + " --- Got RespCode " + respCode);
          badFiles.add(file);
          numBad++;
        }
      } catch (Exception exc) {
        System.out.println("Error: " + file + " --- Got Exception " + exc);
        badFiles.add(file);
        numBad++;
      }
      if (DEBUG) {
        System.err.println("=============================================");
      }
    }

    // output stats
    System.out.println("Good files: " + numGood);
    System.out.println("Bad files: " + numBad);
    System.out.println("---------------------------------------");
    for (int i = 0; i < numBad; i++) {
      System.out.println(badFiles.get(i));
    }
  }

  public int precompilepage(String file) throws Exception {
    int respCode = -1;
    Socket s = new Socket(host, port);
    PrintWriter out = new PrintWriter(new OutputStreamWriter(s.getOutputStream()));
    String target = file + "?jsp_precompile=true";
    out.print("GET " + target + " HTTP/1.0" + "\r\n");
    if (cookieString != null) out.print("Cookie: " + cookieString + "\r\n");
    out.print("Connection: close\r\n");
    out.print("Host: " + host + "\r\n");
    out.print("\r\n");
    out.flush();
    BufferedReader buffreader = new BufferedReader(new InputStreamReader(s.getInputStream()));
    String line = buffreader.readLine();
    if (line != null) {
      StringTokenizer st = new StringTokenizer(line.trim());
      st.nextToken();
      respCode = Integer.parseInt(st.nextToken());
    }
    while ((line = buffreader.readLine()) != null) {
      if (line.trim().length() == 0) break;
    }
    StringBuffer buf = new StringBuffer();
    while ((line = buffreader.readLine()) != null) {
      buf.append(line).append("\n");
    }
    if (DEBUG) {
      System.err.println(target);
      System.err.println(respCode);
      System.err.println(buf.toString());
    }
    buffreader.close();
    out.close();
    s.close();
    return respCode;
  }

  private void sendLoginRequestFormBasedAuth(Socket s) throws Exception {
    String c = null;
    PrintWriter out = new PrintWriter(new OutputStreamWriter(s.getOutputStream()));
    String target = formAuthTarget;
    // out = new PrintWriter (System.out, true);
    out.print("POST " + target + " HTTP/1.0" + "\r\n");
    out.print("Connection: close\r\n");
    out.print("Host: " + host + "\r\n");
    String code = Base64.getEncoder().encodeToString((username + ":" + password).getBytes());
    out.print("Authorization: Basic " + code + "\r\n");
    out.print("Content-type: application/x-www-form-urlencoded\r\n");
    out.print("\r\n");
    out.flush();
    out.close();
  }

  private void sendLoginRequestBasicAuth(Socket s) throws Exception {
    String c = null;
    PrintWriter out = new PrintWriter(new OutputStreamWriter(s.getOutputStream()));
    String target = testpage;
    // out = new PrintWriter (System.out, true);
    out.print("GET " + target + " HTTP/1.0" + "\r\n");
    out.print("Connection: close\r\n");
    out.print("Host: " + host + "\r\n");
    out.print("Content-type: application/x-www-form-urlencoded\r\n");
    String postString = "j_username=" + username + "&j_password=" + password;
    out.print("Content-length: " + postString.length() + "\r\n");
    out.print("\r\n");
    out.print(postString + "\r\n");
    out.print("\r\n");
    out.flush();
    out.close();
  }

  private void sendRequestTestPage(Socket s) throws Exception {
    String c = null;
    PrintWriter out = new PrintWriter(new OutputStreamWriter(s.getOutputStream()));
    String target = testpage;
    // out = new PrintWriter (System.out, true);
    out.print("GET " + target + " HTTP/1.0" + "\r\n");
    out.print("Connection: close\r\n");
    out.print("Host: " + host + "\r\n");
    out.print("Content-type: application/x-www-form-urlencoded\r\n");
    out.print("\r\n");
    out.flush();
    out.close();
  }

  private String getCookieString(Socket s) throws Exception {
    String cookieLine = null;
    String c = null;
    BufferedReader buffreader = new BufferedReader(new InputStreamReader(s.getInputStream()));
    String line = null;
    while ((line = buffreader.readLine()) != null) {
      if (DEBUG) System.err.println(line);
      if (line.trim().length() == 0) break;
      if (line.toLowerCase().startsWith("set-cookie:")) {
        cookieLine = line.substring("Set-Cookie:".length()).trim();
        int semicolon = cookieLine.indexOf(";");
        if (semicolon != -1) {
          cookieLine = cookieLine.substring(0, semicolon).trim();
        }
        break;
      }
    }
    buffreader.close();
    return cookieLine;
  }

  /** <xmp>Usage: java PrecompileAndTestJSP <fName> <host> <port> </xmp> */
  public static void main(String[] args) throws Exception {
    if (args.length == 0) {
      System.out.println(HELP_MESSAGE);
      System.exit(0);
    }

    String fName = "jsp-pages.txt";
    String tHost = "localhost";
    int tPort = 80;
    String tUser = null;
    String tPass = null;
    String tFormAuthTarget = "/j_security_check";
    String tTestpage = "/index.html";

    boolean tIsFormAuth = false;
    boolean tIsBasicAuth = false;

    boolean tDebug = false;
    String tmpstr = null;
    if ((tmpstr = System.getProperty("host")) != null) tHost = tmpstr;
    if ((tmpstr = System.getProperty("port")) != null) tPort = Integer.parseInt(tmpstr);
    if ((tmpstr = System.getProperty("username")) != null) tUser = tmpstr;
    if ((tmpstr = System.getProperty("password")) != null) tPass = tmpstr;
    if ("true".equals(System.getProperty("debug"))) tDebug = true;
    if ((tmpstr = System.getProperty("formauthtarget")) != null) tFormAuthTarget = tmpstr;
    if ((tmpstr = System.getProperty("testpage")) != null) tTestpage = tmpstr;
    if ((tmpstr = System.getProperty("isformauth")) != null) tIsFormAuth = "true".equals(tmpstr);
    if ((tmpstr = System.getProperty("isbasicauth")) != null) tIsBasicAuth = "true".equals(tmpstr);

    if (args.length > 0) fName = args[0];

    FileReader fr = new FileReader(fName);
    BufferedReader br = new BufferedReader(fr);
    List files = new ArrayList();
    String line = null;
    while ((line = br.readLine()) != null) {
      line = line.trim();
      if (line.length() == 0 || line.startsWith("#")) continue;
      files.add(line);
    }
    fr.close();
    br.close();

    PrecompileAndTestJSP test = new PrecompileAndTestJSP();
    test.host = tHost;
    test.port = tPort;
    test.jspFiles = files;
    test.username = tUser;
    test.password = tPass;
    test.formAuthTarget = tFormAuthTarget;
    test.testpage = tTestpage;
    test.isFormAuth = tIsFormAuth;
    test.isBasicAuth = tIsBasicAuth;
    test.DEBUG = tDebug;

    System.out.println("file: " + fName);
    System.out.println("PrecompileAndTestJSP: " + test);
    test.run();
  }
}

/*
  public String getCookieString00 ()
    throws Exception
  {
    if (username == null || password == null)
      return null;
    String cookieLine = null;
    String c = null;
    Socket s = new Socket (host, port);
    PrintWriter out = new PrintWriter( new OutputStreamWriter( s.getOutputStream() ) );
    String target = formAuthTarget;
    // out = new PrintWriter (System.out, true);
    out.print ("POST " + target + " HTTP/1.0" + "\r\n");
    out.print ("Connection: close\r\n");
    out.print ("Host: " + host + "\r\n");
    out.print ("Content-type: application/x-www-form-urlencoded\r\n" );
    String postString = "j_username=" + username + "&j_password=" + password;
    out.print ("Content-length: " + postString.length() + "\r\n" );
    out.print ("\r\n");
    out.print (postString + "\r\n" );
    out.print ("\r\n");
    out.flush ();
    BufferedReader buffreader = new BufferedReader( new InputStreamReader( s.getInputStream() ) );
    String line = null;
    while( (line = buffreader.readLine()) != null ) {
      if (DEBUG) System.err.println (line);
      if( line.trim().length() == 0 ) break;
      if( line.toLowerCase().startsWith( "set-cookie:" ) ) {
	cookieLine = line.substring( "Set-Cookie:".length() ).trim();
	int semicolon = cookieLine.indexOf( ";" );
	if( semicolon != -1 ) {
	  cookieLine = cookieLine.substring( 0, semicolon ).trim();
	}
	break;
      }
    }
    buffreader.close ();
    out.close ();
    s.close ();
    return cookieLine;
  }

  public int getResponseCode0 (String file)
    throws Exception
  {
    HttpURLConnection urlconn = null;
    try {
      String target = file + "?jsp_precompile=true";
      URL url = new URL ("http", host, port, target);
      urlconn = (HttpURLConnection) url.openConnection ();
      urlconn.connect ();
      // InputStream is = urlconn.getInputStream ();
      // while ( is.read (b) != -1 ) { }
      int respCode = urlconn.getResponseCode ();
      return respCode;
    }
    finally {
      try { if (urlconn != null) urlconn.disconnect (); }
      catch (Exception exc2) { }
    }
  }

*/
