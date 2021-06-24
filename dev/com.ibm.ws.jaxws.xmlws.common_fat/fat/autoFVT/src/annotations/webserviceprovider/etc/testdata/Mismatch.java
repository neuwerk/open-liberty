
/* this contains both types of annotations, illegal, should
 * not make it through wsgen 
 */
package test;
import javax.jws.*;
import javax.xml.ws.*;


@WebService
@WebServiceProvider
public class Mismatch{

    public void foo(){}
}
