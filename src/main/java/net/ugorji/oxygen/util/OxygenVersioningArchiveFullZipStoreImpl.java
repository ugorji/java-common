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
import java.io.FileFilter;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.schlichtherle.io.File;
import de.schlichtherle.io.FileInputStream;
import de.schlichtherle.io.FileOutputStream;
import de.schlichtherle.io.FileReader;
import de.schlichtherle.io.FileWriter;
*/

/**
 * This file is like this: admin.txt (properties file containing filename, head version, extension)
 * description-n.txt version-n.$extension
 *
 * <p>Currently, this class is not being used. Managing a zip archive is kinda heavyweight - so we
 * just use the flat file. Also, the truezip does things with tmp files and a Shutdown Handler which
 * I'm not a fan of.
 *
 * <p>Making this abstract for now, and commenting everything out. Apr 16, 2007. Ugorji.
 */
public abstract class OxygenVersioningArchiveFullZipStoreImpl implements OxygenVersioningArchive {}

/*
  private static final Pattern descPattern = Pattern.compile("description\\-([0-9]+)\\.txt");
  public static final String VER_FILE_SUFFIX = ".oxyver.zip";

  private Pattern verPattern = null;
  private java.io.File backedFile;
  private String backedFileName;
  private String extension;
  private String encoding;

  public OxygenVersioningArchiveFullZipStoreImpl(java.io.File backedFile0, String enc) throws Exception {
    backedFile = backedFile0;
    backedFileName = backedFile.getName();
    extension = StringUtils.getFileNameExtension(backedFileName, ".dat");
    verPattern = Pattern.compile("version\\-([0-9]+)\\" + extension);
    encoding = enc;
    //System.out.println(" archivefile: " + archivefile + " backedFile: " + backedFile + " extension: " + extension);
  }

  public boolean exists() throws Exception {
    return getArchive(false).exists();
  }

  public Map getDescriptions() throws Exception {
    Map map = new HashMap();
    File f = getArchive(false);
    if(f.exists()) {
      FileFilter ff = new FileFilter() {
          public boolean accept(java.io.File f) {
            return descPattern.matcher(f.getName()).matches();
          }
        };
      java.io.File[] files = f.listFiles(ff);
      //System.out.println("getDescriptions - files(): " + Arrays.asList(files));
      for(int i = 0; i < files.length; i++) {
        Matcher m = descPattern.matcher(files[i].getName());
        if(m.matches()) {
          Integer ver = new Integer(m.group(1));
          String desc = str((File)files[i]);
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
    File f = getArchive(false);
    if(f.exists()) {
      f = new File(f, "description-" + rev + ".txt");
      s = str(f);
    }
    return s;
  }

  public InputStream getInputStream(int rev)  throws Exception{
    InputStream is = null;
    File f = getArchive(false);
    if(f.exists()) {
      f = new File(f, "version-" + rev + extension);
      if(f.exists()) {
        is = new FileInputStream(f);
      }
    }
    return is;
  }

  public int getHeadVersion() throws Exception {
    File archivefile = getArchive(true);
    File f = new File(archivefile, "admin.txt");
    InputStream fis = new FileInputStream(f);
    Properties p = new Properties();
    p.load(fis);
    CloseUtils.close(fis);

    int headver = Integer.parseInt(p.getProperty("head_version"));
    return headver;
  }

  public synchronized void addNewVersion(java.io.InputStream is, String logmsg) throws Exception {
    try {
      FileOutputStream fos = null;
      File archivefile = getArchive(true);
      File f = new File(archivefile, "admin.txt");
      InputStream fis = new FileInputStream(f);
      Properties p = new Properties();
      p.load(fis);
      CloseUtils.close(fis);

      int headver = Integer.parseInt(p.getProperty("head_version"));
      headver++;
      p.setProperty("head_version", String.valueOf(headver));

      f = new File(archivefile, "version-" + headver + extension);
      fos = new FileOutputStream(f);
      OxygenUtils.copyStreams(is, fos, false);
      CloseUtils.close(fos);
      f = new File(archivefile, "description-" + headver + ".txt");
      FileWriter fw = new FileWriter(f);
      fw.write(logmsg);
      CloseUtils.close(fw);

      f = new File(archivefile, "admin.txt");
      fos = new FileOutputStream(f);
      p.store(fos, null);
      CloseUtils.close(fos);

    } finally {
      File.update();
    }
  }

  public synchronized void deleteVersions(OxygenIntRange range) throws Exception {
    try {
      File archivefile = getArchive(false);
      while(range.hasNext()) {
        int headver = range.next();
        File f = null;
        f = new File(archivefile, "version-" + headver + extension);
        if(f.exists()) {
          f.delete();
        }
        f = new File(archivefile, "description-" + headver + extension);
        if(f.exists()) {
          f.delete();
        }
      }
    } finally {
      File.update();
    }
  }


  private String str(File f) throws Exception {
    String desc = null;
    if(f.exists()) {
      FileReader fr = new FileReader(f);
      desc = StringUtils.readerToString(fr);
      CloseUtils.close(fr);
    }
    return desc;
  }

  private File getArchive(boolean createIfNeeded) throws Exception {
    File f = new File(backedFile.getParentFile());
    f = new File(f, METADATA_DIRECTORY);
    f = new File(f, backedFileName + VER_FILE_SUFFIX);
    File archivefile = f;
    if(createIfNeeded) {
      try {
        if(!f.exists()) {
          f.mkdirs();
        }
        f = new File(f, "admin.txt");
        //System.out.println("admin.txt exists: " + f.exists());
        if(!f.exists()) {
          Properties p = new Properties();
          p.setProperty("filename", backedFileName);
          p.setProperty("head_version", "0");
          p.setProperty("extension", extension);
          FileOutputStream fos = new FileOutputStream(f);
          p.store(fos, null);
          CloseUtils.close(fos);
        }
      } finally {
        File.update();
      }
    }
    return archivefile;
  }

}

*/
