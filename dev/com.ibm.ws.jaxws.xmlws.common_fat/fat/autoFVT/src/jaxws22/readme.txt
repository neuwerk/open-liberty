
Test design notes:

To spread coverage around the various containers without a lot of duplicate testing, the
following approach was used:
    MTOM, Service Side:  		servlet container
    MTOM jsr109v13 Client side: launchclient 
    MTOM Client side:           unmanaged.
    
    Addressing , Service Side: 		  ejb container
    Addressing jsr109v13 Client side: war container
    
    RespectBinding Service Side:      servlet container
    RespectBinding jsr109v13 Client:  ejb container
    
Since the code path for the metadata to get picked up by the container during deployment 
is the same for all containers, this was simpler than matrixing all 3 annotations 
across all containers.     
