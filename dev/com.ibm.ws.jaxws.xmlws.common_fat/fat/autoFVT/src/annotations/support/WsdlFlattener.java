package annotations.support;

import java.io.*;
/**
 * Helper class to imbed imported .xsd files into a single wsdl file.
 * 
 * Needed by some versions of the implementation, and by xpath and wsdl4j, whichh
 * can't handle the imports 
 * 
 * @author btiffany
 *
 */
public class WsdlFlattener {

	/**
	 * for debug 
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		WsdlFlattener w = new WsdlFlattener();
		String wsdlfile= args[0];
		String newwsdl = w.flatten(wsdlfile);	
        System.out.println("wrote: "+newwsdl);
	}
	
	/**
	 * @param wsdlfile - path to input file
	 * @return - name of output file, inputfile + "flat"
	 */
	public static String flatten(String wsdlfile) throws Exception{
		String outfile = wsdlfile+"flat";
        wsdlfile = wsdlfile.replace("\\","/");
		StringBuffer sb = new StringBuffer();
		String path = wsdlfile.substring(0,wsdlfile.lastIndexOf("/"));
		BufferedReader br = new BufferedReader(new FileReader(wsdlfile));
		while(br.ready()){
			sb.append(br.readLine());
			sb.append(System.getProperty("line.separator"));
		}
		//System.out.println(sb.toString());
		br.close();
		int head = 0;
		int lasthead = 0;
		int importtag = 0;
		int tail = 0;
		int importfilehead = 0;
		int importfiletail = 0;
		String importfile = null;
		StringBuffer replacement = new StringBuffer("replaced");
		while (head > -1){
			head = sb.indexOf("<xsd:schema>", lasthead);
			if (head == -1) break;
			lasthead = head+1;
			tail = sb.indexOf("</xsd:schema>", head);
			importtag = sb.indexOf("<xsd:import ", head);
			importfilehead =sb.indexOf("schemaLocation=\"",importtag) +16;
			importfiletail = sb.indexOf("\"",importfilehead);
			importfile = sb.substring(importfilehead, importfiletail);
			replacement.setLength(0);
			// go get the xsd contents and drop 'em in. 
			replacement.append(getImport(path+"/"+importfile));
			sb.replace(head, tail+13, replacement.toString());
			//System.out.println(sb.toString());			
		}
  		//System.out.println(sb.toString());
		BufferedWriter w = new BufferedWriter(new FileWriter(outfile));
		w.write(sb.toString());
		w.flush();
		w.close();
		return outfile;
	}
	 
	static String getImport(String filename) throws Exception {
		BufferedReader br = new BufferedReader(new FileReader(filename));
		// dump the first line
		StringBuffer sb = new StringBuffer("<!-- embedded from: "+filename +"-->\n");
		br.readLine();
		while(br.ready()){
			sb.append("\t"+br.readLine());
			sb.append(System.getProperty("line.separator"));
		}
		//System.out.println(sb.toString());
		br.close();
		return sb.toString();
		
	}
	

}
