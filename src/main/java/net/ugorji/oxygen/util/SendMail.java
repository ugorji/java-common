/* <<< COPYRIGHT START >>>
 * Copyright 2006-Present OxygenSoftwareLibrary.com
 * Licensed under the GNU Lesser General Public License.
 * http://www.gnu.org/licenses/lgpl.html
 *
 * @author: Ugorji Nwoke
 * <<< COPYRIGHT END >>>
 */

package net.ugorji.oxygen.util;

import java.io.File;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.Writer;
import java.net.URL;
import java.util.Arrays;
import java.util.Date;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * Utility class for sending mail
 *
 * @author ugorji
 */
public class SendMail {
  public static void main(String[] args) throws Exception {
    boolean debug = false;
    EmailInfoHolder email = new EmailInfoHolder();
    for (int i = 0; i < args.length; i++) {
      if (args[i].equals("-url")) {
        email.text = OxygenUtils.getURLContents(new URL(args[++i]));
      } else if (args[i].equals("-file")) {
        email.text = OxygenUtils.getFileContents(new File(args[++i]));
      } else if (args[i].equals("-html")) {
        email.headers.put("Content-Type", "text/html; charset=\"us-ascii\"");
      } else if (args[i].equals("-recipients")) {
        email.recipients = args[++i];
      } else if (args[i].equals("-sender")) {
        email.sender = args[++i];
      } else if (args[i].equals("-subject")) {
        email.subject = args[++i];
      } else if (args[i].equals("-mailprop")) {
        email.properties.put(args[++i], args[++i]);
      } else if (args[i].equals("-debug")) {
        debug = "true".equals(args[++i]);
      }
    }
    sendMail(email, debug);
  }

  public static void sendMail(EmailInfoHolder email) throws Exception {
    sendMail(email, false);
  }

  static void sendMail(EmailInfoHolder email, boolean debug) throws Exception {
    if (debug) {
      System.out.println(">>>>> SendMail.sendMail: debug=true");
      email.write(new PrintWriter(System.out));
      System.out.println(">>>>> SendMail.sendMail: done");
      return;
    }

    String smtpHost = email.properties.getProperty("mail.smtp.host");
    Properties props2 = System.getProperties();
    props2.put("mail.smtp.host", smtpHost);
    // Session session = Session.getDefaultInstance(props2, null);
    Session session = Session.getInstance(props2, null);

    Message mailMsg = new MimeMessage(session);

    InternetAddress fromAddr = new InternetAddress(email.sender);
    mailMsg.setFrom(fromAddr);
    mailMsg.setReplyTo(new InternetAddress[] {fromAddr});

    InternetAddress[] recipients = null;
    if (email.recipientsArray != null && email.recipientsArray.length > 0) {
      recipients = new InternetAddress[email.recipientsArray.length];
      for (int i = 0; i < email.recipientsArray.length; i++) {
        recipients[i] = new InternetAddress(email.recipientsArray[i]);
      }
    } else {
      recipients = InternetAddress.parse(email.recipients);
    }
    mailMsg.setRecipients(Message.RecipientType.TO, recipients);

    mailMsg.setSubject(email.subject);

    mailMsg.setText(email.text);

    if (email.headers != null) {
      int headersSize = email.headers.size();
      for (int i = 0; i < headersSize; i++) {
        mailMsg.setHeader((String) email.headers.getKey(i), (String) email.headers.getValue(i));
      }
    }

    Date sentDate = email.sentDate;
    if (sentDate == null) sentDate = new Date();
    mailMsg.setSentDate(sentDate);

    Transport transport = session.getTransport("smtp");
    transport.connect(smtpHost, null, null);
    transport.sendMessage(mailMsg, recipients);
  }

  public static class EmailInfoHolder implements Serializable {
    public String subject = "";

    public String text = "";

    public ListMap headers = new ListMap();

    public Date sentDate = new Date();

    public String sender;

    public String[] recipientsArray;

    public String recipients;

    public Properties properties = new Properties();

    public void write(Writer w) {
      PrintWriter pw = new PrintWriter(w);
      pw.println("subject: " + subject);
      pw.println("sender: " + sender);
      pw.println("recipients: " + recipients);
      pw.println(
          "recipientsArray: " + (recipientsArray == null ? null : Arrays.asList(recipientsArray)));
      pw.println("properties: " + properties);
      pw.println("sentDate: " + sentDate);
      pw.println("headers: " + headers);
      pw.println("text: " + text);
      pw.flush();
    }

    public void write(OutputStream w) {
      write(new OutputStreamWriter(w));
    }

    public String toString() {
      return "Email: Subject: " + subject + ". To: " + recipients + ". From: " + sender;
    }
  }
}
