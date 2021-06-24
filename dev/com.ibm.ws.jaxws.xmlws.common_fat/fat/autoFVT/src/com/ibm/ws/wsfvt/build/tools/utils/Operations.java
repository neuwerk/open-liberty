/*
 * autoFVT/src/com/ibm/ws/wsfvt/build/tools/utils/Operations.java, WAS.websvcs.fvt, WAS85.FVT, cf011231.01
 *
 * IBM Confidential OCO Source Material
 * 5724-i63 (C) COPYRIGHT International Business Machines Corp. 2002, 2006
 * The source code for this program is not published or otherwise divested
 * of its trade secrets, irrespective of what has been deposited with the
 * U.S. Copyright Office.
 *
 * Date        Feature/Defect       Author           Description
 * ----------  -----------------    ---------        ----------------------------
 * 03/12/2003  LIDB1270.256.11      ulbricht         Add method to check for IHS
 * 10/19/2003  D179331.2            ulbricht         Add enable/disable trace methods
 * 10/31/2003  D181424              ulbricht         Add standard method to run exec
 * 01/07/2004  D186680              ulbricht         Exec method change for z/OS
 * 01/08/2004  D186806              ulbricht         Need encoding for z/OS
 * 01/20/2004  D187853              ulbricht         Add classloader mode change command
 * 01/26/2004  D188429              ulbricht         New algorithm for cell/node name due to z/OS
 * 01/30/2004  D188860              ulbricht         getCellAndNodeName method removed
 * 03/01/2004  LIDB2931-05.3        ulbricht         Add method to read zip files
 * 04/21/2004  D195811              ulbricht         Add starting weight change command
 * 06/24/2004  D211929              ulbricht         Modify for new installer paths
 * 07/09/2004  D215233              ulbricht         Modify for new installer paths
 * 08/09/2004  D222747              ulbricht         Add method to get reader with encoding
 * 08/19/2004  D225528              ulbricht         Add method to tell if z/OS
 * 02/22/2005  D254265              ulbricht         Add method to set java.library.path for z/OS
 * 03/10/2005  D260687              ulbricht         jacl command changed for classloaderMode
 * 03/25/2005  D268461              ulbricht         Don't hard code server name
 * 04/15/2005  D269183              ulbricht         iSeries doesn't have sh on end of scripts
 * 05/16/2005  D274529              ulbricht         Ignore case when removing .sh for iSeries
 * 08/02/2005  D294431              ulbricht         Dont remove .sh for some files
 * 04/23/2006  308793.2             ulbricht         Add method for writing file
 * 08/15/2006  381622               jramos           Made changes for new framework topology classes
 * 11/01/2006  LIDB4401-30.01       ulbricht         Add file read method
 * 01/17/2007  414850               btiffany         Add writefile w. default encoding
 * 05/24/2007  440922               jramos           Changes for Pyxis
 * 10/22/2007  476750               jramos           Use TopologyDefaults tool and ACUTE 2.0 api
 * 03/19/2008  506378               btiffany         improve timeout behavior of  getUrlStatus
 * 09/10/2008  538244.1.1           btiffany         fix bug in fetchUrl
 * 10/31/08    559143               jramos           Incorporate Simplicity
 */
package com.ibm.ws.wsfvt.build.tools.utils;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import com.ibm.websphere.simplicity.Machine;
import com.ibm.websphere.simplicity.OperatingSystem;
import com.ibm.websphere.simplicity.RemoteFile;
import com.ibm.websphere.simplicity.Server;
import com.ibm.websphere.simplicity.Topology;
import com.ibm.ws.wsfvt.build.tools.AppConst;

/**
 * The Operations class contains general purpose methods that are
 * helpful to many tests.
 */
public class Operations  {

	private static Random generator = null;

	/**
	 * The compareTextFiles method will accept two String arguments
	 * containing text file names and compare the files.
	 * 
	 * @param file1
	 *            A String containing the file name of the first file
	 *            to be compared
	 * @param file2
	 *            A String containing the file name of the second file
	 *            to be compared
	 * @return A boolean value which returns true if the files are the
	 *         same.
	 */
	public static boolean compareTextFiles(String file1, String file2) {

		List file1Contents = readTextFile(file1);
		List file2Contents = readTextFile(file2);

		return compareLineByLine(file1Contents, file2Contents);

	}

	/**
	 * The compareTextFiles method will copy two files from specified
	 * hosts to the local machine and compare the files
	 * 
	 * @param file1Host
	 *            The hostname of the machine that contains the first
	 *            file
	 * @param srcFile1
	 *            The path to the first file
	 * @param destFile1
	 *            The path to copy the first file to on the local
	 *            machine
	 * @param file2Host
	 *            The hostname of the machine that contains the second
	 *            file
	 * @param srcFile2
	 *            The path to the second file
	 * @param destFile2
	 *            The path to copy the second file to on the local
	 *            machine
	 * @return A boolean value which returns true if the files are the
	 *         same
	 * @throws STAFException
	 */
	public static boolean compareTextFiles(String file1Host, String srcFile1, String destFile1,
			String file2Host, String srcFile2, String destFile2) throws Exception {

		File destFileOne = new File(destFile1);
		File destFileTwo = new File(destFile2);
        Machine srcM1 = Machine.getMachine(Topology.getBootstrapFileOps().getMachineConnectionInfo(file1Host));
        Machine srcM2 = Machine.getMachine(Topology.getBootstrapFileOps().getMachineConnectionInfo(file2Host));
        RemoteFile srcFileOne = srcM1.getFile(srcFile1);
        RemoteFile srcFileTwo = srcM2.getFile(srcFile2);

		srcFileOne.copyToDest(Machine.getLocalMachine().getFile(destFileOne.getAbsolutePath()));
        srcFileTwo.copyToDest(Machine.getLocalMachine().getFile(destFileTwo.getAbsolutePath()));

		return Operations.compareTextFiles(destFileOne.getAbsolutePath(), destFileTwo
				.getAbsolutePath());
	}

	/**
	 * This method will verify that the two contents first contain the
	 * same number of lines. If the files contain the same number of
	 * lines, the lines are then compared to each other to verify that
	 * they contain the same content.
	 * 
	 * @param file1Contents
	 *            A List containing the contents of the first file
	 * @param file2Contents
	 *            A List containing the contents of the second file
	 * @return A boolean that returns true if the files are the same
	 */
	private static boolean compareLineByLine(List file1Contents, List file2Contents) {

		// If the two files don't contain the same number
		// of lines, we don't have to do anymore checking.
		if (file1Contents.size() != file2Contents.size()) {
			return false;
		}

		Iterator file1Lines = file1Contents.iterator();
		Iterator file2Lines = file2Contents.iterator();
		String file1InputLine = "";
		String file2InputLine = "";
		boolean fileContentsEqual = true;

		while (file1Lines.hasNext()) {

			try {
				file1InputLine = (String) file1Lines.next();
				file2InputLine = (String) file2Lines.next();
				if (!file1InputLine.equals(file2InputLine)) {
					fileContentsEqual = false;
					break;
				}
			} catch (NoSuchElementException e) {
				fileContentsEqual = false;
				break;
			}

		}

		return fileContentsEqual;

	}

	/**
	 * This method will compare a file in a Jar file with another file
	 * not contained in a Jar file. The Jar file and comparison file
	 * hosts are specified and the files are copied to the local host
	 * for comparison.
	 * 
	 * @param srcJarFileHost
	 *            The hostname of the machine that has the source Jar
	 *            file
	 * @param srcJarFile
	 *            A JarFile object which contains a file to be
	 *            compared
	 * @param destJarFile
	 *            The location to copy the srcJarFile to on the local
	 *            machine
	 * @param fileInJarFile
	 *            The name of the file in the Jar file to be compared
	 * @param comparisonFileHost
	 *            The hostname of the machine that has the source
	 *            comparison file
	 * @param srcComparisonFile
	 *            A file not in a Jar file to be compared
	 * @param destComparisonFile
	 *            The location to copy the comparison file to on the
	 *            local host
	 * @return A boolean that returns true if the files are the same
	 */
	public static boolean compareTextFiles(String srcJarFileHost, JarFile srcJarFile,
			String destJarFile, String fileInJarFile, String comparisonFileHost,
			String srcComparisonFile, String destComparisonFile) throws Exception,
			IOException {

		File destJar = new File(destJarFile);
		File destComparison = new File(destComparisonFile);
        Machine srcM1 = Machine.getMachine(Topology.getBootstrapFileOps().getMachineConnectionInfo(srcJarFileHost));
        Machine srcM2 = Machine.getMachine(Topology.getBootstrapFileOps().getMachineConnectionInfo(comparisonFileHost));
        RemoteFile srcFileOne = srcM1.getFile(srcJarFile.getName());
        RemoteFile srcFileTwo = srcM2.getFile(srcComparisonFile);
        
        srcFileOne.copyToDest(Machine.getLocalMachine().getFile(destJar.getAbsolutePath()));
        srcFileTwo.copyToDest(Machine.getLocalMachine().getFile(destComparison.getAbsolutePath()));
        
		return Operations.compareTextFiles(new JarFile(destJar), fileInJarFile, destComparison
				.getAbsolutePath());
	}

	/**
	 * This method will compare a file in a Jar file with another file
	 * not contained in a Jar file.
	 * 
	 * @param jarFile
	 *            A JarFile object which contains a file to be
	 *            compared
	 * @param fileInJarFile
	 *            The name of the file in the Jar file to be compared
	 * @param comparisonFile
	 *            A file not in a Jar file to be compared
	 * @return A boolean that returns true if the files are the same
	 */
	public static boolean compareTextFiles(JarFile jarFile, String fileInJarFile,
			String comparisonFile) {

		List file1Contents = readTextFile(jarFile, fileInJarFile);
		List file2Contents = readTextFile(comparisonFile);

		return compareLineByLine(file1Contents, file2Contents);

	}

	/**
	 * This method will read a text file contained in Jar file and
	 * return the contents of the text file in a List.
	 * 
	 * @param jarFile
	 *            A JarFile object containing a text file to be read
	 * @param fileInJarFile
	 *            The name of the file in a Jar file to be read
	 * @return A list containing the contents of the text file read
	 *         from the Jar file
	 */
	public static List readTextFile(JarFile jarFile, String fileInJarFile) {

		List<String> inputFileContents = new ArrayList<String>();
		InputStream is = null;
		BufferedReader in = null;

		try {

			String inputLine = "";
			ZipEntry jarFileEntry = jarFile.getEntry(fileInJarFile);
			is = jarFile.getInputStream(jarFileEntry);
			in = new BufferedReader(new InputStreamReader(is, AppConst.DEFAULT_ENCODING));

			while ((inputLine = in.readLine()) != null) {
				if (!inputLine.startsWith("<!--")) {
					inputFileContents.add(inputLine);
				}
			}

			in.close();
			is.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return inputFileContents;

	}

	/**
	 * This method copies a Jar file from a specified host to the
	 * local machine and returns the contents of the specified file
	 * within the Jar file
	 * 
	 * @param srcHost
	 *            The hostname of the machine that contains the Jar
	 *            file
	 * @param srcJarFile
	 *            A JarFile object containing a text file to be read
	 * @param destJarFile
	 *            The location to copy the Jar file to on the local
	 *            machine
	 * @param fileInJarFile
	 *            The name of the file in a Jar file to be read
	 * @return A list containing the contents of the text file read
	 *         from the Jar file
	 * @throws STAFException
	 * @throws IOException
	 */
	public static List readTextFile(String srcHost, JarFile srcJarFile, String destJarFile,
			String fileInJarFile) throws Exception, IOException {

		File destFile = new File(destJarFile);
        Machine srcM1 = Machine.getMachine(Topology.getBootstrapFileOps().getMachineConnectionInfo(srcHost));
        RemoteFile srcFileOne = srcM1.getFile(srcJarFile.getName());
        
        srcFileOne.copyToDest(Machine.getLocalMachine().getFile(destFile.getAbsolutePath()));
        
		return readTextFile((new JarFile(destFile)), fileInJarFile);
	}

	/**
	 * The readTextFile method accepts a String containing the name of
	 * the text file to read. The file will be read, its contents
	 * contents added to a collection object and returned.
	 * 
	 * @param fileName
	 *            A String containing the name of the text file to be
	 *            read
	 * @return A List containing the contents of the file
	 */
	public static List readTextFile(String fileName) {

		List<String> inputLineContents = new ArrayList<String>();
		FileInputStream fis = null;
		BufferedReader in = null;

		try {

			String inputLine = "";
			fis = new FileInputStream(fileName);
			in = new BufferedReader(new InputStreamReader(fis, AppConst.DEFAULT_ENCODING));

			while ((inputLine = in.readLine()) != null) {
				inputLineContents.add(inputLine);
			}

		} catch (EOFException eof) {
			// Ok to reach the end of file
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (in != null) {
					in.close();
				}
				if (fis != null) {
					fis.close();
				}
			} catch (IOException ignore) {
				// let it go!
			}
		}

		return inputLineContents;

	}

	/**
	 * This method copies a file from the specified host to the local
	 * machine and returns the contents in a collection object.
	 * 
	 * @param srcHost
	 *            The hostname of the machine that contains the file
	 * @param srcFile
	 *            The path to the file
	 * @param destFile
	 *            The location to copy the file to on the local
	 *            machine
	 * @return A List containing the contents of the file
	 * @throws STAFException
	 */
	public static List readTextFile(String srcHost, String srcFile, String destFile)
			throws Exception {

		File myDestFile = new File(destFile);
        Machine srcM1 = Machine.getMachine(Topology.getBootstrapFileOps().getMachineConnectionInfo(srcHost));
        RemoteFile srcFileOne = srcM1.getFile(srcFile);
        
        srcFileOne.copyToDest(Machine.getLocalMachine().getFile(myDestFile.getAbsolutePath()));

		return readTextFile(myDestFile.getAbsolutePath());
	}

	/**
	 * This method returns the contents of a text file in String form
	 * and uses ISO8859-1 encoding to do the read.
	 * 
	 * @param src
	 *            The file to be read
	 * @return The contents of the file
	 * @throws IOException
	 *             Any kind I/O error
	 */
	public static String readTextFile(File src) throws IOException {
		return readTextFile(src, AppConst.DEFAULT_ENCODING);
	}

	/**
	 * This method copies a file from the specified host to the local
	 * machine and returns the contents of the file in String format.
	 * It uses ISO8859-1 encoding to do the read.
	 * 
	 * @param srcHost
	 *            The hostname of the machine that contains the file
	 * @param srcFile
	 *            The path to the file
	 * @param destFile
	 *            The path to copy the file to on the local machine
	 * @return The contents of the file
	 * @throws STAFException
	 * @throws IOException
	 */
	public static String readTextFile(String srcHost, File srcFile, File destFile)
			throws Exception, IOException {

        Machine m = Machine.getMachine(Topology.getBootstrapFileOps().getMachineConnectionInfo(srcHost));
        RemoteFile src = m.getFile(srcFile.getAbsolutePath());
        RemoteFile dest = Machine.getLocalMachine().getFile(destFile.getAbsolutePath());
        src.copyToDest(dest);
		return readTextFile(destFile);
	}

	/**
	 * This method returns the contents of a text file in String form.
	 * 
	 * @param src
	 *            The file to be read
	 * @param encoding
	 *            The encoding of the file to be read
	 * @return The contents of the file
	 * @throws IOException
	 *             Any kind I/O error
	 */
	public static String readTextFile(File src, String encoding) throws IOException {

		Reader reader = null;
		String contents = null;

		try {
			reader = new InputStreamReader(new FileInputStream(src), encoding);

			BufferedReader br = new BufferedReader(reader);

			int fileLengthInBytes = (int) src.length();
			StringBuffer tmpBuf = new StringBuffer(fileLengthInBytes);
			int readChar = 0;

			while (true) {
				readChar = br.read();
				if (readChar < 0) {
					break;
				}
				tmpBuf.append((char) readChar);
			}

			contents = tmpBuf.toString();

			br.close();
			reader = null;

		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
				}
			}
		}

		return contents;

	}

	/**
	 * wsadmin requires that the file separator be escaped when
	 * issuing commands on Windows. This method will add an an escape
	 * to the file separator in the file path.
	 * 
	 * @param existingString
	 *            String that needs an escape added for the file
	 *            separator
	 * @return A String that has added the additional escape
	 *         characters
	 */
	public static String escapeFileSeparator(String existingString) {

		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < existingString.length(); i++) {
			if (existingString.charAt(i) == '\\') {
				sb.append('\\');
				sb.append('\\');
			} else {
				sb.append(existingString.charAt(i));
			}
		}
		return sb.toString();

	}

	/**
	 * This method will get the local host.
	 * 
	 * @return A String containing the local host of the computer
	 */
	public static String getLocalHost() {

		String localHost = null;

		try {
			localHost = InetAddress.getLocalHost().getHostName();

			if ((localHost != null) && (localHost.indexOf(".") != -1)) {
				localHost = localHost.substring(0, localHost.indexOf("."));
			}

		} catch (UnknownHostException uhe) {
			uhe.printStackTrace();
		}

		return localHost;

	}

	/**
	 * This method will make a backup file of the file name passed as
	 * an argument. The backup file will have the same name as the
	 * original file with ".bak" appended to the end. This method
	 * assume default encoding of ISO8859-1.
	 * 
	 * @param originalFileName
	 *            A String containing the name of the file to backup
	 */
	public static void makeBackupFile(String originalFileName) {
		makeBackupFile(originalFileName, AppConst.DEFAULT_ENCODING);
	}

	/**
	 * This method will make a backup file of the file name passed as
	 * an argument. The backup file will have the same name as the
	 * original file with ".bak" appended to the end.
	 * 
	 * @param originalFileName
	 *            A String containing the name of the file to backup
	 * @param encoding
	 *            The encoding of the file
	 */
	public static void makeBackupFile(String originalFileName, String encoding) {

		File originalFile = new File(Operations.escapeFileSeparator(originalFileName));
		File backupFile = new File(Operations.escapeFileSeparator(originalFileName + ".bak"));

		try {

			String inputLine = "";
			FileInputStream fis = new FileInputStream(originalFile);
			BufferedReader in = new BufferedReader(new InputStreamReader(fis, encoding));

			FileOutputStream fos = new FileOutputStream(backupFile);
			OutputStreamWriter osw = new OutputStreamWriter(fos, encoding);
			PrintWriter out = new PrintWriter(osw);

			while ((inputLine = in.readLine()) != null) {
				out.println(inputLine);
			}

			in.close();
			fis.close();

			out.close();
			osw.close();
			fos.close();

		} catch (EOFException eof) {
			// Ok to reach the end of file
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * This method will restore a backup file to the original file
	 * name.
	 * 
	 * @param backupFileName
	 *            A String containing the name of the backup file
	 */
	public static void restoreBackupFile(String backupFileName) {
		int backupIndex = backupFileName.indexOf(".bak");
		String originalFileName = backupFileName.substring(0, backupIndex);
		File originalFile = new File(Operations.escapeFileSeparator(originalFileName));
		originalFile.delete();
		File backupFile = new File(Operations.escapeFileSeparator(backupFileName));
		backupFile.renameTo(originalFile);
	}

	/**
	 * This method will return a string containing the actual name of
	 * the module (instead of the description module name).
	 * 
	 * @param uri
	 *            A string containing the entire URI
	 * @return A string containing the actual module name in the ear
	 *         file
	 */
	public static String getUriModuleName(String uri) {
		return uri.substring(0, uri.indexOf(","));
	}

	/**
	 * This method will return a command that can be fed into wsadmin
	 * to change the classloader mode on an application.
	 * 
	 * @param appName
	 *            The name of app to have classloader mode changed
	 * @param moduleUri
	 *            The name of the module within the application
	 * @param mode
	 *            The mode to change to (ex. PARENT_LAST or
	 *            PARENT_FIRST)
	 * @param iServerProcess
	 *            The iServerProcess to run this command
	 * @return The wsadmin command for changing the classloader mode
	 */
	public static String getClsLoaderModeChgCmd(String appName, String moduleUri, String mode,
			Server iServerProcess) throws Exception {
		return modifyAppDeployment(appName, moduleUri, "[[classloaderMode " + mode + "]]",
				iServerProcess);
	}

	/**
	 * This method will create a string containing commands for
	 * changing attributes on a module within an application.
	 * 
	 * @param appName
	 *            The name of the app that needs to change
	 * @param moduleUri
	 *            The module within the app that needs to change
	 * @param modifyAttr
	 *            The attribute and value to be changed
	 * @param iServerProcess
	 *            The iServerProcess to run this command
	 * @return A wsadmin jacl command to change the attribute on the
	 *         module
	 */
	public static String modifyAppDeployment(String appName, String moduleUri, String modifyAttr,
			Server iServerProcess) throws Exception {
		Machine machine = iServerProcess.getNode().getMachine();
        return "deployment = AdminConfig.getid(\'/Deployment:" + appName + "/\')"
                + machine.getOperatingSystem().getLineSeparator() + "appDeploy = AdminConfig.showAttribute("
                + "deployment, \'deployedObject\')" + machine.getOperatingSystem().getLineSeparator()
                + "appModules = AdminConfig.show(appDeploy, \'modules\')" + machine.getOperatingSystem().getLineSeparator()
                + "from java.util import StringTokenizer" + machine.getOperatingSystem().getLineSeparator()
                + "moduleTokenizer = StringTokenizer(appModules, \' []\')" + machine.getOperatingSystem().getLineSeparator()
                + "moduleTokenizer.nextToken()" + machine.getOperatingSystem().getLineSeparator()
                + "while(moduleTokenizer.hasMoreTokens()):" + machine.getOperatingSystem().getLineSeparator()
                + "    module = moduleTokenizer.nextToken()" + machine.getOperatingSystem().getLineSeparator()
                + "    uri = AdminConfig.showAttribute(module, \'uri\')" + machine.getOperatingSystem().getLineSeparator()
                + "    if(uri == \'" + moduleUri + "\'):" + machine.getOperatingSystem().getLineSeparator()
                + "        AdminConfig.modify(module, \'" + modifyAttr + "\')" + machine.getOperatingSystem().getLineSeparator()
                + "        AdminConfig.save()" + machine.getOperatingSystem().getLineSeparator()
                + "        break" + machine.getOperatingSystem().getLineSeparator();
	}

	/**
	 * This method will retrieve a file from a zip file. It does not
	 * have to deal with encoding because we are writing bytes.
	 * 
	 * @param outerFile
	 *            The zip file that contains the desired file
	 * @param innerFile
	 *            The file to be retrieved from the zip file
	 * @param destDir
	 *            The directory where the innerFile will be written
	 * @throws IOException
	 *             Any kind of IO Exception
	 * @throws FileNotFoundException
	 *             If the file is not found
	 * @throws ZipException
	 *             Any kind of zip exception
	 */
	public static void getFileFromZipFile(String outerFile, String innerFile, String destDir)
			throws IOException, FileNotFoundException, ZipException {

		FileOutputStream fos = null;
		ZipOutputStream zos = null;
		ZipInputStream zis = null;
		FileInputStream fis = null;

		try {
			fis = new FileInputStream(new File(outerFile));
			zis = new ZipInputStream(fis);
			ZipEntry zipEntry = null;
			zipEntry = zis.getNextEntry();
			while (zipEntry != null) {
				if (!zipEntry.toString().equals(innerFile)) {
					zipEntry = zis.getNextEntry();
					continue;
				}
				// if we're here, we've found the zipEntry

				fos = new FileOutputStream(destDir + "/" + innerFile);

				int n;
				byte[] buf = new byte[1024];
				while ((n = zis.read(buf, 0, 1024)) > -1)
					fos.write(buf, 0, n);

				fos.close();
				zis.closeEntry();
				// we're done, let's break out of this while loop
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (fos != null) {
					fos.close();
				}
				if (zos != null) {
					zos.close();
				}
				if (zis != null) {
					zis.close();
				}
				if (fis != null) {
					fis.close();
				}
			} catch (Exception e) {
				// let it go!
				e.printStackTrace();
			}
		}
	}

	/**
	 * This method will retrieve a zip file from a specified host to
	 * the local machine and then retrieve the inner file from the zip
	 * file.
	 * 
	 * @param srcHost
	 *            The hostname of the machine with the zip file
	 * @param outerSrcFile
	 *            The zip file that contains the inner file
	 * @param outerDestFile
	 *            The absolute path to copy the zip file to on the
	 *            local machine
	 * @param innerFile
	 *            The file to be retrieved from the zip file
	 * @param innerFileDestDir
	 *            The directory on the local machine where the inner
	 *            file will be written
	 * @throws STAFException
	 * @throws FileNotFoundException
	 * @throws ZipException
	 * @throws IOException
	 */
	public static void getFileFromZipFile(String srcHost, String outerSrcFile,
			String outerDestFile, String innerFile, String innerFileDestDir)
			throws Exception, FileNotFoundException, ZipException, IOException {

		File destFile = new File(outerDestFile);
		File innerDestDir = new File(innerFileDestDir);
        Machine m = Machine.getMachine(Topology.getBootstrapFileOps().getMachineConnectionInfo(srcHost));
        RemoteFile src = m.getFile(outerSrcFile);
        RemoteFile dest = Machine.getLocalMachine().getFile(destFile.getAbsolutePath());
        src.copyToDest(dest);

		Operations.getFileFromZipFile(destFile.getAbsolutePath(), innerFile, innerDestDir
				.getAbsolutePath());
	}

	/**
	 * Generate a single random integer between aLowerLimit and
	 * aUpperLimit, inclusive.
	 * 
	 * @param aLowerLimit
	 *            The lower end of the range
	 * @param aUpperLimit
	 *            The high end of the range
	 * @exception IllegalArgumentException
	 *                if aLowerLimit is not less than aUpperLimit.
	 */
	public static int pickNumberInRange(int aLowerLimit, int aUpperLimit) {
		if (aLowerLimit >= aUpperLimit) {
			StringBuffer message = new StringBuffer();
			message.append("Lower limit (");
			message.append(aLowerLimit);
			message.append(") must be lower than Upper limit (");
			message.append(aUpperLimit);
			message.append(")");
			throw new IllegalArgumentException(message.toString());
		}

		if (generator == null) {
			generator = new Random();
		}
		// get the range, casting to long to avoid overflow problems
		long range = (long) aUpperLimit - (long) aLowerLimit + 1;
		// compute a fraction of the range, 0 <= frac < range
		long fraction = (long) (range * generator.nextDouble());
		return (int) (fraction + aLowerLimit);
	}

	/**
	 * This method will return a command that can be fed into wsadmin
	 * for changing the starting weight of a module within an
	 * application.
	 * 
	 * @param appName
	 *            The name of the application
	 * @param moduleUri
	 *            The name of the module within the application
	 * @param weight
	 *            The new starting weight for the module
	 * @param iServerProcess
	 *            The iServerProcess to run this command
	 */
	public static String getStartingWeightChgCmd(String appName, String moduleUri, String weight,
			Server iServerProcess) throws Exception {

		return modifyAppDeployment(appName, moduleUri, "{{startingWeight " + weight + " }}",
				iServerProcess);
	}

	/**
	 * This method will return a java.library.path with the path
	 * passed to the method prepended to the existing
	 * java.library.path.
	 * 
	 * @param path
	 *            The path to prepend to the java.library.path
	 * @return A java.library.path with passed in path prepended
	 */
	public static String addToJavaLibraryPath(String path) {
		return path + File.pathSeparator + System.getProperty("java.library.path");
	}

	/**
	 * This method is mainly just for z/OS. z/OS requires additional
	 * system properties added to a java execution.
	 * 
	 * @param cmd
	 *            The existing java command
	 * @param iServerProcess
	 *            The iServerProcess to run this command
	 * @return The java command with additional system properties
	 */
	public static StringBuffer addGlobalSysPropsToCmd(StringBuffer cmd,
			Server iServerProcess) throws Exception {
		Machine localMachine = Machine.getLocalMachine();
		Machine iServerMachine = iServerProcess.getNode().getMachine();
		if (localMachine.getOperatingSystem() == OperatingSystem.ZOS) {
			cmd.append("-Djava.library.path="
					+ addToJavaLibraryPath(iServerProcess.getNode().getWASInstall().getInstallRoot()
							+ iServerMachine.getOperatingSystem().getFileSeparator() + "lib") + " -Dfile.encoding="
					+ AppConst.DEFAULT_ENCODING + " -Xnoargsconversion ");

		}
		cmd.append("-Djava.util.logging.manager=com.ibm.ws.bootstrap.WsLogManager "
				+ "-Djava.util.logging.configureByServer=true ");
		return cmd;
	}

	/**
	 * This method will write a file to the local machine using the
	 * specified file name, string, and DEFAULT encoding.
	 * 
	 * @param fileName
	 *            The name of the file to be written
	 * @param content
	 *            The contents of the file to be written
	 * @throws Exception
	 *             Any kind of exception
	 */
	public static void writeFile(String fileName, String content) throws Exception {
		writeFile(fileName, content, AppConst.DEFAULT_ENCODING);
	}

	/**
	 * This method will write a file to the local machine using the
	 * specified file name, string, and encoding.
	 * 
	 * @param fileName
	 *            The name of the file to be written
	 * @param content
	 *            The contents of the file to be written
	 * @param encoding
	 *            The encoding of the file that will be written
	 * @throws Exception
	 *             Any kind of exception
	 */
	public static void writeFile(String fileName, String content, String encoding) throws Exception {

		FileOutputStream fos = new FileOutputStream(fileName);
		OutputStreamWriter osw = new OutputStreamWriter(fos, encoding);
		PrintWriter out = new PrintWriter(osw);

		out.print(content);

		out.close();
		osw.close();
		fos.close();

	}

	/**
	 * This method returns the contents of a text file in String form.
	 * 
	 * @param file
	 *            The file containing lots of data
	 * @param encoding
	 *            The encoding of the file
	 * @return The contents of the file
	 * @throws IOException
	 *             Any kind I/O error
	 */
	public static String readFile(String file, String encoding) throws IOException {

		Reader reader = null;
		String contents = null;
		File src = new File(file);

		try {
			reader = new InputStreamReader(new FileInputStream(src), encoding);

			BufferedReader br = new BufferedReader(reader);

			int fileLengthInBytes = (int) src.length();
			StringBuffer tmpBuf = new StringBuffer(fileLengthInBytes);
			int readChar = 0;

			while (true) {
				readChar = br.read();
				if (readChar < 0) {
					break;
				}
				tmpBuf.append((char) readChar);
			}

			contents = tmpBuf.toString();

			br.close();
			reader = null;

		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
				}
			}
		}

		return contents;

	}

	/**
	 * see if we can get a good connection to a url. return true if we
	 * can.
	 * 
	 * @param url
	 * @param timeoutSec
	 * @return true if we got a response code between 200 and 300 for
	 *         a GET request. redirects are followed and in that case
	 *         we get the final response code.
	 */
	public static boolean getUrlStatus(String url, int timeoutSec) {
		return (fetchurl(url, timeoutSec, true).compareTo("true") == 0);
	}

	/**
	 * retrieve the contents of a url. Follow redirects if needed to
	 * do it.
	 * 
	 * @param url
	 * @param timeoutSec -
	 *            timeout to establish initial connection
	 * @return the contents - whatever it could get in event of a
	 *         timeout
	 */
	public static String getUrlContents(String url, int timeoutSec) {
		return (fetchurl(url, timeoutSec, false));
	}

	/**
	 * see if a url can be reached and the contents retrieved.
	 * 
	 * @param url
	 * @param timeoutSec -
	 *            timeout to establish connect and, if requested, get
	 *            contents.
	 * @param ignoreContents -
	 *            just check the connection. Don't bother retreiving
	 *            contents
	 * @return the contents - whatever it could get in the event of a
	 *         timeout.
     *         
     * Sometimes the http timeouts don't work as advertised, and this method was 
     * hanging, so it was redone to do all the http work inside a separate thread,
     * so no matter what happens, at the end of the timeout we can kill the thread. 
     * 
     * TODO: clean up the inner class code since all the timeouts are in the thread now anyway.
	 */
	private static String fetchurl(String url, int timeoutSec, boolean ignoreContents) {          
       
        // define a  method level inner class we can run in a separate thread.
        // the variables in it will get set with the method args.
        class urlFetcher implements Runnable{
            public String  url = null;
            public int timeoutSec = 30;
            public boolean ignoreContents = false; 
            public String result = null;
                
            public void run(){
                URL u = null;
                HttpURLConnection h = null;
                int responseCode = 0;
                boolean gotresponse = false;
                byte[] buf = null;
                try {
                    u = new URL(url);
                } catch (MalformedURLException e) {
                    throw new RuntimeException("malformed url");
                }
                int i = 0;
                System.out.println("trying: " + url);
                long initialTime = java.lang.System.currentTimeMillis();
                long endtime = initialTime + (1000 * timeoutSec);
                do {
                    try {
                        h = (HttpURLConnection) u.openConnection();
                        h.setRequestMethod("GET");
                        h.setConnectTimeout((int)(endtime - System.currentTimeMillis()));
                        h.connect();
                        responseCode = h.getResponseCode();
                    } catch (IOException e) {
                        i++;
                        // System.out.println("waiting for server: "+url);
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException x) {
                        }
                        continue;
                    }
                    gotresponse = true;
                    break;

                } while (System.currentTimeMillis() < endtime);

                if (gotresponse) {
                    if (responseCode < 200 || responseCode > 300) {
                        System.out.println("got error response: " + responseCode);
                        gotresponse = false;
                    } else {
                        System.out.println("got response:" + responseCode);
                    }

                } else {
                    System.out.println("no response, timed out connecting to url: " + url);
                }

                if (ignoreContents) {
                    h.disconnect();
                    if (gotresponse){
                        result = "true";
                        return;
                    }else{
                        result = "false";
                        return ;
                    }    
                }

                // otherwise, go get the contents
                try {
                    InputStream is = h.getInputStream();
                    // TODO: smarter buffering
                    int bufsize = 320000;
                    buf = new byte[bufsize];
                    int bytesread = 0;
                    int totalbytesread = 0;         
                    do {
                       bytesread = is.read(buf, totalbytesread, bufsize - totalbytesread );
                       totalbytesread += bytesread;
                    } while (bytesread > -1 && totalbytesread < bufsize -1);
                    if (bytesread == -1 ) { totalbytesread++; }
                    
                    h.disconnect();
                    
                    // convert byte[] to string this way:
                    result= new String(buf, 0, totalbytesread);
                    return;
                } catch (IOException e) {
                    result = "io exception - probably socket timeout";
                    return;
                }
            }
        }  // end inner class
        
        // set up the class
        urlFetcher uf = new urlFetcher();
        uf.ignoreContents = ignoreContents;
        uf.url = url;
        uf.timeoutSec = timeoutSec;
        
        // fire if off in a second thread.
        
        Thread t = new Thread(uf);
        t.start();
        
        long initialTime = java.lang.System.currentTimeMillis();
        long endtime = initialTime + (1000 * timeoutSec);
        while(java.lang.System.currentTimeMillis() < endtime){
            if (uf.result !=null){                
                return uf.result;
            }            
           try{ Thread.sleep(1000);} catch(InterruptedException e){} 
        }
        t.interrupt();
        return "timed out, thread killed";
        
        
	}
}
