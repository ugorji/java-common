/* <<< COPYRIGHT START >>>
 * Copyright 2006-Present OxygenSoftwareLibrary.com
 * Licensed under the GNU Lesser General Public License.
 * http://www.gnu.org/licenses/lgpl.html
 *
 * @author: Ugorji Nwoke
 * <<< COPYRIGHT END >>>
 */

package net.ugorji.oxygen.util;

import java.io.Serializable;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.suigeneris.jrcs.diff.Diff;
import org.suigeneris.jrcs.diff.Revision;
import org.suigeneris.jrcs.diff.delta.Chunk;
import org.suigeneris.jrcs.diff.delta.Delta;

/**
 * Encapsulates the changes between 2 versions of a page
 *
 * @author ugorji
 */
public class OxygenRevision implements Serializable {
  private static FreemarkerTemplateHelper fmth;

  static {
    Map staticModels = new HashMap();
    staticModels.put("StringUtils", "net.ugorji.oxygen.util.StringUtils");
    fmth = new FreemarkerTemplateHelper(new String[] {"/net/ugorji/oxygen/util"}, staticModels);
  }

  private List origchunks = new ArrayList();
  private List revchunks = new ArrayList();
  private List origIndxs = new ArrayList();
  private List revIndxs = new ArrayList();
  private int numchunks = 0;

  private int r1;
  private int r2;

  private I18n i18n;

  OxygenRevision() {}

  public OxygenRevision(int _r1, int _r2, I18n i18n) {
    setOriginalVersion(_r1);
    setRevisedVersion(_r2);
    setI18n(i18n);
  }

  public void setI18n(I18n i18n) {
    this.i18n = i18n;
  }

  public void setOriginalVersion(int _r1) {
    r1 = _r1;
  }

  public void setRevisedVersion(int _r2) {
    r2 = _r2;
  }

  public void addChange(int origIndx, Object[] orig, int revIndx, Object[] rev) {
    origchunks.add(orig);
    revchunks.add(rev);
    origIndxs.add(new Integer(origIndx));
    revIndxs.add(new Integer(revIndx));
    numchunks++;
  }

  public int getOriginalVersion() {
    return r1;
  }

  public int getRevisedVersion() {
    return r2;
  }

  public int getSize() {
    return numchunks;
  }

  public StringBuffer getOriginal(int indx, StringBuffer buf, String prefix, String postfix) {
    if (buf == null) {
      buf = new StringBuffer();
    }
    Object[] origchunk = (Object[]) origchunks.get(indx);
    for (int i = 0; i < origchunk.length; i++) {
      buf.append(prefix).append(origchunk[i]).append(postfix);
    }
    return buf;
  }

  public StringBuffer getRevised(int indx, StringBuffer buf, String prefix, String postfix) {
    if (buf == null) {
      buf = new StringBuffer();
    }
    Object[] revchunk = (Object[]) revchunks.get(indx);
    for (int i = 0; i < revchunk.length; i++) {
      buf.append(prefix).append(revchunk[i]).append(postfix);
    }
    return buf;
  }

  public String getChangeSummary(int indx) {
    // I18n i18n = WebLocal.getI18n();
    Object[] revchunk = (Object[]) revchunks.get(indx);
    Object[] origchunk = (Object[]) origchunks.get(indx);
    int origIndx = ((Integer) origIndxs.get(indx)).intValue();
    int revIndx = ((Integer) revIndxs.get(indx)).intValue();
    String s = null;
    if (origchunk.length == 0) {
      s =
          i18n.str(
              "common.revision.lines_added",
              new String[] {s(revIndx), s(revIndx + revchunk.length - 1)});
    } else if (revchunk.length == 0) {
      s =
          i18n.str(
              "common.revision.lines_removed",
              new String[] {s(origIndx), s(origIndx + origchunk.length - 1)});
    } else {
      s =
          i18n.str(
              "common.revision.lines_replaced",
              new String[] {
                s(origIndx),
                s(origIndx + origchunk.length - 1),
                s(revIndx),
                s(revIndx + revchunk.length - 1)
              });
    }
    return s;
  }

  public String toString() {
    return toTextString();
  }

  public String toTextString() {
    // I18n i18n = WebLocal.getI18n();
    String lsep = StringUtils.LINE_SEP;
    StringBuffer buf = new StringBuffer();
    buf.append(i18n.str("common.revision.before", s(getOriginalVersion())))
        .append(lsep)
        .append(i18n.str("common.revision.after", s(getRevisedVersion())))
        .append(lsep)
        .append(lsep);
    int deltasize = getSize();
    for (int i = 0; i < deltasize; i++) {
      String summ = getChangeSummary(i);
      StringBuffer buf0 = new StringBuffer();
      buf0 = getOriginal(i, buf0, " - ", lsep);
      StringBuffer buf1 = new StringBuffer();
      buf1 = getRevised(i, buf1, " + ", lsep);

      buf.append(summ);
      buf.append(lsep);
      buf.append(buf0);
      buf.append(lsep);
      buf.append(buf1);
    }
    buf.append(lsep);
    return buf.toString();
  }

  public String toHTMLString() throws Exception {
    StringWriter stw = new StringWriter();
    writeHTML(stw);
    return stw.toString();
  }

  public void writeHTML(Writer stw) throws Exception {
    Map tmplctx = new HashMap();
    tmplctx.put("wrev", this);
    tmplctx.put("lines", i18n.str("common.revision.lines"));

    fmth.write("oxygenrevision.html", tmplctx, stw);
  }

  /**
   * Diff 2 strings, returning a WikiRevision object
   *
   * @param page1
   * @param page2
   * @return
   * @throws Exception
   */
  public static OxygenRevision getDiff(String page1, String page2, I18n i18n) throws Exception {
    String[] arr1 = Diff.stringToArray(page1);
    String[] arr2 = Diff.stringToArray(page2);
    return getDiff(arr1, arr2, i18n);
  }

  /**
   * Gets the diff. Basically, each arg is really an array of strings, which represents the lines
   *
   * @param arr1
   * @param arr2
   * @return
   * @throws Exception
   */
  public static OxygenRevision getDiff(Object[] arr1, Object[] arr2, I18n i18n) throws Exception {
    Revision rev = Diff.diff(arr1, arr2);
    int deltasize = rev.size();

    OxygenRevision wrev = new OxygenRevision();
    wrev.setI18n(i18n);
    for (int i = 0; i < deltasize; i++) {
      Delta delta = rev.getDelta(i);
      Chunk c0 = delta.getOriginal();
      Chunk c1 = delta.getRevised();
      String[] coArr = (String[]) c0.chunk().toArray(new String[0]);
      String[] c1Arr = (String[]) c1.chunk().toArray(new String[0]);
      wrev.addChange(c0.anchor(), coArr, c1.anchor(), c1Arr);
    }
    return wrev;
  }

  private static String s(int i) {
    return String.valueOf(i);
  }
}
