/* <<< COPYRIGHT START >>>
 * Copyright 2006-Present OxygenSoftwareLibrary.com
 * Licensed under the GNU Lesser General Public License.
 * http://www.gnu.org/licenses/lgpl.html
 *
 * @author: Ugorji Nwoke
 * <<< COPYRIGHT END >>>
 */

package net.ugorji.oxygen.util;

import com.jhlabs.image.RippleFilter;
import com.jhlabs.image.TransformFilter;
import com.jhlabs.image.WaterFilter;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.font.LineMetrics;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Line2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Random;
import java.util.StringTokenizer;
import javax.imageio.ImageIO;

public class CaptchaTextImageProducer {

  private Font[] fonts = null;
  private char[] wordchars = null;

  private Random rand = new Random();
  private int width = 200;
  private int height = 50;
  private int wordlength = 5;

  public CaptchaTextImageProducer() {
    init("Arial,Dialog", "A-Z");
  }

  public CaptchaTextImageProducer(String fontnames, String charRanges) {
    init(fontnames, charRanges);
  }

  public void init(String fontnames, String charRanges) {
    StringTokenizer stz = null;
    Collection col = null;

    col = new ArrayList();
    stz = new StringTokenizer(fontnames, ", ");
    while (stz.hasMoreTokens()) {
      col.add(new Font(stz.nextToken(), Font.BOLD, 40));
    }
    // System.out.println("List: " + col);
    fonts = (Font[]) col.toArray(new Font[0]);

    col = new LinkedHashSet();
    stz = new StringTokenizer(charRanges, ", ");
    while (stz.hasMoreTokens()) {
      String s = stz.nextToken();
      int idx = s.indexOf('-');
      int i0 = -1;
      int i1 = -1;
      if (idx == -1) {
        i0 = (int) (s.charAt(0));
        i1 = i0;
      } else {
        i0 = (int) (s.substring(0, idx).charAt(0));
        i1 = (int) (s.substring(idx + 1).charAt(0));
      }
      for (int i = i0; i <= i1; i++) {
        col.add(new Character((char) i));
      }
    }
    wordchars = new char[col.size()];
    int i = 0;
    for (Iterator itr = col.iterator(); itr.hasNext(); ) {
      wordchars[i++] = ((Character) itr.next()).charValue();
    }
    // System.out.println("Set: " + col);
  }

  public String getNextRandomWord() {
    StringBuffer buf = new StringBuffer();
    for (int i = 0; i < wordlength; i++) {
      buf.append(wordchars[rand.nextInt(wordchars.length)]);
    }
    return buf.toString();
  }

  public BufferedImage getCaptchaImage(String word) {
    BufferedImage image = getPlainTextImage(word);
    image = getDistortedImage(image);
    image = addBackground(image);
    image = drawBox(image);
    image = makeNoise(image, .1f, .1f, .25f, .25f);
    image = makeNoise(image, .1f, .25f, .5f, .9f);
    return image;
  }

  public void writeImageAsJPEG(BufferedImage image, OutputStream os) throws IOException {
    ImageIO.write(image, "jpeg", os);
    // JPEGImageEncoder jpegEncoder = JPEGCodec.createJPEGEncoder(os);
    // jpegEncoder.encode(image);
    os.flush();
  }

  public BufferedImage getPlainTextImage(String word) {
    GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();

    GraphicsDevice gd = ge.getDefaultScreenDevice();
    GraphicsConfiguration gc = gd.getDefaultConfiguration();
    BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

    Graphics2D g2D = image.createGraphics();
    g2D.setColor(Color.black);

    RenderingHints hints =
        new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    hints.add(
        new RenderingHints(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY));

    g2D.setRenderingHints(hints);

    char[] wc = word.toCharArray();
    Color fontColor = Color.black;
    g2D.setColor(fontColor);
    FontRenderContext frc = g2D.getFontRenderContext();
    int startPosX = 25;
    for (int i = 0; i < wc.length; i++) {
      char[] itchar = new char[] {wc[i]};
      // g2D.setColor(Color.black);
      int choiceFont = rand.nextInt(fonts.length);
      Font itFont = fonts[choiceFont];
      g2D.setFont(itFont);
      LineMetrics lmet = itFont.getLineMetrics(itchar, 0, itchar.length, frc);
      GlyphVector gv = itFont.createGlyphVector(frc, itchar);
      double charWitdth = gv.getVisualBounds().getWidth();

      g2D.drawChars(itchar, 0, itchar.length, startPosX, 35);
      startPosX = startPosX + (int) charWitdth + 2;
    } // for next char array.

    return image;
  }

  public BufferedImage getDistortedImage(BufferedImage image) {
    BufferedImage imageDistorted =
        new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);

    Graphics2D graph = (Graphics2D) imageDistorted.getGraphics();

    // create filter ripple
    RippleFilter filter = new RippleFilter();
    filter.setWaveType(RippleFilter.SINGLEFRAME);
    filter.setXAmplitude(2.6f);
    filter.setYAmplitude(1.7f);
    filter.setXWavelength(15);
    filter.setYWavelength(5);
    filter.setEdgeAction(TransformFilter.RANDOMPIXELORDER);

    // create water filter
    WaterFilter water = new WaterFilter();
    water.setAmplitude(4);
    water.setAntialias(true);
    water.setPhase(15);
    water.setWavelength(70);

    // apply filter water
    FilteredImageSource filtered = new FilteredImageSource(image.getSource(), water);
    Image img = Toolkit.getDefaultToolkit().createImage(filtered);

    // apply filter ripple
    filtered = new FilteredImageSource(img.getSource(), filter);
    img = Toolkit.getDefaultToolkit().createImage(filtered);

    graph.drawImage(img, 0, 0, null, null);

    graph.dispose();

    return imageDistorted;
  }

  public BufferedImage addBackground(BufferedImage image) {
    int width = image.getWidth();
    int height = image.getHeight();

    Color from = Color.lightGray;
    Color to = Color.white;

    // create an opac image
    BufferedImage resultImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

    Graphics2D graph = (Graphics2D) resultImage.getGraphics();
    RenderingHints hints =
        new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

    hints.add(
        new RenderingHints(
            RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY));
    hints.add(
        new RenderingHints(
            RenderingHints.KEY_ALPHA_INTERPOLATION,
            RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY));

    hints.add(
        new RenderingHints(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY));

    graph.setRenderingHints(hints);

    // create the gradient color
    GradientPaint ytow = new GradientPaint(0, 0, from, width, height, to);

    graph.setPaint(ytow);
    // draw gradient color
    graph.fill(new Rectangle2D.Double(0, 0, width, height));

    // draw the transparent image over the background
    graph.drawImage(image, 0, 0, null);

    return resultImage;
  }

  public BufferedImage drawBox(BufferedImage image) {
    int w = width;
    int h = height;
    Graphics2D graphics = image.createGraphics();
    graphics.setColor(Color.black);

    BasicStroke stroke = new BasicStroke(2.0f);
    graphics.setStroke(stroke);

    Line2D d2 = new Line2D.Double((double) 0, (double) 0, (double) 0, (double) w);
    graphics.draw(d2);

    Line2D d3 = new Line2D.Double((double) 0, (double) 0, (double) w, (double) 0);
    graphics.draw(d3);

    d3 = new Line2D.Double((double) 0, (double) h - 1, (double) w, (double) h - 1);
    graphics.draw(d3);

    d3 = new Line2D.Double((double) w - 1, (double) h - 1, (double) w - 1, (double) 0);

    graphics.draw(d3);
    return image;
  }

  public BufferedImage makeNoise(
      BufferedImage image, float factorOne, float factorTwo, float factorThree, float factorFour) {
    int width = image.getWidth();
    int height = image.getHeight();

    // the points where the line changes the stroke and direction
    Point2D[] pts = null;

    // the curve from where the points are taken
    CubicCurve2D cc =
        new CubicCurve2D.Float(
            width * factorOne, height * rand.nextFloat(),
            width * factorTwo, height * rand.nextFloat(),
            width * factorThree, height * rand.nextFloat(),
            width * factorFour, height * rand.nextFloat());

    // creates an iterator to define the boundary of the flattened curve
    PathIterator pi = cc.getPathIterator(null, 2);
    Point2D tmp[] = new Point2D[200];
    int i = 0;

    // while pi is iterating the curve, adds points to tmp array
    while (!pi.isDone()) {
      float[] coords = new float[6];
      switch (pi.currentSegment(coords)) {
        case PathIterator.SEG_MOVETO:
        case PathIterator.SEG_LINETO:
          tmp[i] = new Point2D.Float(coords[0], coords[1]);
      }
      i++;
      pi.next();
    }

    pts = new Point2D[i];
    // copies points from tmp to pts
    System.arraycopy(tmp, 0, pts, 0, i);

    Graphics2D graph = (Graphics2D) image.getGraphics();
    graph.setRenderingHints(
        new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON));

    Color mycol = Color.black;

    graph.setColor(mycol);

    // for the maximum 3 point change the stroke and direction
    for (i = 0; i < pts.length - 1; i++) {
      if (i < 3) {
        graph.setStroke(new BasicStroke(0.9f * (4 - i)));
      }
      graph.drawLine(
          (int) pts[i].getX(), (int) pts[i].getY(),
          (int) pts[i + 1].getX(), (int) pts[i + 1].getY());
    }

    graph.dispose();
    return image;
  }

  // public static void main(String[] args) throws Exception {
  //  System.out.println((int)'A' + "-" + (int)'Z');
  //  System.out.println(Character.getNumericValue('A') + "-" + Character.getNumericValue('Z'));
  //  System.out.println(Character.getNumericValue('a') + "-" + Character.getNumericValue('z'));
  //  new CaptchaTextImageProducer("Arial, Dialog", "A-Z");
  // }
}
