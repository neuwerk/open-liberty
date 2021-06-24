How to call tcpmon classes in 
       FVT\ws\code\websvcs.fvt\src\com\ibm\ws\wsfvt\build\tools\tcpmon :

For Example: changing http payload in "SOAPMessage"
  See an example in FVT\ws\code\websvcs.fvt\src\wssecfvt\audit\jaxws\wssecfvt\test\
   (test cases and Listener classes)

1) In client side: 
   Create a new class which implements  TCPMonEvenListener.java
   
   /**
    * Listener notification that event has been received
    * @param isInbound message direction, returnes true when receiving response
    * @param event byte content of the HTTP payload, http headers are removed
    * @return message to send. a null means send what you have
    */
   public String receiveEvent(boolean isInbound, String event);
   
   isInBound = true  ... Client side is receiving a responding SOAPMessage from AppServer 
   isInBound = false ... Client side is sending a SOAPMessage from client
   event     = is the whole SOAPMessage in a String

   The method ought to do this:
      Modify the SOAPMessage in the event string and make it a SOAPMessage we want
      Or verify the SOAPMessage as requested by the test cases

2) Get a instance of TCPMonitor by calling this method in TCPMonitor.java:
   public synchronized static TCPMonitor instance(int listenPort, String targetHost, int targetPort, 
                             TCPMonEvenListener listener);
   ** Put the newly created class (TCPMonEvenListener) into the parameter.                            

3) The original design was to call terminate() before change to a new Listener. 

   But from the runtime experience, the terminate() has some troubles to release the
   socket resource in time.
   So, follow the example in 
     FVT\ws\code\websvcs.fvt\src\wssecfvt\audit\jaxws\wssecfvt\test\
   or
     FVT\ws\code\websvcs.fvt\src\wssecfvt\audit\jaxws\ws2ws\wssecfvt\test\
   might be safer


