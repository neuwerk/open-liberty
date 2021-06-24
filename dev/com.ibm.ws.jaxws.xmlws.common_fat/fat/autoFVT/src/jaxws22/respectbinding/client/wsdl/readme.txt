These wsdls will be used to test the client.

The client tests won't actually invoke the service, so
we'll put an unreachable host and port (localhost, 56789 ) in the wsdl.

The wsdls need to all be alike EXCEPT for the extensibility elements.

The name of each wsdl file defines how the extensibility elements
are to be configured on the binding. 

So generate once from server/vlaidateRequiredAnnoTrue/Echo.java,
then just copy and edit.

Since we don't have any valid, required ex. elements yet,
those wsdls can just be ordinary for now.

