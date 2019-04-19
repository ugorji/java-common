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
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import net.ugorji.oxygen.io.VirtualFile;
import net.ugorji.oxygen.io.VirtualFileFilter;
import net.ugorji.oxygen.io.VirtualWritableFile;

public class OxygenVersioningArchiveVirtualFileImpl implements OxygenVersioningArchive {
  private static final Pattern descPattern =
      Pattern.compile(".*?\\.description\\-([0-9]+)\\.txt\\.gz");

  private Pattern verPattern;
  private VirtualFile backedFile;
  private String backedFileName;
  private String extension;
  private String encoding;

  public OxygenVersioningArchiveVirtualFileImpl(VirtualFile backedFile0, String enc)
      throws Exception {
    backedFile = backedFile0;
    backedFileName = backedFile.getName();
    extension = StringUtils.getFileNameExtension(backedFileName, ".dat");
    verPattern = Pattern.compile(".*?\\.version\\-([0-9]+)\\" + extension + "\\.gz");
    encoding = enc;
    // System.out.println(" archivefile: " + archivefile + " backedFile: " + backedFile + "
    // extension: " + extension);
  }

  public boolean exists() throws Exception {
    VirtualFile f = getArchive(false).getChild(backedFileName + "." + "admin.txt.gz");
    return f.exists();
  }

  public Map getDescriptions() throws Exception {
    Map map = new HashMap();
    final String bfname = backedFileName;
    if (exists()) {
      VirtualFileFilter ff =
          new VirtualFileFilter() {
            public boolean accept(VirtualFile f) {
              String myfname = f.getName();
              return (myfname.startsWith(bfname) && descPattern.matcher(myfname).matches());
            }
          };
      VirtualFile[] files = getArchive(false).list(ff, 1);
      // System.out.println("getDescriptions - files(): " + Arrays.asList(files));
      for (int i = 0; i < files.length; i++) {
        Matcher m = descPattern.matcher(files[i].getName());
        if (m.matches()) {
          Integer ver = new Integer(m.group(1));
          String desc = str((VirtualFile) files[i], encoding);
          map.put(ver, desc);
        }
      }
    }
    // System.out.println("getDescriptions(): " + map);
    return map;
  }

  public Map getDescriptions(OxygenIntRange range) throws Exception {
    Map map = new HashMap();
    while (range.hasNext()) {
      int i = range.next();
      map.put(new Integer(i), getDescription(i));
    }
    return map;
  }

  public String getDescription(int rev) throws Exception {
    String s = null;
    if (exists()) {
      VirtualFile f =
          getArchive(false).getChild(backedFileName + "." + "description-" + rev + ".txt" + ".gz");
      s = str(f, encoding);
    }
    return s;
  }

  public InputStream getInputStream(int rev) throws Exception {
    InputStream is = null;
    if (exists()) {
      VirtualFile f =
          getArchive(false).getChild(backedFileName + "." + "version-" + rev + extension + ".gz");
      if (f.exists()) {
        is = new GZIPInputStream(f.getInputStream());
      }
    }
    return is;
  }

  public int getHeadVersion() throws Exception {
    VirtualFile archivefile = getArchive(true);
    VirtualFile f = archivefile.getChild(backedFileName + "." + "admin.txt.gz");
    InputStream fis = new GZIPInputStream(f.getInputStream());
    Properties p = new Properties();
    p.load(fis);
    CloseUtils.close(fis);
    int headver = Integer.parseInt(p.getProperty("head_version"));
    return headver;
  }

  public synchronized void addNewVersion(InputStream is, String logmsg) throws Exception {
    try {
      GZIPOutputStream fos = null;
      VirtualWritableFile archivefile = w(getArchive(true));
      VirtualWritableFile f =
          (VirtualWritableFile) archivefile.getChild(backedFileName + "." + "admin.txt.gz");
      InputStream fis = new GZIPInputStream(f.getInputStream());
      Properties p = new Properties();
      p.load(fis);
      CloseUtils.close(fis);

      int headver = Integer.parseInt(p.getProperty("head_version"));
      headver++;
      p.setProperty("head_version", String.valueOf(headver));

      f =
          (VirtualWritableFile)
              archivefile.getChild(backedFileName + "." + "version-" + headver + extension + ".gz");
      fos = new GZIPOutputStream(f.getOutputStream());
      OxygenUtils.copyStreams(is, fos, false);
      fos.finish();
      CloseUtils.close(fos);

      f =
          (VirtualWritableFile)
              archivefile.getChild(
                  backedFileName + "." + "description-" + headver + ".txt" + ".gz");
      fos = new GZIPOutputStream(f.getOutputStream());
      OutputStreamWriter fw = new OutputStreamWriter(fos, encoding);
      fw.write(logmsg);
      fw.flush();
      fos.finish();
      CloseUtils.close(fw);

      // write the admin file last - it is like our commit
      f = (VirtualWritableFile) archivefile.getChild(backedFileName + "." + "admin.txt.gz");
      fos = new GZIPOutputStream(f.getOutputStream());
      p.store(fos, null);
      fos.finish();
      CloseUtils.close(fos);
    } finally {

    }
  }

  public synchronized void deleteVersions(OxygenIntRange range) throws Exception {
    try {
      VirtualWritableFile archivefile = w(getArchive(false));

      while (range.hasNext()) {
        int headver = range.next();
        VirtualWritableFile f = null;
        f =
            (VirtualWritableFile)
                archivefile.getChild(
                    backedFileName + "." + "version-" + headver + extension + ".gz");
        if (f.exists()) {
          f.delete();
        }
        f =
            (VirtualWritableFile)
                archivefile.getChild(
                    backedFileName + "." + "description-" + headver + ".txt" + ".gz");
        if (f.exists()) {
          f.delete();
        }
      }
    } finally {
    }
  }

  public Date getDate(int rev) throws Exception {
    VirtualFile archivefile = getArchive(false);
    VirtualFile f =
        archivefile.getChild(backedFileName + "." + "description-" + rev + ".txt" + ".gz");
    return new Date(f.lastModified());
  }

  private VirtualWritableFile w(VirtualFile vf) throws Exception {
    if (!(vf instanceof VirtualWritableFile)) {
      throw new Exception("This is not a writable file");
    }
    return (VirtualWritableFile) vf;
  }

  private String str(VirtualFile f, String enc) throws Exception {
    String desc = null;
    if (f.exists()) {
      InputStreamReader fr = new InputStreamReader(new GZIPInputStream(f.getInputStream()), enc);
      desc = StringUtils.readerToString(fr);
      CloseUtils.close(fr);
    }
    return desc;
  }

  private VirtualFile getArchive(boolean createIfNeeded) throws Exception {
    VirtualFile f0 = backedFile.getParent().getChild(METADATA_DIRECTORY);
    VirtualFile archivefile = f0;
    if (createIfNeeded) {
      try {
        VirtualWritableFile f = w(f0);
        if (!f.exists()) {
          f.mkdirs();
        }
        f = (VirtualWritableFile) f.getChild(backedFileName + "." + "admin.txt.gz");
        // System.out.println("admin.txt exists: " + f.exists());
        if (!f.exists()) {
          Properties p = new Properties();
          p.setProperty("filename", backedFileName);
          p.setProperty("head_version", "0");
          p.setProperty("extension", extension);
          GZIPOutputStream fos = new GZIPOutputStream(f.getOutputStream());
          p.store(fos, null);
          fos.finish();
          CloseUtils.close(fos);
        }
      } finally {

      }
    }
    return archivefile;
  }
}
