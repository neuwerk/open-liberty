#
#  Description: This file tunes the server for the Web services tests.
#

import  java.lang.System  as  sys
import  java.lang.String  as  str
lineSep = sys.getProperty('line.separator')

cell = AdminConfig.list('Cell')
cellName = AdminConfig.showAttribute(cell, 'name')
nodes = AdminConfig.list('Node', cell).split(lineSep)

for node in nodes:
    nodeName = AdminConfig.showAttribute(node, 'name')
    servers = AdminConfig.list('Server', node).split(lineSep)
    for server in servers:
        serverName = AdminConfig.showAttribute(server, 'name')
        serv = AdminConfig.getid('/Cell:' + cellName + '/Node:' + nodeName + '/Server:' + serverName + '/')
        jvms = AdminConfig.list('JavaVirtualMachine', serv).split(lineSep)
        jpds = AdminConfig.list('JavaProcessDef', serv).split(lineSep)
        osname = sys.getProperty('os.name')
        print 'osname is %s' % (osname)

        if(osname == 'z/OS'):
            for jpd in jpds :
                jscmd=AdminConfig.showAttribute(jpd, 'startCommandArgs') 
                jpdName = AdminConfig.showAttribute(jpd,'processType')      
                jvmps = AdminConfig.list('JavaVirtualMachine', jpd).split(lineSep)
                if( jpdName != 'Control' ):
                    if( jpdName == 'Servant' ):
                        # We'll just set this property on all JVMs
                        for jvm in jvmps:
                            print 'jvm in zOS JavaProcessDef %s is %s' % (jpdName,jvm)
                            jvmArgs = AdminConfig.showAttribute(jvm, 'genericJvmArguments')
                            print 'jvmargs before change is %s' % (jvmArgs)
                            # delete the property we setn with the enable script.
                            jvmArgs = jvmArgs.replace('-Djaxws.runtime.legacyWebMethod=true', '')
                            print 'jvmargs after change is %s' % (jvmArgs)
                            AdminConfig.modify(jvm, [['genericJvmArguments', jvmArgs]])
                        #end-for-jvm
            #end-for-jpds
        # end if-Zos    
        else:
            # We'll just set this property on all JVMs
            for jvm in jvms:
                print 'jvm is %s' % (jvm)
                jvmArgs = AdminConfig.showAttribute(jvm, 'genericJvmArguments')
                print 'jvmargs before change is %s' % (jvmArgs)
                jvmArgs = jvmArgs.replace('-Djaxws.runtime.legacyWebMethod=true', '')
                print 'jvmargs after change is %s' % (jvmArgs)
                AdminConfig.modify(jvm, [['genericJvmArguments', jvmArgs]])
            # end-for-jvm
        #end-else-zos
    #end-for-server
#end-for-node

AdminConfig.save()
print 'saved config'
