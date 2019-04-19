/* <<< COPYRIGHT START >>>
 * Copyright 2006-Present OxygenSoftwareLibrary.com
 * Licensed under the GNU Lesser General Public License.
 * http://www.gnu.org/licenses/lgpl.html
 *
 * @author: Ugorji Nwoke
 * <<< COPYRIGHT END >>>
 */

package net.ugorji.oxygen.util;

/*
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
*/

/**
 * This dir contains file like this: $fileName.admin.txt (properties file containing filename, head
 * version, extension) $fileName.description-n.txt $fileName.version-n.$extension
 *
 * <p>Currently, this class is not being used. The OxygenVersioningArchiveFullVirtualFileImpl does
 * everything this class does, and is generic (can do it for zips, jars, or flat files/directories)
 *
 * <p>Making this abstract for now, and commenting everything out. Apr 16, 2007. Ugorji.
 */
public abstract class OxygenVersioningArchiveFullDirStoreImpl implements OxygenVersioningArchive {}

/*
  private static final Pattern descPattern = Pattern.compile(".*?\\.description\\-([0-9]+)\\.txt\\.gz");

  private Pattern verPattern;
  private File backedFile;
  private String backedFileName;
  private String extension;
  private String encoding;

  public OxygenVersioningArchiveFullDirStoreImpl(File backedFile0, String enc) throws Exception {
    backedFile = backedFile0;
    backedFileName = backedFile.getName();
    extension = StringUtils.getFileNameExtension(backedFileName, ".dat");
    verPattern = Pattern.compile(".*?\\.version\\-([0-9]+)\\" + extension + "\\.gz");
    encoding = enc;
    //System.out.println(" archivefile: " + archivefile + " backedFile: " + backedFile + " extension: " + extension);
  }

  public boolean exists() throws Exception {
    File f = new File(getArchive(false), backedFileName + "." + "admin.txt.gz");
    return f.exists();
  }

  public Map getDescriptions() throws Exception {
    Map map = new HashMap();
    final String bfname = backedFileName;
    if(exists()) {
      FileFilter ff = new FileFilter() {
          public boolean accept(File f) {
            String myfname = f.getName();
            return (myfname.startsWith(bfname) && descPattern.matcher(myfname).matches());
          }
        };
      File[] files = getArchive(false).listFiles(ff);
      //System.out.println("getDescriptions - files(): " + Arrays.asList(files));
      for(int i = 0; i < files.length; i++) {
        Matcher m = descPattern.matcher(files[i].getName());
        if(m.matches()) {
          Integer ver = new Integer(m.group(1));
          String desc = str((File)files[i], encoding);
          map.put(ver, desc);
        }
      }
    }
    //System.out.println("getDescriptions(): " + map);
    return map;
  }

  public Map getDescriptions(OxygenIntRange range) throws Exception {
    Map map = new HashMap();
    while(range.hasNext()) {
      int i = range.next();
      map.put(new Integer(i), getDescription(i));
    }
    return map;
  }

  public String getDescription(int rev)  throws Exception{
    String s = null;
    if(exists()) {
      File f = new File(getArchive(false), backedFileName + "." + "description-" + rev + ".txt" + ".gz");
      s = str(f, encoding);
    }
    return s;
  }

  public InputStream getInputStream(int rev)  throws Exception{
    InputStream is = null;
    if(exists()) {
      File f = new File(getArchive(false), backedFileName + "." + "version-" + rev + extension + ".gz");
      if(f.exists()) {
        is = new GZIPInputStream(new FileInputStream(f));
      }
    }
    return is;
  }

  public int getHeadVersion() throws Exception {
    File archivefile = getArchive(true);
    File f = new File(archivefile, backedFileName + "." + "admin.txt.gz");
    InputStream fis = new GZIPInputStream(new FileInputStream(f));
    Properties p = new Properties();
    p.load(fis);
    CloseUtils.close(fis);
    int headver =  Integer.parseInt(p.getProperty("head_version"));
    return headver;
  }

  public synchronized void addNewVersion(InputStream is, String logmsg) throws Exception {
    try {
      GZIPOutputStream fos = null;
      File archivefile = getArchive(true);
      File f = new File(archivefile, backedFileName + "." + "admin.txt.gz");
      InputStream fis = new GZIPInputStream(new FileInputStream(f));
      Properties p = new Properties();
      p.load(fis);
      CloseUtils.close(fis);

      int headver = Integer.parseInt(p.getProperty("head_version"));
      headver++;
      p.setProperty("head_version", String.valueOf(headver));

      f = new File(archivefile, backedFileName + "." + "version-" + headver + extension + ".gz");
      fos = new GZIPOutputStream(new FileOutputStream(f));
      OxygenUtils.copyStreams(is, fos, false);
      fos.finish();
      CloseUtils.close(fos);

      f = new File(archivefile, backedFileName + "." + "description-" + headver + ".txt" + ".gz");
      fos = new GZIPOutputStream(new FileOutputStream(f));
      OutputStreamWriter fw = new OutputStreamWriter(fos, encoding);
      fw.write(logmsg);
      fw.flush();
      fos.finish();
      CloseUtils.close(fw);

      //write the admin file last - it is like our commit
      f = new File(archivefile, backedFileName + "." + "admin.txt.gz");
      fos = new GZIPOutputStream(new FileOutputStream(f));
      p.store(fos, null);
      fos.finish();
      CloseUtils.close(fos);
    } finally {

    }
  }

  public synchronized void deleteVersions(OxygenIntRange range) throws Exception {
    try {
      File archivefile = getArchive(false);
      while(range.hasNext()) {
        int headver = range.next();
        File f = null;
        f = new File(archivefile, backedFileName + "." + "version-" + headver + extension + ".gz");
        if(f.exists()) {
          f.delete();
        }
        f = new File(archivefile, backedFileName + "." + "description-" + headver + ".txt" + ".gz");
        if(f.exists()) {
          f.delete();
        }
      }
    } finally {
    }
  }

  private String str(File f, String enc) throws Exception {
    String desc = null;
    if(f.exists()) {
      InputStreamReader fr = new InputStreamReader(new GZIPInputStream(new FileInputStream(f)), enc);
      desc = StringUtils.readerToString(fr);
      CloseUtils.close(fr);
    }
    return desc;
  }

  private File getArchive(boolean createIfNeeded) throws Exception {
    File f = backedFile.getParentFile();
    f = new File(f, METADATA_DIRECTORY);
    File archivefile = f;
    if(createIfNeeded) {
      try {
        if(!f.exists()) {
          f.mkdirs();
        }
        f = new File(f, backedFileName + "." + "admin.txt.gz");
        //System.out.println("admin.txt exists: " + f.exists());
        if(!f.exists()) {
          Properties p = new Properties();
          p.setProperty("filename", backedFileName);
          p.setProperty("head_version", "0");
          p.setProperty("extension", extension);
          GZIPOutputStream fos = new GZIPOutputStream(new FileOutputStream(f));
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

*/
