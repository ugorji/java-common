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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import net.ugorji.oxygen.util.OxygenUtils;
import net.ugorji.oxygen.util.StringUtils;

/**
 * Represents a standard file as a VirtualFile
 *
 * @author ugorji
 */
public class VirtualPlainFile implements VirtualWritableFile {

  private File f;

  public VirtualPlainFile(File _f) {
    f = _f;
  }

  public String toString() {
    return "VirtualPlainFile: " + getName();
  }

  public File getFile() {
    return f;
  }

  public String getName() {
    return f.getName();
  }

  public boolean exists() throws IOException {
    return f.exists();
  }

  public long lastModified() throws IOException {
    return f.lastModified();
  }

  public long size() throws IOException {
    return f.length();
  }

  public InputStream getInputStream() throws IOException {
    return new FileInputStream(f);
  }

  public VirtualFile getParent() throws IOException {
    return new VirtualPlainFile(f.getParentFile());
  }

  public void mkdirs() throws IOException {
    OxygenUtils.mkdirs(f);
  }

  public OutputStream getOutputStream() throws IOException {
    return new FileOutputStream(f);
  }

  public void delete() throws IOException {
    OxygenUtils.deleteFile(f);
  }

  public boolean isDirectory() throws IOException {
    return f.isDirectory();
  }

  public VirtualFile[] list(VirtualFileFilter vff, int maxdepth) throws IOException {
    maxdepth = Math.min(maxdepth, Integer.MAX_VALUE / 2);
    ArrayList list = new ArrayList();
    listFiles(list, this, vff, 0, maxdepth);
    return (VirtualFile[]) list.toArray(new VirtualFile[0]);
  }

  private void listFiles(
      List list, VirtualPlainFile vf, VirtualFileFilter vff, int currDepth, int maxDepth)
      throws IOException {
    currDepth++;
    if (currDepth > maxDepth) {
      return;
    }
    if (vf.f.exists() && vf.f.isDirectory()) {
      File[] files = vf.f.listFiles();
      for (int i = 0; i < files.length; i++) {
        VirtualPlainFile vpf = new VirtualPlainFile(files[i]);
        if (vff == null || vff.accept(vpf)) {
          list.add(vpf);
        }
        if (files[i].isDirectory()) {
          listFiles(list, vpf, vff, currDepth, maxDepth);
        }
      }
    }
  }

  public VirtualFile getChild(String name0) throws IOException {
    name0 = StringUtils.getCleanPathName(name0, SEPARATOR_CHAR);
    if (StringUtils.isBlank(name0)) {
      return this;
    }
    File f2 = new File(f, name0);
    return new VirtualPlainFile(f2);
  }

  public String getPath() {
    return f.getAbsolutePath().replace('\\', '/');
  }

  public int compareTo(Object o) {
    VirtualPlainFile f2 = (VirtualPlainFile) o;
    return f.compareTo(f2.getFile());
  }

  public void close() {}
}

/*

  private VirtualFile[] list(VirtualFileFilter vff) throws IOException {
    ArrayList list = new ArrayList();
    if(f.exists() && f.isDirectory()) {
      File[] files = f.listFiles();
      for(int i = 0; i < files.length; i++) {
        VirtualPlainFile vpf = new VirtualPlainFile(files[i]);
        if(vff == null || vff.accept(vpf)) {
          list.add(vpf);
        }
      }
    }
    return (VirtualPlainFile[])list.toArray(new VirtualPlainFile[0]);
  }

  private VirtualFile[] listAll(VirtualFileFilter vff) throws IOException {
    ArrayList al = new ArrayList();
    OxygenUtils.listFiles(f, true, al);
    ArrayList list = new ArrayList(al.size());
    for(Iterator itr = al.iterator(); itr.hasNext(); ) {
      VirtualPlainFile vpf = new VirtualPlainFile((File)itr.next());
      if(vff == null || vff.accept(vpf)) {
        list.add(vpf);
      }
    }
    return (VirtualPlainFile[])list.toArray(new VirtualPlainFile[0]);
  }

*/
