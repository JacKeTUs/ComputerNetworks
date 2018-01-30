package print_server;

import javafx.print.PrintResolution;
import javafx.print.PrinterJob;

import java.awt.print.PageFormat;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.print.*;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;
import javax.print.attribute.DocAttributeSet;
import javax.print.attribute.HashDocAttributeSet;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.*;
import javax.print.event.PrintJobAdapter;
import javax.print.event.PrintJobEvent;


class PrintJobWatcher {
    boolean done = false;

    PrintJobWatcher(DocPrintJob job) {
        job.addPrintJobListener(new PrintJobAdapter() {
            public void printJobCanceled(PrintJobEvent pje) {
                allDone();
            }
            public void printJobCompleted(PrintJobEvent pje) {
                allDone();
            }
            public void printJobFailed(PrintJobEvent pje) {
                allDone();
            }
            public void printJobNoMoreEvents(PrintJobEvent pje) {
                allDone();
            }
            void allDone() {
                synchronized (PrintJobWatcher.this) {
                    done = true;
                    System.out.println("Документ успешно напечатан...");
                    PrintJobWatcher.this.notify();
                }
            }
        });
    }
    public synchronized void waitForDone() {
        try {
            while (!done) {
                wait();
            }
        } catch (InterruptedException e) {
        }
    }
}

public class DoPrint extends Thread {

    String file_name;
    File file;
    FileInputStream fileInputStream;
    public DoPrint(String path) {
        file_name = path;
    }

    @Override
    public void run() {
        try {
            //ServerFrame.updateMsgWindow("Info : Printing");
            fileInputStream = new FileInputStream(file_name);

            PrintService printService = PrintServiceLookup.lookupDefaultPrintService();
            StartServer._ta.append("Идёт печать на принтер " + printService.getName() + "\n");

            DocPrintJob job = printService.createPrintJob();

            DocFlavor[] docFlavors = printService.getSupportedDocFlavors();
            for(int i=0; i<docFlavors.length; i++){
                System.out.println(docFlavors[i].toString());
            }
            //DocFlavor docFlavor =DocFlavor.INPUT_STREAM.JPEG;
            DocFlavor docFlavor = getFlavorFromFilename(file_name);
            StartServer._ta.append("Используется формат " + docFlavor.toString() + "\n");
            // DocFlavor docFlavor=DocFlavor.INPUT_STREAM.TEXT_HTML_UTF_8;

            DocAttributeSet docAttributes = new HashDocAttributeSet();
            docAttributes.add(OrientationRequested.PORTRAIT);
            PrintRequestAttributeSet printAttributes = new HashPrintRequestAttributeSet();
            printAttributes.add(new Copies(1));
            printAttributes.add(new JobName("printed", null));

            //SimpleDoc simpleDoc = new SimpleDoc(pdfFile.toURL(), docFlavor, null);
            //Doc doc = new SimpleDoc(new FileInputStream("c://myPdfFile.pdf"), docFlavor, docAttributes);
            //Doc doc = new SimpleDoc(new FileInputStream("c://info.htm"), docFlavor, docAttributes);
            Doc doc = new SimpleDoc(fileInputStream, docFlavor, docAttributes);
            try {
                job.print(doc, printAttributes);
            } catch(PrintException pe){
                StartServer._ta.append("Печать прервана: " + pe.getLocalizedMessage() + "\n");
                System.out.println(pe.getLocalizedMessage());
                return;
            }
            StartServer._ta.append("Печать успешно завершена!\n");
        } catch (FileNotFoundException ex) {
            Logger.getLogger(DoPrint.class.getName()).log(Level.SEVERE, null, ex);
        } /*catch (PrintException ex) {
            Logger.getLogger(DoPrint.class.getName()).log(Level.SEVERE, null, ex);
        }*/
    }

    private DocFlavor getFlavorFromFilename(String file_name) {
        String extension = file_name.substring(file_name.lastIndexOf('.')+1);
        extension = extension.toLowerCase();
        if (extension.equals("gif"))
            return DocFlavor.INPUT_STREAM.GIF;
        else if (extension.equals("jpeg"))
            return DocFlavor.INPUT_STREAM.JPEG;
        else if (extension.equals("jpg"))
            return DocFlavor.INPUT_STREAM.JPEG;
        else if (extension.equals("png"))
            return DocFlavor.INPUT_STREAM.PNG;
        else if (extension.equals("ps"))
            return DocFlavor.INPUT_STREAM.POSTSCRIPT;
        else if (extension.equals("txt"))
            return DocFlavor.INPUT_STREAM.TEXT_PLAIN_UTF_8;
        else if (extension.equals("pdf"))
            return DocFlavor.INPUT_STREAM.PDF;
        // Fallback: try to determine flavor from file content
        else return DocFlavor.INPUT_STREAM.AUTOSENSE;
    }


}
