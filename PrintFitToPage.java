package no.ssb.ppi.print;

import java.awt.print.Printable;
import java.awt.print.PageFormat;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Component;
import javax.swing.RepaintManager;

/**
 * Class PrintFitToPage
 *
 *   Utility class that scales any component to fit to the default printers full page.
 *   The class can be added as an ActionListener to a print button as outlined below.
 *
 *   // Create the component to be printed
 *   FameChart fameChart = new FameChart();
 *   // Create a button and add it to a Panel
 *   JButton printButton = new JButton("Print");
 *   aPanel.add(printButton);
 *
 *   // Set up the ActionListener to print the desired component.
 *   printButton.addActionListener(
 *       new java.awt.event.ActionListener()
 *       {
 *           public void actionPerformed(java.awt.event.ActionEvent event)
 *           {
 *               try
 *               {
 *                   // Print a FameChart with default LANDSCAPE orientation.
 *                   PrintFitToPage.print( fameChart);
 *               }
 *               catch( PrinterException printerException)
 *               {
 *                   // Display an error message if there are any Exceptions with printing.
 *                   JOptionPane.showMessageDialog( getContentPane(), printerException.getMessage(), "Printer Error", JOptionPane.ERROR_MESSAGE);
 *               }
 *           }
 *       });
 *
 * NOTE: Large complicated components can produce large spool files and slow printing.
 */

public class PrintFitToPage
   implements Printable{
    // The component to be printed.
    public Component m_component;
    //LANDSCAPE, PORTRAIT, REVERSE_LANDSCAPE of java.awt.print.PageFormat
    public int m_orientation;

    /**
     * Constructor PrintFitToPage
     *
     *
     * @param pComponent The component to be printed.
     *
     */

    public PrintFitToPage (Component pComponent){
        this(pComponent, PageFormat.LANDSCAPE);
    }

    /**
     * Constructor PrintFitToPage
     *
     *
     * @param pComponent The component to be printed.
     * @param pOrientation The Orientation from PageFormat.
     *
     */

    public PrintFitToPage (Component pComponent, int pOrientation){
        m_component   = pComponent;
        m_orientation = pOrientation;
    }

    /**
     * Method print
     *
     *
     * @param pComponent The component to be printed.
     * @param pOrientation The orientation from PageFormat.
     *
     * @throws PrinterException
     *
     */

    public static void print (Component pComponent, int pOrientation)
       throws PrinterException{
        new PrintFitToPage(pComponent, pOrientation).print();
    }

    /**
     * Method print
     *
     *
     * @param pComponent The component to be printed.
     *
     * @throws PrinterException
     *
     */

    public static void print (Component pComponent)
       throws PrinterException{
        new PrintFitToPage(pComponent).print();
    }

    /**
     * Method print
     *  Set up the print Job
     *
     * @throws PrinterException
     *
     */

    public void print ()
       throws PrinterException{
        // Get the default printer.
        PrinterJob printerJob = PrinterJob.getPrinterJob();
        printerJob.setJobName("PrintFitToPage");
        PageFormat pf = printerJob.defaultPage();
        pf.setOrientation(m_orientation);
        printerJob.setPrintable(this, pf);
        printerJob.print();
    }

    /**
     * Method print
     *
     * Render graphics on the printer.
     * This function is not intended to be called directly.
     *
     * @param g The graphics context of the printer
     * @param pf The PageFormat of the printer
     * @param pi The page index to be rendered
     *
     * @return Whether the page index exists for printing
     *
     * @throws PrinterException
     *
     */

    public int print (Graphics g, PageFormat pf, int pi)
       throws PrinterException{
        // there is only 1 page to be printed
        if (pi > 0){
            return Printable.NO_SUCH_PAGE;
        }

        // Scale the resolution to the printer
        Graphics2D g2     = ( Graphics2D ) g;
        int        xPage  = ( int ) pf.getImageableX();
        int        yPage  = ( int ) pf.getImageableY();
        int        wPage  = ( int ) pf.getImageableWidth();
        int        hPage  = ( int ) pf.getImageableHeight();
        int        width  = m_component.getWidth();
        int        height = m_component.getHeight();
        double     wScale = ( double ) wPage / ( double ) width;
        double     hScale = ( double ) hPage / ( double ) height;
        double     scale  = java.lang.Math.min(wScale, hScale);

        // Turn off double buffereing
        RepaintManager.currentManager(m_component).setDoubleBufferingEnabled(false);
        // Set up image transformations
        g2.translate(xPage, yPage);
        g2.scale(scale, scale);
        // Paint the component into the printer graphics context.
        m_component.paint(g2);
        // Turn on double buffereing
        RepaintManager.currentManager(m_component).setDoubleBufferingEnabled(true);
        // Clean up resources in an attempt to improve efficiency
        System.gc();
        return Printable.PAGE_EXISTS;
    }
}
