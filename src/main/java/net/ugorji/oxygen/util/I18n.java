/* <<< COPYRIGHT START >>>
 * Copyright 2006-Present OxygenSoftwareLibrary.com
 * Licensed under the GNU Lesser General Public License.
 * http://www.gnu.org/licenses/lgpl.html
 *
 * @author: Ugorji Nwoke
 * <<< COPYRIGHT END >>>
 */

package net.ugorji.oxygen.util;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Internationalization class. This provides accessor methods for getting the appropriate strings.
 *
 * @author ugorji
 */
public class I18n {
  private ResourceBundle m_rb;
  private Locale m_locale;
  private String m_path;

  public I18n(String path, Locale _locale) {
    m_path = path;
    m_locale = _locale;
    if (m_locale == null) {
      m_locale = Locale.getDefault();
    }
    m_rb = ResourceBundle.getBundle(m_path, m_locale);
    // m_rb = new OxygenResourceBundle(m_path, m_locale);
  }

  /**
   * Given a key, and an array of arguments, get the appropriate localized message.
   *
   * @param key
   * @param args
   * @return
   */
  public String str(String key, String[] args) {
    String str = m_rb.getString(key);
    if (args != null && args.length > 0) {
      MessageFormat fmt = new MessageFormat(str);
      str = fmt.format(args);
    }
    return str;
  }

  /**
   * Convenience method, calls: get(key, new String[]{args}) or get(key, null) appropriately
   *
   * @param key
   * @param arg
   * @return
   */
  public String str(String key, String arg) {
    String[] args = null;
    if (arg != null) {
      args = new String[] {arg};
    }
    return str(key, args);
  }

  public String str(String key, String arg1, String arg2) {
    String[] args = new String[] {arg1, arg2};
    return str(key, args);
  }

  /**
   * Convenience method: calls get(key, null)
   *
   * @param key
   * @return
   */
  public String str(String key) {
    String[] args = null;
    return str(key, args);
  }

  /** return the resource bundle */
  // public ResourceBundle rb() {
  //  return m_rb;
  // }

  public Locale locale() {
    return m_locale;
  }
}
