// @(#) 1.3 autoFVT/src/mtom/enableSOAPBinding/wsfvt/client/AttachmentHelper.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01 5/21/08 12:00:53 [8/8/12 06:58:15]
//
// IBM Confidential OCO Source Material
// (C) COPYRIGHT International Business Machines Corp. 2006, 2007
// The source code for this program is not published or otherwise divested
// of its trade secrets, irrespective of what has been deposited with the
// U.S. Copyright Office.
//
// Change History:
// Date     UserId      Defect          Description
// ----------------------------------------------------------------------------
// 05/20/08 jtnguyen    522617          New AttachmentHelper.java to compare image size only


package mtom.enableSOAPBinding.wsfvt.client;

import javax.imageio.ImageWriter;
import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.ByteArrayOutputStream;
import java.util.Iterator;

import java.io.*;


public class AttachmentHelper {
    
    
    public static boolean compareImages(File imageReceivedName, File imageSentName) {
                
        System.out.println("Compare file = " + imageReceivedName + ", file = " +  imageSentName);
        BufferedImage imageReceived = null;
        BufferedImage imageSent = null;       
        try {
            // read files
            imageReceived = ImageIO.read(imageReceivedName);
            imageSent = ImageIO.read(imageSentName); 
            
        }
        catch ( IOException e ) {
            e.printStackTrace();
            System.out.println( "ERROR! Can't read files." );
        } 
        
        return(compare(imageReceived, imageSent));
    }
       
    public static boolean compareImages(Image imageReceived, Image imageSent) throws IOException {
                        
        System.out.println("Compare image = " + imageReceived + ", image = " +  imageSent);
        BufferedImage bi_imageReceived = (BufferedImage)imageReceived;
        BufferedImage bi_imageSent = (BufferedImage)imageSent; 
        
        return(compare(bi_imageReceived, bi_imageSent));

    }
    
    
    public static boolean compareImages(BufferedImage imageReceived, BufferedImage imageSent) {
                        
        System.out.println("Compare BufferedImage = " + imageReceived + ", image = " +  imageSent);
        
        return(compare(imageReceived, imageSent));
    
    }
    
    private static boolean compare(BufferedImage imageReceived, BufferedImage imageSent) {

        System.out.println("Starting Comparison");
        
        if (imageReceived == null) {
            
           System.out.println("ERROR! Received image is NULL");
           return(false);   
        }
      
        
            int pixel1;   
            int pixel2;
    
            int w1 = imageReceived.getWidth(null);
            int h1 = imageReceived.getHeight(null);
    
            int w2 = imageSent.getWidth(null);
            int h2 = imageSent.getHeight(null);
                             
            System.out.println("Sizes: w1=" + w1 + ",h1=" + h1 + ",w2=" + w2 + ",h2=" + h2);
            
            // compare height and width 
            if( ((w1 != w2) || (h1 != h2)))
            {
    
                 System.out.println("FAILED! Comparison - Sizes are different.");
                 System.out.println("Sizes: w1=" + w1 + ",h1=" + h1 + ",w2=" + w2 + ",h2=" + h2);
                 return(false);  // images not equal
            }
            else {
                /*
    
                System.out.println("Comparing pixels...");
                // continue to compare pixel
                for (int j = 0 ; j < h1; j++)
                {
                    for (int i = 0; i < w1; i++)
                    {
                            pixel1 = imageReceived.getRGB(i, j);   
                            pixel2 = imageSent.getRGB(i, j);
    
                            System.out.println("pixel1 = " + pixel1 + ", pixel2 = " + pixel2 );
    
                          
                            if( pixel1 != pixel2 ) 
                            {
    
                                System.out.println("FAILED! Comparison - Pixels are not matched.");
                                System.out.println("location: i = " + i + ", j = " + j );
                                return (false);  // images not equal
                            }
                    }
                }
                
                */
    
                 
                System.out.println("Comparison of image size - Successful.");
                return (true);  // images equal
            }
        


    }
    
    public static byte[] getImageBytes(Image image, String type) throws IOException {
    
      
        ByteArrayOutputStream baStream = new ByteArrayOutputStream();
        ImageOutputStream imageStream = null;
        ImageWriter writer = null;
        
        int width = image.getWidth(null);
        int height = image.getHeight(null);
        BufferedImage bi = new BufferedImage (width,height,BufferedImage.TYPE_INT_RGB);
        
        // get correct MIME type of the image
        System.out.println("type = " + type);
        Iterator<ImageWriter> iter = ImageIO.getImageWritersByMIMEType(type);
        if (iter.hasNext()) {
               writer = (ImageWriter)iter.next();
        }

        if (writer != null) {
            imageStream = ImageIO.createImageOutputStream(baStream);
            writer.setOutput(imageStream);

            writer.write(bi);
            imageStream.close();
            
            return (baStream.toByteArray());
        } else {
        
            System.out.println("ERROR!!! - NULL byte array stream.");
            return null; 
        }
        
    }
    
    
    public static boolean compareTextFiles(String filename1, String filename2) {
        
        BufferedReader inputStream = null;
        BufferedReader sourceStream = null;
        
        if ((filename1 == null) || (filename2 == null))
        	return false;
        
      	try {
      	    inputStream = new BufferedReader(new FileReader(filename1));
     	    sourceStream = new BufferedReader(new FileReader(filename2));

      	    String line = null;
      	    String sourceLine = null;

      	    // Get the lines
      	    line = inputStream.readLine();
      	    sourceLine = sourceStream.readLine();
               	    
      	    while (line != null) {
      		  // System.out.println(line);
      		   if (line.compareTo(sourceLine) != 0) {
      			 System.out.println("Lines mismatched: received=\"" + line + "\",source=\"" + sourceLine);
      			 return false;
      	           }
      		   else {
      			   
      		         line = inputStream.readLine();
      		         sourceLine = sourceStream.readLine();
                   }
      	    }

      	    inputStream.close();
      	    sourceStream.close();  
            return true;
      	    
      	}
      	catch (FileNotFoundException e) {
            e.printStackTrace();
      	    System.out.println("Error opening the file(s).");
      	}
      	catch (IOException e) {
            e.printStackTrace();
      	    System.out.println("Error reading from the file(s)");
      	}
      	return false;
      	
    }
    
}


