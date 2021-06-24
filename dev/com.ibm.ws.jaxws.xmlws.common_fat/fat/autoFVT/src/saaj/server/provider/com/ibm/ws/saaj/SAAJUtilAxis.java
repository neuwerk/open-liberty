/*
 * Copyright 2006 International Business Machines Corp.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package com.ibm.ws.saaj;

import java.io.*;
import java.util.*;

// SAAJ SOAPMessage related
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.Name;
import javax.xml.soap.Node;
import javax.xml.soap.Text;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPPart;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.MimeHeader;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMText;
import javax.xml.namespace.QName;

/**
 * SAAJBase
 * 
 * The utility method common to SAAJ FVT Server and Client
 *
 */

public class SAAJUtilAxis extends SAAJUtil12{

    public static boolean isEqual13Elem( OMElement elem1, SOAPElement elem2 ) {
        return isEqual12Elem( elem1, elem2 );
    }

    public static boolean isEqual12Elem( OMElement elem1, SOAPElement elem2 ) {
        if( elem1 == null && elem2 == null ) return true;
        if( elem1 == null || elem2 == null ){
            System.out.println( "One of the element is null" );  // dbg 
            return false; // unless both are null
        }

        try{
            System.out.println( "Compare element '" + elem1.toString() + "' && '" +  // dbg 
                                elem2.toString() + "'" );  // dbg 
            QName name1 = elem1.getQName();
            QName name2 = elem2.getElementQName();
            if( !isEqual12Name( name1, name2 ) ) {
                System.out.println( "Name not equal" );  // dbg 
                return false;
            }
            Iterator<QName> iterAttr1 = elem1.getAllAttributes();
            Iterator<Name>  iterAttr2 = elem2.getAllAttributes();
            if( !isEqual12Attributes( iterAttr1, iterAttr2, elem1, elem2 ) ){
                System.out.println( "Attributess not equal" );  // dbg 
                return false;
            }
            Iterator<OMElement>   iterKids1 = elem1.getChildElements();
            Iterator<SOAPElement> iterKids2 = elem2.getChildElements();
            if( !isEqual12ElementsForAxis( iterKids1, iterKids2 ) ) {
                System.out.println( "Elements not equal" );  // dbg 
                return false;
            }
            String strValue1 = elem1.getText();
            String strValue2 = elem2.getValue();
            return isEqualStr( strValue1, strValue2 );
        } catch( Exception e ){
            e.printStackTrace(System.out);
            return false;
        }
    }

    public static boolean isEqual12Name( QName name1, Name name2 ) {
        if( name1 == null && name2 == null ) return true;
        if( name1 == null || name2 == null ){
            System.out.println( "One of the name is null" );  // dbg 
            return false; // unless both are null
        }

        try{
            System.out.println( "Compare name '" + name1.toString() + "' && '" +  // dbg 
                                name2.toString() + "'" );  // dbg 
            String strPrefix1 = name1.getPrefix(); 
            String strLocal1  = name1.getLocalPart();
            String strQname1  = name1.toString();
            String strUri1    = name1.getNamespaceURI();

            String strPrefix2 = name2.getPrefix(); 
            String strLocal2  = name2.getLocalName();
            String strQname2  = name2.getQualifiedName();
            String strUri2    = name2.getURI();
            if( strPrefix1.indexOf( "axis2ns" ) >= 0 ||
                strPrefix2.indexOf( "axis2ns" ) >= 0  ){
                System.out.println( "axis2ns temporary prefix found. Skip it" );  // dbg 
                strPrefix1 = strPrefix2;
                strQname1  = strPrefix2 + ":" + strLocal1;
            }

            if ( //isEqualStr( strPrefix1, strPrefix2 ) &&
                 isEqualStr( strLocal1 , strLocal2  ) &&
                 //isEqualStr( strQname1 , strQname2  ) &&
                 isEqualStr( strUri1   , strUri2    ) ){
                return true;
            } else {
                System.out.println( "Name not Equals: '" +  // dbg 
                                    strQname1 + "' <> '" +  // dbg 
                                    strQname2 + "'"  // dbg 
                                    );  // dbg 
                return false;
            }
        } catch( Exception e ){
            e.printStackTrace(System.out);
            return false;
        }
    }

    public static boolean isEqual12Name( QName name1, QName name2 ) {
        if( name1 == null && name2 == null ) return true;
        if( name1 == null || name2 == null ){
            System.out.println( "One of the name is null" );  // dbg 
            return false; // unless both are null
        }

        try{
            System.out.println( "Compare name '" + name1.toString() + "' && '" +  // dbg 
                                name2.toString() + "'" );  // dbg 
            String strPrefix1 = name1.getPrefix(); 
            String strLocal1  = name1.getLocalPart();
            String strQname1  = name1.toString();
            String strUri1    = name1.getNamespaceURI();

            String strPrefix2 = name2.getPrefix(); 
            String strLocal2  = name2.getLocalPart();
            String strQname2  = name2.toString();
            String strUri2    = name2.getNamespaceURI();
            if( strPrefix1.indexOf( "axis2ns" ) >= 0 ||
                strPrefix2.indexOf( "axis2ns" ) >= 0  ){
                System.out.println( "axis2ns temporary prefix found. Skip it" );  // dbg 
                strPrefix1 = strPrefix2;
                strQname1  = strPrefix2 + ":" + strLocal1;
            }

            if ( //isEqualStr( strPrefix1, strPrefix2 ) &&
                 isEqualStr( strLocal1 , strLocal2  ) &&
                 //isEqualStr( strQname1 , strQname2  ) &&
                 isEqualStr( strUri1   , strUri2    ) ){
                return true;
            } else {
                System.out.println( "Name not Equals: '" +  // dbg 
                                    strQname1 + "' <> '" +  // dbg 
                                    strQname2 + "'"  // dbg 
                                    );  // dbg 
                return false;
            }
        } catch( Exception e ){
            e.printStackTrace(System.out);
            return false;
        }
    }

    public static boolean isEqual12Attributes( Iterator<QName> iter1, 
                                               Iterator<Name>  iter2,
                                               OMElement       elem1,
                                               SOAPElement     elem2
                                               ) {
        if( iter1 == null && iter2 == null ) return true;
        if( iter1 == null || iter2 == null ) {
            System.out.println( "one of the attributes is null" );  // dbg 
            return false; // unless both are null
        }

        try{
            System.out.println( "Compare attributes" );  // dbg 
            // translate the Name Iterator to Hashtable
            List list1 = new Vector();
            while(iter1.hasNext()) {
                QName name =iter1.next();
                if( !name.getPrefix()   .equals( "xmlns" ) &&
                    !name.getLocalPart().equals( "xmlns" ) 
                  ){ 
                    // We do not compare NameSpace
                    // NameSpace is active only when it define some name
                    list1.add( name );
                }
            }
            List list2 = new Vector();
            while(iter2.hasNext()) {
                Name name =iter2.next();
                if( !name.getPrefix()   .equals( "xmlns" ) &&
                    !name.getLocalName().equals( "xmlns" ) 
                ) { 
                    // We do not compare NameSpace definitions
                    // NameSpace is active only when it define some name
                    list2.add( name );
                }
            }
            int iSize = list1.size();
            if( iSize != list2.size() ) 
            {
                System.out.println( "Size does not match " + iSize + "<>" +   // dbg 
                                    list2.size() );  // dbg 
                return false;
            }
            for( int iI = 0; iI < iSize; iI ++ ){
                int iSize2 = list2.size();
                int iJ = 0;
                for( ; iJ < iSize2; iJ ++ ){
                    QName name1 = (QName)list1.get(iI);
                    Name  name2 = (Name)list2.get(iJ);
                    if( isEqual12Name( name1, name2) ){
                        String strValue1 = elem1.getAttributeValue( name1 );
                        String strValue2 = elem2.getAttributeValue( name2 );
                        if( strValue1.equals( strValue2 ) ){
                            list2.remove( iJ );
                            break;
                        }
                    }
                }
                if( iJ >= iSize2 ) {
                    System.out.println( "Name not found " + iJ + " vs size" +   // dbg 
                                        iSize2  );  // dbg 
                    return false; // can not find a match Name
                }
            }
        } catch( Exception e ){
            e.printStackTrace(System.out);
            return false;
        }
        return true;
    }

   
    public static boolean isEqual12ElementsForAxis( Iterator<OMElement> iter1, Iterator<SOAPElement> iter2 ) {
        if( iter1 == null && iter2 == null ) return true;
        if( iter1 == null || iter2 == null ){
            System.out.println( "one of the elemnts is null" );  // dbg 
            return false; // unless both are null
        }

        try{
            System.out.println( "Compare elements" );  // dbg 
            // translate the Name Iterator to Hashtable
            List list1 = new Vector<OMElement>();
            while(iter1.hasNext()) {
                Object obj1 = iter1.next();
                System.out.println( "Object1 : '" + obj1.toString() + "'" );  // dbg 
                if( !(obj1 instanceof OMText )){
                    list1.add( obj1 );
                }
            }
            List list2 = new Vector<SOAPElement>();
            while(iter2.hasNext()) {
                Object obj2 = iter2.next();
                System.out.println( "Object2 : '" + obj2.toString() + "'" );  // dbg 
                if( !(obj2 instanceof Text )){
                    list2.add( obj2 );
                }
            }
            int iSize = list1.size();
            if( iSize != list2.size() ){
                System.out.println( "Size does not match " + iSize + "<>" +   // dbg 
                                    list2.size() );  // dbg 
                return false;
            }
            for( int iI = 0; iI < iSize; iI ++ ){
                int iSize2 = list2.size();
                int iJ = 0;
                for( ; iJ < iSize2; iJ ++ ){
                    Object obj1 = list1.get(iI);
                    Object obj2 = list2.get(iJ);
                    if( obj1 instanceof OMElement  &&
                        obj2 instanceof SOAPElement  &&
                        isEqual12Elem( (OMElement)obj1, (SOAPElement)obj2 ) ){
                        list2.remove( iJ );
                        break;
                    }
                    if( obj1 instanceof OMText &&
                        obj2 instanceof Text &&
                        isEqualStr( ((OMText)obj1).getText(), ((Text)obj2).getValue() ) ){
                        list2.remove( iJ );
                        break;
                    }
                    if( obj1 instanceof String &&
                        obj2 instanceof String &&
                        isEqualStr( (String)obj1, (String)obj2 ) ){
                        list2.remove( iJ );
                        break;
                    }
                }
                if( iJ >= iSize2 ) {
                    System.out.println( "Elemenet not found " + iJ + " vs size " +   // dbg 
                                        iSize2 );  // dbg 
                    return false; // can not find a match Name
                }
            }
        } catch( Exception e ){
            e.printStackTrace(System.out);
            return false;
        }
        return true;
    }


}
