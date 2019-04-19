/* <<< COPYRIGHT START >>>
 * Copyright 2006-Present OxygenSoftwareLibrary.com
 * Licensed under the GNU Lesser General Public License.
 * http://www.gnu.org/licenses/lgpl.html
 *
 * @author: Ugorji Nwoke
 * <<< COPYRIGHT END >>>
 */

package net.ugorji.oxygen.logging;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Filter;
import java.util.logging.Handler;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import net.ugorji.oxygen.util.StringToPrimitiveConverter;

/**
 * <pre>
 * <configuration>
 * <property name="" value="" />
 * <logger name="com.bea.server" removehandlers="true" classname="" methodA="" methodB="" />
 * <logger name="com.bea.domain" classname="" methodA="" methodB="" />
 * <logger name="com.bea.access" classname="" methodA="" methodB="" />
 * <logger name="com.bea.server.applications.myear1" classname="" methodA="" methodB="" >
 * <filter classname="" methodA="" methodB="" />
 * <handler classname="" methodA="" methodB="" >
 * <filter classname="" methodA="" methodB="" />
 * </handler>
 * <handler classname="" methodA="" methodB="" />
 * </logger>
 * </configuration>
 * </pre>
 * <pre>
 */
public class OxyLMXMLParser extends DefaultHandler {
  private int ADDING_LOGGER = 1;
  private int ADDING_HANDLER = 2;
  // private Class[] stringclassarr = new Class[]{String.class};
  private Logger currlogger = null;
  private Handler currhandler = null;
  private int state = 0;

  public OxyLMXMLParser() {}

  private Logger createInstance(String classname, String logname) throws Exception {
    Logger lg = null;
    Constructor[] constructors = Class.forName(classname).getConstructors();
    for (int i = 0; i < constructors.length; i++) {
      Class[] paramtypes = constructors[i].getParameterTypes();
      if (paramtypes.length == 2
          && paramtypes[0].equals(String.class)
          && paramtypes[1].equals(ResourceBundle.class)) {
        lg = (Logger) constructors[i].newInstance(new Object[] {logname, null});
        break;
      } else if (paramtypes.length == 1 && paramtypes[0].equals(String.class)) {
        lg = (Logger) constructors[i].newInstance(new Object[] {logname});
        break;
      }
    }
    return lg;
  }

  public void startElement(String uri, String localName, String qName, Attributes attributes)
      throws SAXException {
    try {
      String name = attributes.getValue("name");
      boolean removeHandlers = "true".equals(attributes.getValue("removehandlers"));
      String classname = attributes.getValue("classname");
      String value = attributes.getValue("value");
      LogManager logmgr = LogManager.getLogManager();

      if ("property".equals(qName)) {
        if (logmgr instanceof OxyLogManager) {
          ((OxyLogManager) logmgr).setProperty(name, value);
        }
      } else if ("logger".equals(qName)) {
        state = state | ADDING_LOGGER;
        Logger lg = logmgr.getLogger(name);
        if (lg == null) {
          if (classname != null) {
            lg = createInstance(classname, name);
            if (lg != null) {
              logmgr.addLogger(lg);
            }
          }
        }
        if (lg != null) {
          callSetMethodsOnObject(lg, attributes);
        }
        lg = Logger.getLogger(name);
        if (removeHandlers) {
          Handler[] hdlrs = lg.getHandlers();
          for (int i = 0; i < hdlrs.length; i++) {
            hdlrs[i].close();
            lg.removeHandler(hdlrs[i]);
          }
        }
        currlogger = lg;
      } else if ("handler".equals(qName)) {
        state = state | ADDING_HANDLER;
        Handler hdlr = (Handler) Class.forName(classname).newInstance();
        callSetMethodsOnObject(hdlr, attributes);
        currlogger.addHandler(hdlr);
        currhandler = hdlr;
      } else if ("filter".equals(qName)) {
        Filter filter = (Filter) Class.forName(classname).newInstance();
        callSetMethodsOnObject(filter, attributes);
        if ((state & ADDING_HANDLER) == ADDING_HANDLER) {
          currhandler.setFilter(filter);
        } else if ((state & ADDING_LOGGER) == ADDING_LOGGER) {
          currlogger.setFilter(filter);
        }
      }
    } catch (SAXException se) {
      // se.printStackTrace();
      throw se;
    } catch (Exception exc) {
      // exc.printStackTrace();
      throw new SAXException(exc);
    }
  }

  public void endElement(String uri, String localName, String qName) throws SAXException {
    if ("logger".equals(qName)) {
      state = state & (~ADDING_LOGGER);
      currlogger = null;
    } else if ("handler".equals(qName)) {
      state = state & (~ADDING_HANDLER);
      currhandler = null;
    }
  }

  private void callSetMethodsOnObject(Object obh, Attributes atts) throws Exception {
    Class clazz = obh.getClass();
    Map attsmap = new HashMap();
    int len = atts.getLength();
    for (int i = 0; i < len; i++) {
      String key = atts.getQName(i).toLowerCase();
      String val = atts.getValue(i);
      attsmap.put(key, val);
    }
    // System.out.println("attsmap: " + attsmap);
    Method[] methods = clazz.getMethods();
    for (int i = 0; i < methods.length; i++) {
      Method meth = methods[i];
      String methname = meth.getName();
      // System.out.println("methname: " + methname);
      Class[] paramtypes = meth.getParameterTypes();
      if (methname.startsWith("set") && paramtypes.length == 1) {
        // System.out.println("paramtypes[0]: " + paramtypes[0]);
        String key = methname.substring(3).toLowerCase();
        String val = (String) attsmap.get(key);
        if (val != null) {
          if (paramtypes[0].equals(String.class)) {
            meth.invoke(obh, new Object[] {val});
          } else if (paramtypes[0].equals(Character.TYPE)) {
            meth.invoke(obh, new Object[] {new Character(StringToPrimitiveConverter.toChar(val))});
          } else if (paramtypes[0].equals(Integer.TYPE)) {
            meth.invoke(obh, new Object[] {new Integer(StringToPrimitiveConverter.toInt(val))});
          } else if (paramtypes[0].equals(Boolean.TYPE)) {
            // System.out.println("Calling boolean");
            meth.invoke(obh, new Object[] {new Boolean(StringToPrimitiveConverter.toBoolean(val))});
          } else if (paramtypes[0].equals(Double.TYPE)) {
            meth.invoke(obh, new Object[] {new Double(StringToPrimitiveConverter.toDouble(val))});
          } else if (paramtypes[0].equals(Object.class)) {
            meth.invoke(obh, new Object[] {val});
          }
        }
      }
    }
  }
}
