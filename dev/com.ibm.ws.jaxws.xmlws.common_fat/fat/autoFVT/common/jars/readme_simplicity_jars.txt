These simplicity jars come from the simplicity project. 
When they are updated there, they are NOT automatically updated here.

How to update WS FVT's simplicity:

- extract from CMVC:   WASX.TEST    TEST/ws/code/fat.sharedlib.simplicity/simplicity.zip
- unzip it
- 9 files that we need:

   - 5 jars:TEST\ws\code\fat.sharedlib.simplicity\simplicity\jars:
     provider.api.jar
     provider.jmx.jar
     provider.rxa.jar
     provider.wsadmin.jar
     public.api.jar

    - 1 jar from TEST\ws\code\fat.sharedlib.simplicity\simplicity\lib\jiiws:
	jiiws.jar
	
    - 3 jars from TEST\ws\code\fat.sharedlib.simplicity\simplicity\lib\rxa:
    jlanclient.jar
    remoteaccess.jar
    ssh.jar

- check back these 9 jars to CMVC for WAS?.FVT, to FVT\ws\code\websvcs.fvt\common\jars

When autowas is in use, the versions checked in to WAS?.FVT are updated by autowas in the local test tree. 

For more information: http://wasautomation.austin.ibm.com/xwiki/bin/view/Simplicity

http://wasautomation.austin.ibm.com/xwiki/bin/view/Simplicity/
