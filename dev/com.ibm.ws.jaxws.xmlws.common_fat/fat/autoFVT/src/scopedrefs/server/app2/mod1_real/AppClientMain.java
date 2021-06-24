package scopedrefs.server.app2.mod1;

/**
 * app2 is repacked as an application client app. 
 * This class provides the main method used by the application client. 
 * @author btiffany
 *
 */
public class AppClientMain  extends scopedrefs.server.app2.common.HelperMethods  {
    public static void main (String [] args) throws Exception {
        if(args.length == 0){
            System.out.println("******* AppclientMain.main is starting *********");
            System.out.println("Error: specify a JNDI reference to look up.");
            
        } else {
            AppClientMain me = new AppClientMain();
            String jndiref = args[0];       
            System.out.println("looking up and invoking: "+jndiref);
            System.out.println( "Appclient response: " + me.lookupAndInvokeAnyJndiRef(jndiref));
        }
        System.out.println("******* AppclientMain.main is ending *********");
    }
}
