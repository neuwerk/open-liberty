# How to debug tWAS case locally
1. cd to com.ibm.ws.jaxws_fat folder
2. ant -f build-test.xml localbuild
3. update slice.properties and test cases as required, and set debug flag, etc.
4. ant -f build-test.xml jaxws_fat.compile
5. ant -f build-test.xml localrun
6. repeat 3~5 to do debugging


# How to play with jaxws_fat between full and lite mode
Since Full FAT mode is enabled for migrated tWAS case, These things need to pay 
attention to:
1. If to exclude a whole bucket, you need to update slice.properties as well as 
   TestBuild.xml to make lite and full mode sync.
2. If only excluding one test class file, just update build-test.xml to delete that 
   file in localbuild phase, the full and lite mode should be synced automatically.
3. For personal build wants to run full buckets, set fat.test.mode=full and 
   fat.buckets.to.run=com.ibm.ws.jaxws_fat
4. For personal build wants to run a specific bucket, just update slice.properties
   and ensure fat.test.mode=lite 
5. For the current Liberty build definitions, only "X Open Full FAT Build - Liberty 
   (No Personal Builds Please)" will run the full buckets of jaxws_fat
6. If you want to run SOE build for specific bucket, change the slice.properties 
   in your workspace first, then you need to set these properties in your personal 
   build:
     soe.platform.requests=SOE platform names separated by comma
     fat.buckets.to.run=com.ibm.ws.jaxws_fat
     fat.test.mode=lite
   Check http://was.pok.ibm.com/xwiki/bin/view/Build/SOETest for more platform names.