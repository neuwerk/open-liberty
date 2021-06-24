F743-23362

In buildTest.xml and install.xml

- Keystore files are copied to dmgr and app server's ws-security/samples directory.  The Keystore files originally are from <WAS.base.dir>/etc/ws-security/samples 
        
- User defined policy set is copied to server's config\cells\cellname\PolicySets directory.

- Application binding with custom properties
   - packaged in the server app ear
   - copied to directory installableApps, same location of client ear, as it's needed by launchClient