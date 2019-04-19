/* <<< COPYRIGHT START >>>
 * Copyright 2006-Present OxygenSoftwareLibrary.com
 * Licensed under the GNU Lesser General Public License.
 * http://www.gnu.org/licenses/lgpl.html
 *
 * @author: Ugorji Nwoke
 * <<< COPYRIGHT END >>>
 */

package net.ugorji.oxygen.manager;

import java.io.File;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import net.ugorji.oxygen.util.StringUtils;

public class FSUserPasswordManager implements UserPasswordManager {
  private File file;
  private Map mapping = new HashMap();
  private long lastmod = -1;
  private String encoding = null;
  private String algorithm = null;
  private boolean doEncrypt = false;
  private MessageDigest md = null;

  public void init(Properties p) throws Exception {
    String s = null;

    // System.out.println("FSUserPasswordManager: init props: " + p);
    s = p.getProperty(PROPS_PREFIX + "password.digest.encoding");
    encoding = (StringUtils.isBlank(s)) ? encoding : s;
    s = p.getProperty(PROPS_PREFIX + "password.digest.algorithm");
    algorithm = (StringUtils.isBlank(s)) ? algorithm : s;
    doEncrypt = !(StringUtils.isBlank(algorithm));

    if (doEncrypt) {
      md = (MessageDigest) MessageDigest.getInstance(algorithm).clone();
    }

    // System.out.println("FSUserPasswordManager: encoding: " + encoding + " | algorithm: " +
    // algorithm + " | doEncrypt: " + doEncrypt);

    s = p.getProperty(PROPS_PREFIX + "password.file");
    s = StringUtils.replacePropertyReferencesInString(s, p);
    file = new File(s);
    sync();
  }

  public void setPassword(String username, char[] passwd) throws Exception {
    String encPasswd = encrypt(new String(passwd));
    mapping.put(username, encPasswd);
  }

  public boolean checkPassword(String username, char[] passwd) throws Exception {
    boolean b = false;
    String savedEncPasswd = (String) mapping.get(username);
    if (savedEncPasswd != null) {
      String encPasswd = encrypt(new String(passwd));
      b = savedEncPasswd.equals(encPasswd);
    }
    return b;
  }

  public char[] getEncryptedPassword(String username) throws Exception {
    return ((String) mapping.get(username)).toCharArray();
  }

  public void save(Properties metadata) throws Exception {
    ManagerUtils.saveMapForManagers(mapping, file, null, ':');
  }

  public void close() {}

  public void sync() throws Exception {
    long lastmod2 = file.lastModified();
    if (lastmod2 > lastmod) {
      mapping.clear();
      ManagerUtils.loadMapForManagers(mapping, file, null);
      lastmod = lastmod2;
    }
  }

  public String encrypt(String s) throws Exception {
    String s2 = s;
    if (doEncrypt) {
      synchronized (md) {
        md.update(s.getBytes(encoding));
        s2 = Base64.getEncoder().encodeToString(md.digest());
        md.reset();
      }
    }
    // System.out.println("FSUserPasswordManager: encrypt for: " + s + " = " + s2 + "||");
    return s2;
  }

  private static String encrypt(String s, String alg, String enc) throws Exception {
    MessageDigest md = (MessageDigest) MessageDigest.getInstance(alg).clone();
    md.update(s.getBytes(enc));
    return Base64.getEncoder().encodeToString(md.digest());
  }

  public static void main(String[] args) throws Exception {
    System.out.println(encrypt("ugorji", "MD5", "UTF-8"));
  }
}
