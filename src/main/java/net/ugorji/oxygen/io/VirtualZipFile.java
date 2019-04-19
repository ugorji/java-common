/* <<< COPYRIGHT START >>>
 * Copyright 2006-Present OxygenSoftwareLibrary.com
 * Licensed under the GNU Lesser General Public License.
 * http://www.gnu.org/licenses/lgpl.html
 *
 * @author: Ugorji Nwoke
 * <<< COPYRIGHT END >>>
 */

package net.ugorji.oxygen.io;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import javax.swing.tree.DefaultMutableTreeNode;
import net.ugorji.oxygen.util.CloseUtils;
import net.ugorji.oxygen.util.StringUtils;

/**
 * Represents an entry in a zip or jar file, as an abstract file Note: Some entries on the Tree
 * might not exist. We put them there, like when folks call getChild(...) so we can track.
 * Consequently, things like list(...) should check if the stuff really exists
 *
 * @author ugorji
 */
public class VirtualZipFile implements VirtualFile {
  private File f;
  private ZipFile zf;
  private ZipEntry ze;
  private String name;
  private boolean exists = false;
  // private String leafname;
  private DefaultMutableTreeNode node;

  public VirtualZipFile(File _f) throws Exception {
    f = _f;
    zf = new ZipFile(f);
    name = "";
    node = new DefaultMutableTreeNode(this, true);
    exists = true;
    for (Enumeration enum0 = zf.entries(); enum0.hasMoreElements(); ) {
      ZipEntry ze0 = (ZipEntry) enum0.nextElement();
      addNode(ze0);
    }
    // Thread.dumpStack();
    // System.out.println("In VirtualZipFile: listAllNodes(false): " +
    // Arrays.asList(listAllNodes(false)));
    // System.out.println("In VirtualZipFile: listAllNodes(true): " +
    // Arrays.asList(listAllNodes(true)));
    // System.out.println("In VirtualZipFile: list(MAX): " + Arrays.asList(list(null,
    // Integer.MAX_VALUE - 2000)));
  }

  private VirtualZipFile(
      File _f, ZipFile _zf, ZipEntry _ze, DefaultMutableTreeNode _node, String _name) {
    f = _f;
    zf = _zf;
    name = _name;
    node = _node;
    ze = _ze;
  }

  private void resetZipEntry(ZipEntry ze0) {
    ze = ze0;
    node.setAllowsChildren(ze == null ? false : ze.isDirectory());
  }

  public String toString() {
    return getPath();
  }

  public String getName() {
    return name;
  }

  public boolean exists() throws IOException {
    return exists;
  }

  public long lastModified() throws IOException {
    return (ze == null ? -1 : ze.getTime());
  }

  public long size() throws IOException {
    return (ze == null ? -1 : ze.getSize());
  }

  public InputStream getInputStream() throws IOException {
    return zf.getInputStream(ze);
  }

  public VirtualFile getParent() throws IOException {
    DefaultMutableTreeNode pnode0 = (DefaultMutableTreeNode) node.getParent();
    VirtualFile vf = null;
    if (pnode0 != null) {
      vf = (VirtualFile) pnode0.getUserObject();
    }
    return vf;
  }

  public boolean isDirectory() throws IOException {
    return node.getAllowsChildren();
  }

  public VirtualFile[] list(VirtualFileFilter vff, int maxdepth) throws IOException {
    maxdepth = Math.min(maxdepth, Integer.MAX_VALUE / 2);
    ArrayList list = new ArrayList();
    Enumeration enum0 = (maxdepth <= 1 ? node.children() : node.breadthFirstEnumeration());
    while (enum0.hasMoreElements()) {
      DefaultMutableTreeNode node1 = (DefaultMutableTreeNode) enum0.nextElement();
      VirtualZipFile vzf = (VirtualZipFile) node1.getUserObject();
      // System.out.print(vzf.exists() ? "X" : "Y");
      if (vzf.exists()
          && (vff == null || vff.accept(vzf))
          && (maxdepth <= 1
              || (vzf.node.getUserObjectPath().length
                  <= (node.getUserObjectPath().length + maxdepth)))) {
        list.add(vzf);
      }
    }
    // System.out.println("Z");
    // OxygenUtils.p("In VirtualZipFile.list(): " + list);
    return (VirtualZipFile[]) list.toArray(new VirtualZipFile[0]);
  }

  // a
  // a/b
  // a/b/c.txt
  // a/b/c/d.txt

  public VirtualFile getChild(String name0) throws IOException {
    // OxygenUtils.p("In VirtualZipFile: " + " name: " + name + " getChild(): " + name0);
    name0 = StringUtils.getCleanPathName(name0, SEPARATOR_CHAR);
    if (StringUtils.isBlank(name0)) {
      return this;
    }
    String[] names = name0.split("/");
    DefaultMutableTreeNode pnode0 = node;
    DefaultMutableTreeNode node0 = null;
    for (int i = 0; i < names.length; i++) {
      if (names[i].length() == 0) {
        continue;
      }
      node0 = getTreeNodeFor(pnode0, names[i], null);
      // OxygenUtils.p("In VirtualZipFile: names[i]: " + names[i] + " node0: " + node0);
      pnode0 = node0;
    }
    VirtualZipFile vzf = (VirtualZipFile) node0.getUserObject();
    return vzf;
  }

  public String getPath() {
    Object[] arr = node.getUserObjectPath();
    StringBuffer buf = new StringBuffer();
    for (int i = 1; i < arr.length; i++) {
      VirtualZipFile vzf = (VirtualZipFile) arr[i];
      if (i != 1) {
        buf.append("/");
      }
      buf.append(vzf.getName());
    }
    return buf.toString();
  }

  public int compareTo(Object o) {
    VirtualZipFile f2 = (VirtualZipFile) o;
    int rtn = -1;
    try {
      rtn = getPath().compareTo(f2.getPath());
    } catch (Exception exc) {
    }
    return rtn;
  }

  public void close() {
    CloseUtils.close(zf);
    zf = null;
  }

  private void addNode(ZipEntry ze0) {
    String name0 = ze0.getName();
    String[] names = name0.split("/");
    DefaultMutableTreeNode pnode0 = node;
    DefaultMutableTreeNode node0 = null;
    VirtualZipFile vzf0 = null;
    for (int i = 0; i < names.length; i++) {
      if (names[i].length() == 0) {
        continue;
      }
      node0 = getTreeNodeFor(pnode0, names[i], null);
      node0.setAllowsChildren(true);
      vzf0 = ((VirtualZipFile) node0.getUserObject());
      vzf0.exists = true;
      pnode0 = node0;
    }
    vzf0.resetZipEntry(ze0);
  }

  private DefaultMutableTreeNode getTreeNodeFor(
      DefaultMutableTreeNode pnode0, String name0, ZipEntry ze0) {
    // OxygenUtils.p("In VirtualZipFile.getTreeNodeFor: " + " pnode0: " + pnode0 + " name0: " +
    // name0);
    DefaultMutableTreeNode node0 = null;
    int count0 = pnode0.getChildCount();
    for (int j = 0; j < count0; j++) {
      DefaultMutableTreeNode node1 = (DefaultMutableTreeNode) pnode0.getChildAt(j);
      VirtualZipFile vzf = (VirtualZipFile) node1.getUserObject();
      // OxygenUtils.p("In VirtualZipFile.getTreeNodeFor: " + " node1.vzf.getName(): " +
      // vzf.getName());
      if (name0.equals(vzf.getName())) {
        node0 = node1;
        break;
      }
    }
    // OxygenUtils.p("In VirtualZipFile.getTreeNodeFor: " + " node0: " + node0);
    if (node0 == null) {
      boolean bb = (ze0 == null ? false : ze0.isDirectory());
      node0 = new DefaultMutableTreeNode(name0, bb);
      pnode0.setAllowsChildren(true);
      pnode0.add(node0);
      // node0.setParent(pnode0);
      VirtualZipFile vzf = new VirtualZipFile(f, zf, ze0, node0, name0);
      node0.setUserObject(vzf);
    }

    return node0;
  }

  private DefaultMutableTreeNode[] listAllNodes(boolean onlyExists) throws IOException {
    ArrayList list = new ArrayList();
    for (Enumeration enum0 = node.breadthFirstEnumeration(); enum0.hasMoreElements(); ) {
      DefaultMutableTreeNode node1 = (DefaultMutableTreeNode) enum0.nextElement();
      if (node1 == node) {
        continue;
      }
      VirtualZipFile vzf0 = (VirtualZipFile) node1.getUserObject();
      if (!onlyExists || vzf0.exists) {
        list.add(node1);
      }
    }
    return (DefaultMutableTreeNode[]) list.toArray(new DefaultMutableTreeNode[0]);
  }
}

/*

  private VirtualFile[] list(VirtualFileFilter vff) throws IOException {
    ArrayList list = new ArrayList();
    for(Enumeration enum0 = node.children(); enum0.hasMoreElements(); ) {
      DefaultMutableTreeNode node1 = (DefaultMutableTreeNode)enum0.nextElement();
      VirtualZipFile vzf = (VirtualZipFile)node1.getUserObject();
      if(vff == null || vff.accept(vzf)) {
        list.add(vzf);
      }
    }
    //OxygenUtils.p("In VirtualZipFile.list(): " + list);
    return (VirtualZipFile[])list.toArray(new VirtualZipFile[0]);
  }

  private VirtualFile[] listAll(VirtualFileFilter vff) throws IOException {
    ArrayList list = new ArrayList();
    for(Enumeration enum0 = node.breadthFirstEnumeration(); enum0.hasMoreElements(); ) {
      DefaultMutableTreeNode node1 = (DefaultMutableTreeNode)enum0.nextElement();
      if(node1 == node) {
        continue;
      }
      VirtualZipFile vzf = (VirtualZipFile)node1.getUserObject();
      if(vff == null || vff.accept(vzf)) {
        list.add(vzf);
      }
    }
    return (VirtualZipFile[])list.toArray(new VirtualZipFile[0]);
  }

  // disregard this ... I'm tripping ...
  private String getLeafName() {
    if(leafname == null) {
      leafname = name;
      if(name != null) {
        int idx = name.lastIndexOf("/");
        if(idx >= 0) {
          leafname = name.substring(idx + 1);
        }
      }
    }
    return leafname;
  }

*/
