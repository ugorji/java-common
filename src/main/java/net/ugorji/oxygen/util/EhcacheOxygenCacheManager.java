/* <<< COPYRIGHT START >>>
 * Copyright 2006-Present OxygenSoftwareLibrary.com
 * Licensed under the GNU Lesser General Public License.
 * http://www.gnu.org/licenses/lgpl.html
 *
 * @author: Ugorji Nwoke
 * <<< COPYRIGHT END >>>
 */

package net.ugorji.oxygen.util;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Pattern;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

public abstract class EhcacheOxygenCacheManager implements OxygenCacheManager {
  protected CacheManager cm;

  public void close() throws Exception {
    // System.out.println("Closing EhcacheBasedWikiCacheManager");
    // Thread.dumpStack();
    cm.shutdown();
    // cm.dispose();
  }

  public void put(String group, String key, Object value) {
    Cache cache = getCache(group);
    Element element = new Element((Serializable) key, (Serializable) value);
    cache.put(element);
    // cache.flush();
  }

  public Object get(String group, String key) {
    Cache cache = getCache(group);
    Object rtn = null;
    try {
      Element element = cache.get(key);
      if (element != null) {
        rtn = element.getValue();
      }
    } catch (Exception exc) {
      throw new RuntimeException(exc);
    }
    return rtn;
  }

  public Map getAll(String group, Pattern keyMatches) {
    Map m = new HashMap();
    Cache cache = getCache(group);
    for (Iterator itr = cache.getKeys().iterator(); itr.hasNext(); ) {
      String s = (String) itr.next();
      if (keyMatches == null || keyMatches.matcher(s).matches()) {
        Object o = get(group, s);
        if (o != null) {
          m.put(s, o);
        }
      }
    }
    return m;
  }

  public void remove(String group, String key) {
    Cache cache = getCache(group);
    cache.remove((Serializable) key);
  }

  private synchronized Cache getCache(String group) {
    try {
      if (!(cm.cacheExists(group))) {
        cm.addCache(group);
      }
      return cm.getCache(group);
    } catch (Exception exc) {
      // this should never happen, since we check first
      throw new RuntimeException(exc);
    }
  }

  public void clear() {
    cm.removalAll();
  }
}
