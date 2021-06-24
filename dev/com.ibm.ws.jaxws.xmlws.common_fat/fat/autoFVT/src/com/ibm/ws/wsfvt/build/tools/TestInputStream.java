/*
 * @(#) 1.3 autoFVT/src/com/ibm/ws/wsfvt/build/tools/TestInputStream.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 11/7/08 11:07:23 [8/8/12 06:56:44]
 *
 * COMPONENT_NAME: WAS.webservices.fvt
 *
 * IBM Confidential OCO Source Material
 * 5639-D57, 5630-A36, 5630-A37, 5724-D18 (C) COPYRIGHT International Business Machines Corp. 2002, 2004
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 *
 * Date        Feature/Defect       Author           Description
 * ----------  -----------------    ---------        ----------------------------
 * 12/09/2002  155271               ulbricht         New file
 * 08/09/2004  222747               ulbricht         z/OS requires different encoding
 * 09/07/2004  227113               ulbricht         Add new constructor to set encoding
 * 05/24/2007  440922               jramos           Changes for Pyxis
 * 11/03/2008  559143               jramos           Incorporate Simplicity
 *        
 */
package com.ibm.ws.wsfvt.build.tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.ibm.websphere.simplicity.Machine;

/**
 * This class can be used as is or extended to allow for searching through
 * the output from an InputStream.  The Thread class is extended to allow
 * for multiple InputStreams to be read at the same time.
 */
public class TestInputStream extends Thread {

    private InputStream inputStream;
    private String encoding;

    /**
     * A constructor with an input argument of an InputStream.  The
     * encoding will use defaults.
     *
     * @param _inputStream The InputStream to be read.
     */
    public TestInputStream(InputStream _inputStream) {
        inputStream = _inputStream;
        try {
        	encoding = Machine.getLocalMachine().getOperatingSystem().getDefaultEncoding();
        } catch(Exception e) {
        	e.printStackTrace();
        }
    }

    /**
     * A constructor that allows for setting the encoding.
     *
     * @param _inputStream The InputStream to be read
     * @param _encoding The encoding to read the InputStream in
     */
    public TestInputStream(InputStream _inputStream, String _encoding) {
        inputStream = _inputStream;
        encoding = _encoding;
    }

    /**
     * This method is required for an object that extends the Thread
     * object.  This method will set up the BufferedReader for reading
     * the stream.
     */
    public void run() {
        InputStreamReader isr = null;
        BufferedReader br = null;
        try {
            isr = new InputStreamReader(inputStream, encoding);
            br = new BufferedReader(isr);
            readStream(br);
            br.close();
            isr.close();
        } catch (IOException ioe) {
            reportException(ioe);
        } finally {
            try {
                if (isr != null) { isr.close(); }
                if (br != null) { br.close(); }
            } catch (IOException ex) {
            }
        }
    }

    /**
     * This method is a basic implementation of a read that will
     * read the input stream and print the output to the console.
     *
     * @param br A BufferedReader object to be read one line at a
     *           time
     * @throws IOException An I/O exception during reading of the 
     *                     InputStream
     */
    public void readStream(BufferedReader br) throws IOException {
        String line = null;
        while ((line = br.readLine()) != null) {
            System.out.println(line);
        }
    }

    /**
     * A basic implementation that will print the stack trace for an
     * exception.
     *
     * @param e An Exception to run printStackTrace on
     */
    public void reportException(Exception e) {
        e.printStackTrace();
    }

}

