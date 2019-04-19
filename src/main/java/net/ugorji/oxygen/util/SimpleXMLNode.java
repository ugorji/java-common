/* <<< COPYRIGHT START >>>
 * Copyright 2006-Present OxygenSoftwareLibrary.com
 * Licensed under the GNU Lesser General Public License.
 * http://www.gnu.org/licenses/lgpl.html
 *
 * @author: Ugorji Nwoke
 * <<< COPYRIGHT END >>>
 */

package net.ugorji.oxygen.util;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

public class SimpleXMLNode {
  public String name;
  public String text;
  public SimpleXMLNode[] children;
  public SimpleXMLNode parent;
  public Attrs attrs;
  private List childrenList;
  private StringBuffer textbuf;

  public SimpleXMLNode() {}

  public static SimpleXMLNode load(InputStream is) throws Exception {
    Hdlr hdlr = new Hdlr();
    SAXParserFactory factory = SAXParserFactory.newInstance();
    SAXParser parser = factory.newSAXParser();
    parser.parse(is, hdlr);
    return hdlr.getSimpleXMLNode();
  }

  public static void removeHierachyRef(SimpleXMLNode parent, SimpleXMLNode child) {
    if (child.parent == parent) child.parent = null;
    List parentchildren = Arrays.asList(parent.children);
    if (parentchildren.contains(child)) {
      List list = new ArrayList(parentchildren);
      list.remove(child);
      parent.children = (SimpleXMLNode[]) parentchildren.toArray(new SimpleXMLNode[0]);
    }
  }

  public static class Attrs {
    public String[] keys;
    public String[] values;

    public String getValue(String key) {
      if (keys == null || values == null || key == null) return null;
      for (int i = 0; i < keys.length; i++) {
        if (key.equals(keys[i])) return (values[i]);
      }
      return null;
    }
  }

  private static class Hdlr extends DefaultHandler {
    private SimpleXMLNode snode;
    private SimpleXMLNode currnode;
    private SimpleXMLNode ancestornode;

    public SimpleXMLNode getSimpleXMLNode() {
      return snode;
    }

    public void characters(char[] ch, int start, int length) {
      String str = String.valueOf(ch, start, length);
      if (currnode.textbuf == null) currnode.textbuf = new StringBuffer(str);
      else currnode.textbuf.append(str);
    }

    public void startElement(String uri, String localName, String qName, Attributes atts) {
      currnode = new SimpleXMLNode();
      currnode.name = localName;
      int len = -1;
      if (atts != null && (len = atts.getLength()) > 0) {
        currnode.attrs = new Attrs();
        currnode.attrs.keys = new String[len];
        currnode.attrs.values = new String[len];
        for (int i = 0; i < len; i++) {
          currnode.attrs.keys[i] = atts.getLocalName(i);
          currnode.attrs.values[i] = atts.getValue(i);
        }
      }

      if (ancestornode != null) {
        currnode.parent = ancestornode;
        if (ancestornode.childrenList == null) ancestornode.childrenList = new ArrayList(4);
        ancestornode.childrenList.add(currnode);
      }

      if (snode == null) snode = currnode;

      ancestornode = currnode;
    }

    public void endElement(String uri, String localName, String qName) {
      if (currnode.textbuf != null) {
        currnode.text = currnode.textbuf.toString();
        currnode.textbuf = null;
      }

      if (currnode.childrenList != null && currnode.childrenList.size() > 0) {
        currnode.children = (SimpleXMLNode[]) currnode.childrenList.toArray(new SimpleXMLNode[0]);
        currnode.childrenList.clear();
        currnode.childrenList = null;
      }
      ancestornode = currnode.parent;
    }
  }
}
