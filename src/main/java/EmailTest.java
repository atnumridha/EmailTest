import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import javax.mail.*;
import javax.mail.search.*;

public class EmailTest {

    public static void main(String[] args) {
        Properties props = new Properties();
        props.setProperty("mail.store.protocol", "imaps");

        try {
            Session session = Session.getInstance(props, null);
            Store store = session.getStore();
            store.connect("imap.gmail.com", "anupmridha27@gmail.com", "yehjgqtwqyjtxpcl");
            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_WRITE);
            Message messages[] = inbox.search(new FlagTerm(new Flags(
                    Flags.Flag.SEEN), false));

            for (int i = 0; i < messages.length; i++) {
                Message message = messages[i];
                String subject = message.getSubject();
                System.out.println("Found message #" + i + ": " + subject);

                // mark as read
                inbox.setFlags(new Message[]{message}, new Flags(Flags.Flag.SEEN), true);

                if (subject.toUpperCase().contains(String.valueOf("Approval").toUpperCase()) || subject.toUpperCase().contains(String.valueOf("Approved").toUpperCase()) || subject.toUpperCase().contains(String.valueOf("Approve").toUpperCase())) {
                    System.out.println("Found it");

                    PrintMsg(String.valueOf(i), message);

                }

            }

//            GetMessage(inbox);
//            SearchEmail(inbox, "Rebate");
//            SearchEmail(inbox, "Rebate  Approved Evidence", "check activity");
//            SearchEmail(inbox, "Critical security alert", "check activity", false, false);
//            SearchEmail(inbox, "Critical security alert", "check activity", true, false);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void SearchEmail(Folder folder, String keyword) {
        System.out.println("=====SearchEmail======");
        try {
            // creates a search criterion
            SearchTerm searchCondition = new SearchTerm() {
                @Override
                public boolean match(Message message) {
                    try {

                        System.out.println(message.getSubject());
                        System.out.println(message.getFlags());

                        if (message.getSubject().contains(keyword)) {
                            return true;
                        }
                    } catch (MessagingException ex) {
                        ex.printStackTrace();
                    }
                    return false;
                }
            };

            // performs search through the folder
            Message[] foundMessages = folder.search(searchCondition);

            for (int i = 0; i < foundMessages.length; i++) {
                Message message = foundMessages[i];
                String subject = message.getSubject();
                System.out.println("Found message #" + i + ": " + subject);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public static void SearchEmail(Folder folder, String subject, String body, boolean seen, boolean recent) {
        System.out.println("=====SearchEmail with seen and recent======");
        try {
            SearchTerm[] srchTerms = new SearchTerm[4];
            srchTerms[0] = new SubjectTerm(subject);
            srchTerms[1] = new BodyTerm(body);
            srchTerms[2] = new FlagTerm(new Flags(Flags.Flag.SEEN), seen);
            srchTerms[3] = new FlagTerm(new Flags(Flags.Flag.RECENT), recent);

            SearchTerm searchTerm = new AndTerm(srchTerms);
            Message[] messages = folder.search(searchTerm);
            System.out.println("Message count: " + messages.length);
            if (messages.length > 0) {
                Message msg = messages[0];
//                PrintMsg(message.getFrom() + ".pdf", msg);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public static void SearchEmail(Folder folder, String subject, String body) {
        System.out.println("=====SearchEmail======");
        try {
            SearchTerm searchTerm = new AndTerm(new SubjectTerm(subject), new BodyTerm(body));
            Message[] messages = folder.search(searchTerm);
            System.out.println("Message count: " + messages.length);
            Message msg = messages[0];
//            PrintMsg(message.getFrom() + ".pdf", msg);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public static void GetMessage(Folder folder) {
        System.out.println("=====GetMessage======");
        try {
            System.out.println("Message count: " + folder.getMessageCount());
            Message msg = folder.getMessage(folder.getMessageCount());
//            PrintMsg(message.getFrom() + ".pdf", msg);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void PrintMsg(String fileName, Message msg) {
        StringBuilder sb = new StringBuilder();
        try {
            Address[] in = msg.getFrom();
            for (Address address : in) {
                System.out.println("FROM:" + address.toString());
                sb.append("FROM:" + address.toString());
                sb.append("\n");
            }
            Multipart mp = (Multipart) msg.getContent();
            int count = mp.getCount();
            System.out.println("body count: " + count);
            for (int i = 0; i < count - 1; i++) {
                System.out.println("===========Body no. " + i);
                BodyPart bp = mp.getBodyPart(i);
                System.out.println("SENT DATE:" + msg.getSentDate());
                System.out.println("SUBJECT:" + msg.getSubject());
                System.out.println("BODY:" + bp.getContent());

                sb.append("SENT DATE:" + msg.getSentDate());
                sb.append("\n");
                sb.append("SUBJECT:" + msg.getSubject());
                sb.append("\n\n");
                sb.append("BODY:\n\n" + bp.getContent());

                String str = new String(sb);
                String replacedStr = str.replace(">", "");

                System.out.println(replacedStr);

                createPdf(fileName, replacedStr);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void createPdf(String fileName, String body)
            throws DocumentException, IOException {

        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream("Evidence_" + fileName + ".pdf"));
        document.open();
        document.add(new Paragraph(body));
        document.close();
    }
}

