/* <<< COPYRIGHT START >>>
 * Copyright 2006-Present OxygenSoftwareLibrary.com
 * Licensed under the GNU Lesser General Public License.
 * http://www.gnu.org/licenses/lgpl.html
 *
 * @author: Ugorji Nwoke
 * <<< COPYRIGHT END >>>
 */

package net.ugorji.oxygen.util;

import java.util.Map;
import java.util.regex.Pattern;

/**
 * An OxygenCacheManager supports the concepts of group, to differentiate different caches stored
 * within the same instance. Consequently, each put, get or removed must be scoped to a specific
 * group.
 */
public interface OxygenCacheManager extends Closeable {
  void prepare() throws Exception;

  public void put(String group, String key, Object value);

  public Object get(String group, String key);

  public Map getAll(String group, Pattern keyMatches);

  public void remove(String group, String key);

  public void clear();

  void close() throws Exception;
}
