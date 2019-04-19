/* <<< COPYRIGHT START >>>
 * Copyright 2006-Present OxygenSoftwareLibrary.com
 * Licensed under the GNU Lesser General Public License.
 * http://www.gnu.org/licenses/lgpl.html
 *
 * @author: Ugorji Nwoke
 * <<< COPYRIGHT END >>>
 */

package net.ugorji.oxygen.test;

import java.io.File;
import java.net.URL;
import java.net.URLDecoder;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.LdapContext;
import net.ugorji.oxygen.util.OxygenUtils;

public class SimpleTest {

  public static void main(String[] args) throws Exception {
    testpolymorphism(args);
  }

  private static void testurldecode(String[] args) throws Exception {
    URL url =
        new URL(
            "http://www-03.ibm.com/press/us/en/rssfeed.wss?keyword=null&maxFeed=&feedType=RSS&topic=242");
    System.out.println("hi");
    System.out.println("%2F = " + URLDecoder.decode("%2F", "UTF-8"));
  }

  private static void testwidepanel(String[] args) throws Exception {
    args = new String[] {"17", "19", "20", "23", "27", "32", "37", "40", "42"};
    DecimalFormat df = new DecimalFormat("##.##");
    double x = Math.pow(16.0, 2.0) + Math.pow(9.0, 2.0);
    x = Math.pow(x, 0.5);
    for (int i = 0; i < args.length; i++) {
      double d = Double.parseDouble(args[i]);
      double l = (9.0 * d / x);
      double w = (16.0 * d / x);
      System.out.println("d, l, w = " + df.format(d) + ", " + df.format(l) + ", " + df.format(w));
    }
  }

  private static void testlistfiles(String[] args) throws Exception {
    args = new String[] {"c:/weblogic/internal", "2"};
    Collection bucket = new ArrayList();
    OxygenUtils.listFiles(new File(args[0]), Integer.parseInt(args[1]), bucket, null);
    for (Iterator itr = bucket.iterator(); itr.hasNext(); ) {
      System.out.println((File) itr.next());
    }
  }

  private static void testpolymorphism(String[] args) throws Exception {
    polymorphism(null);
    polymorphism((ArrayList) null);
    polymorphism((Thread) null);
    polymorphism(new ArrayList());
    polymorphism((List) new ArrayList());
    polymorphism((Object) new ArrayList());
  }

  private static void polymorphism(Object o) throws Exception {
    System.out.println("Called for Object: " + o);
  }

  private static void polymorphism(ArrayList o) throws Exception {
    System.out.println("Called for ArrayList: " + o);
  }

  private static void polymorphism(List o) throws Exception {
    System.out.println("Called for List: " + o);
  }

  private static void testldap(String[] args) throws Exception {
    Hashtable env = new Hashtable();

    env.put(LdapContext.CONTROL_FACTORIES, "com.sun.jndi.ldap.ControlFactory");
    env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
    env.put(Context.STATE_FACTORIES, "PersonStateFactory");
    env.put(Context.OBJECT_FACTORIES, "PersonObjectFactory");
    env.put(
        Context.PROVIDER_URL,
        "ldap://ldap.bea.com/dc=MetaView,dc=bea,dc=com?uid?sub?(objectClass=*)");
    env.put("java.naming.ldap.derefAliases", "always");
    DirContext ctx = new InitialDirContext(env);

    System.out.println("Hi");
    NamingEnumeration answer = null;
    String filter = null;
    Attributes matchAttrs = new BasicAttributes(true);
    matchAttrs.put(new BasicAttribute("uid", "ugorjidkkk"));
    // matchAttrs.put(new BasicAttribute("uid", "hsahai"));
    // matchAttrs.put(new BasicAttribute("dn",
    // "uniqueIdentifier=0604,ou=People,dc=MetaView,dc=bea,dc=com"));
    // matchAttrs.put(new BasicAttribute("manager",
    // "uniqueIdentifier=0604,ou=People,dc=MetaView,dc=bea,dc=com"));
    answer = ctx.search("ou=people", matchAttrs);

    // filter = "(&(manager=*0604*)(uid=*))";
    // filter = "(manager=~*0604*)";
    // filter = "(manager=uniqueIdentifier=0604,ou=People,dc=MetaView,dc=bea,dc=com)";
    // filter = "(manager=uid=hsahai,ou=People,dc=MetaView,dc=bea,dc=com)";
    // filter = "(manager~=0604)";
    // filter = "(uid=hsahai)";
    // answer = ctx.search("ou=People", filter, new SearchControls());

    printAnswer(answer);
    // printAttributes(ctx.getAttributes("uniqueIdentifier=0604,ou=People,dc=MetaView,dc=bea,dc=com"));
  }

  private static void printAnswer(NamingEnumeration answer) throws Exception {
    while (answer.hasMore()) {
      SearchResult sr = (SearchResult) answer.next();
      System.out.println(">>>" + sr.getName());
      printAttributes(sr.getAttributes());
    }
  }

  private static void printAttributes(Attributes attrs) throws Exception {
    for (NamingEnumeration ae = attrs.getAll(); ae.hasMore(); ) {
      Attribute attr = (Attribute) ae.next();
      System.out.println("attribute: " + attr.getID());
      for (NamingEnumeration e = attr.getAll(); e.hasMore(); ) {
        Object o = e.next();
        if (!(o instanceof String)) {
          System.out.println("value class: " + o.getClass().getName());
        }
        System.out.println("value: " + o);
      }
    }
  }
}
