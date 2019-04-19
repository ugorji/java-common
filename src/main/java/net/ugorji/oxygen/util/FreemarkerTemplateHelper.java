/* <<< COPYRIGHT START >>>
 * Copyright 2006-Present OxygenSoftwareLibrary.com
 * Licensed under the GNU Lesser General Public License.
 * http://www.gnu.org/licenses/lgpl.html
 *
 * @author: Ugorji Nwoke
 * <<< COPYRIGHT END >>>
 */

package net.ugorji.oxygen.util;

import freemarker.cache.MultiTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.cache.URLTemplateLoader;
import freemarker.core.Environment;
import freemarker.ext.beans.BeansWrapper;
import freemarker.template.*;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class FreemarkerTemplateHelper implements Closeable {

  private static ErrorHandler ERROR_HANDLER_DEF = new ErrorHandlerDefault();

  private Configuration cfg;
  private Map defaultStaticModels;

  public static interface ErrorHandler {
    void handleError(FreemarkerTemplateHelper fth, Exception thr, Writer w) throws Exception;
  }

  protected FreemarkerTemplateHelper() {}

  public FreemarkerTemplateHelper(String[] templates0, Map defaultStaticModels0) {
    init(templates0, defaultStaticModels0);
  }

  protected void init(String[] templates, Map defaultStaticModels0) {
    defaultStaticModels = defaultStaticModels0;
    cfg = new Configuration();

    TemplateLoader[] templateLoaders = new TemplateLoader[templates.length];
    for (int i = 0; i < templates.length; i++) {
      templateLoaders[i] = new OxyTemplateLoader(templates[i]);
    }

    cfg.setTemplateLoader(new MultiTemplateLoader(templateLoaders));
    cfg.setObjectWrapper(new DefaultObjectWrapper());
    cfg.setTemplateExceptionHandler(this.new OxyTemplateExceptionHandler());
  }

  public void write(String templatefile, Map model, Writer writer) throws Exception {
    write(templatefile, model, null, writer);
  }

  public void write(String templatefile, Map model, Map staticModelStrings, Writer writer)
      throws Exception {
    Map model2 = new HashMap(model);
    Map[] sm2 = new Map[] {defaultStaticModels, staticModelStrings};
    for (int i = 0; i < sm2.length; i++) {
      if (sm2[i] == null || sm2[i].size() == 0) continue;
      TemplateHashModel thm = BeansWrapper.getDefaultInstance().getStaticModels();
      for (Iterator itr = sm2[i].entrySet().iterator(); itr.hasNext(); ) {
        Map.Entry entry = (Map.Entry) itr.next();
        String key = (String) entry.getKey();
        String val = (String) entry.getValue();
        model2.put(key, thm.get(val));
      }
    }
    rawWrite(templatefile, model2, writer);
  }

  public void rawWrite(String templatefile, Map model, Writer writer) throws Exception {
    Template t = cfg.getTemplate(templatefile);
    t.process(model, writer);
  }

  public void rawWrite(Reader r, Map model, Writer writer) throws Exception {
    Template t = new Template("blah-XYZUgorji-" + OxygenUtils.getUniqueIntID(1000000), r, cfg);
    t.process(model, writer);
  }

  public void close() {}

  public static ErrorHandler getErrorHandler() {
    ErrorHandler e = (ErrorHandler) OxyLocal.get(ErrorHandler.class);
    if (e != null) return e;
    return ERROR_HANDLER_DEF;
  }

  public static void setErrorHandler(ErrorHandler e) {
    OxyLocal.set(ErrorHandler.class, e);
  }

  private static class ErrorHandlerDefault implements ErrorHandler {
    public void handleError(FreemarkerTemplateHelper fth, Exception thr, Writer w)
        throws Exception {
      if (w instanceof PrintWriter) {
        thr.printStackTrace((PrintWriter) w);
      } else {
        thr.printStackTrace(new PrintWriter(w, true));
      }
    }
  }

  private class OxyTemplateExceptionHandler implements TemplateExceptionHandler {
    public void handleTemplateException(TemplateException te, Environment env, Writer writer)
        throws TemplateException {
      try {
        Exception thr = te.getCauseException();
        thr = ((thr == null) ? te : thr);
        getErrorHandler().handleError(FreemarkerTemplateHelper.this, thr, writer);
      } catch (TemplateException te2) {
        throw te2;
      } catch (Exception e2) {
        throw new TemplateException(e2, env);
      }
    }
  }

  private static class OxyTemplateLoader extends URLTemplateLoader {
    private String basepath;

    private OxyTemplateLoader(String basepath0) {
      basepath = basepath0.replace('\\', '/');
      basepath = StringUtils.trim(basepath, '/');
    }

    protected URL getURL(String name) {
      return Thread.currentThread().getContextClassLoader().getResource(basepath + '/' + name);
    }
  }
}
