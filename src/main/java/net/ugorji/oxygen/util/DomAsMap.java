/* <<< COPYRIGHT START >>>
 * Copyright 2006-Present OxygenSoftwareLibrary.com
 * Licensed under the GNU Lesser General Public License.
 * http://www.gnu.org/licenses/lgpl.html
 *
 * @author: Ugorji Nwoke
 * <<< COPYRIGHT END >>>
 */

package net.ugorji.oxygen.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Map representation of a DOM Node object.<br>
 * Takes a node like:<br>
 * <XMP> <test someattr="v1, v1, v3"> <name>UgorjiServletWritableTest</name>
 * <assertion>UgorjiServletWritableTest</assertion>
 * <description>UgorjiServletWritableTest</description> <phase>3</phase> <test-run mode="automatic"
 * stage="active" /> <valid-period from="Servlet 2.0" to="Servlet 2.3"/>
 * <deploy-agent>ServletWritable</deploy-agent> <test-object>
 * <test-class>weblogic.qa.xmlbasedtests.tryout.UgorjiServletWritableTest</test-class>
 * <test-method>testUgorjiAsHuman</test-method> <test-method>testUgorjiAsMachine</test-method>
 * </test-object> <associated-file>UgorjiServletWritableTest.java</associated-file>
 * <http-url-connection> <check-mode>RESPONSE_STRING</check-mode> <expected-string><![CDATA[ Ugorji
 * is a human being Ugorji is a machine ]]></expected-string> <param name="human" value="true" />
 * <param name="machine" value="true" /> </http-url-connection> </test> </XMP> <br>
 * you will get:<br>
 * <XMP> #someattr -> v1, v1, v3 assertion!1 -> UgorjiServletWritableTest assertion!size -> 1
 * associated-file!1 -> UgorjiServletWritableTest.java associated-file!size -> 1 deploy-agent!1 ->
 * ServletWritable deploy-agent!size -> 1 description!1 -> UgorjiServletWritableTest
 * description!size -> 1 http-url-connection!1:check-mode!1 -> RESPONSE_STRING
 * http-url-connection!1:check-mode!size -> 1 http-url-connection!1:expected-string!1 -> Ugorji is a
 * human being Ugorji is a machine
 *
 * <p>http-url-connection!1:expected-string!size -> 1 http-url-connection!1:param!1#name -> human
 * http-url-connection!1:param!1#value -> true http-url-connection!1:param!2#name -> machine
 * http-url-connection!1:param!2#value -> true http-url-connection!1:param!size -> 2
 * http-url-connection!size -> 1 name!1 -> UgorjiServletWritableTest name!size -> 1 phase!1 -> 3
 * phase!size -> 1 test-object!1:test-class!1 ->
 * weblogic.qa.xmlbasedtests.tryout.UgorjiServletWritableTest test-object!1:test-class!size -> 1
 * test-object!1:test-method!1 -> testUgorjiAsHuman test-object!1:test-method!2 ->
 * testUgorjiAsMachine test-object!1:test-method!size -> 2 test-object!size -> 1 test-run!1#mode ->
 * automatic test-run!1#stage -> active test-run!size -> 1 valid-period!1#from -> Servlet 2.0
 * valid-period!1#to -> Servlet 2.3 valid-period!size -> 1 </XMP>
 *
 * @author Ugorji Nwoke ugorji@gmail.com
 * @version 1.0, Mar 3, 2001
 */
public class DomAsMap {
  private Map info;
  private String name;

  public DomAsMap(Node node) {
    name = node.getNodeName();
    info = load(node, info);
  }

  /** Returns the name of the DOM Node */
  public String getName() {
    return name;
  }

  /** Returns the Map of Information got from this node */
  public Map getInfo() {
    return info;
  }

  /** Convenience method to extract a value from the map of information */
  public String getValue(String key) {
    String val = (String) info.get(key);
    return val;
  }

  /** For a given entry, get the number of times it exists in the map */
  public int getSizeForEntry(String str) {
    return getSizeForEntry(info, str);
  }

  /** Static Method to load a Node into a map representation */
  public static Map load(Node n, Map info) {
    Map map = info;
    if (map == null) map = new HashMap();
    Map sizes = new HashMap();

    NamedNodeMap nnm = n.getAttributes();
    if (nnm != null) {
      int len = nnm.getLength();
      for (int i = 0; i < len; i++) {
        Node n2 = nnm.item(i);
        map.put("#" + n2.getNodeName(), n2.getNodeValue());
      }
    }

    NodeList nl = n.getChildNodes();
    int len = nl.getLength();
    for (int i = 0; i < len; i++) {
      Node n2 = nl.item(i);
      if (n2.getNodeType() == Node.ELEMENT_NODE) getRecursiveMap(n2, map, "", sizes);
    }
    for (Iterator itr = sizes.entrySet().iterator(); itr.hasNext(); ) {
      Map.Entry entry = (Map.Entry) itr.next();
      map.put(entry.getKey() + "!size", entry.getValue());
    }
    return map;
  }

  /** For a given entry, get the number of times it exists in the map */
  public static int getSizeForEntry(Map info, String str) {
    int size = 0;
    Integer ii = (Integer) info.get(str + "!size");
    if (ii != null) size = ii.intValue();
    return size;
  }

  private static void getRecursiveMap(Node n, Map map, String path, Map sizes) {
    String path2 = n.getNodeName();
    if (path.length() > 0) path2 = path + ":" + path2;
    Integer size = (Integer) sizes.get(path2);
    if (size == null) size = new Integer(1);
    else size = new Integer(size.intValue() + 1);
    sizes.put(path2, size);
    path2 = path2 + "!" + size;

    NamedNodeMap nnm = n.getAttributes();
    if (nnm != null) {
      int len = nnm.getLength();
      for (int i = 0; i < len; i++) {
        Node n2 = nnm.item(i);
        map.put(path2 + "#" + n2.getNodeName(), n2.getNodeValue());
      }
    }

    NodeList nl = n.getChildNodes();
    int len = nl.getLength();
    if (len > 0) {
      Node n3 = n.getFirstChild();
      int nNodeType = n3.getNodeType();
      if (nNodeType == Node.CDATA_SECTION_NODE) {
        map.put(path2, n3.getNodeValue());
      } else if (len == 1 && nNodeType == Node.TEXT_NODE) {
        map.put(path2, n3.getNodeValue());
      } else {
        for (int i = 0; i < len; i++) {
          Node n2 = nl.item(i);
          if (n2.getNodeType() == Node.ELEMENT_NODE) getRecursiveMap(n2, map, path2, sizes);
        }
      }
    }
  }
}
