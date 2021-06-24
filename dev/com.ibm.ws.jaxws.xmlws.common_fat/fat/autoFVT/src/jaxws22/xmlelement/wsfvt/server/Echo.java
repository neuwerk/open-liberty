package jaxws22.xmlelement.wsfvt.server;

import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.ParameterStyle;
import javax.jws.soap.SOAPBinding.Style;
import javax.xml.bind.annotation.XmlElement;

@WebService(name="EchoService", targetNamespace="http:/xmlelement/jaxws22")
@SOAPBinding(style=Style.DOCUMENT, parameterStyle=ParameterStyle.WRAPPED)
public class Echo { 
     
/*
 * @XmlElement(nillable=.., required=..) is placed in two places to customize both the request and response wrapper beans
 *  e.g.,
 *  @XmlElement(nillable=true, required=true)   // to customize the method & response bean
 *  public String stringNillTrueRequiredTrue(@XmlElement(nillable=true, required=true) String name){    // to customize the fields and request bean field
 *    ...}
 * */ 
  
  @XmlElement(nillable=true, required=true)
  public String myname;
  
    //no annotation
    public String noAnno(String name){     
        return name;
    }

    /*
     * input = null for type = String     
     */
    
    
    //this checks @xmlElement nillable=true and required=true    
    @XmlElement(nillable=true, required=true)
    public String stringNillTrueRequiredTrue(@XmlElement(nillable=true, required=true)String name){
        return name;
    }

    //this checks @xmlElement nillable=false and required=false    
    @XmlElement(nillable=false, required=false)
    public String stringNillFalseRequiredFalse(@XmlElement(nillable=true, required=true)String name){        
        return name;
    }

    //this checks @xmlElement nillable=true and required=false   
    @XmlElement(nillable=true, required=false)
    public String stringNillTrueRequiredFalse(@XmlElement(nillable=true, required=true)String name){
        return name;
    }

    //this checks @xmlElement nillable=false and required=true    
    @XmlElement(nillable=false, required=true)
    public String stringNillFalseRequiredTrue(@XmlElement(nillable=true, required=true)String name){
        return name;
    }

    /*
     * input = null for a primitive type
     */
    
    @XmlElement(nillable=false, required=true)
    public Float floatNillFalseRequiredTrue(@XmlElement(nillable=true, required=true)Float num){
        return num;
    }

    @XmlElement(nillable=true, required=true)
    public Float floatNillTrueRequiredTrue(@XmlElement(nillable=true, required=true)Float num){
        return num;
    }
    
    @XmlElement(nillable=true, required=false)
    public Float floatNillTrueRequiredFalse(@XmlElement(nillable=true, required=true)Float num){
        return num;
    }

    @XmlElement(nillable=false, required=false)
    public Float floatNillFalseRequiredFalse(@XmlElement(nillable=true, required=true)Float num){
        return num;
    }
    
    /*
     * spot check on annotation on method only
     */
     @XmlElement(nillable=true, required=true)
     public Float annoOnMethodOnly(Float num){
        return num;
     }
     
     /*
      * spot check on annotation on field only
      */
    
     public Float annoOnFieldOnly(@XmlElement(nillable=true, required=true)Float num){
        return num;
     }




    /*
     *  with WebParam name at the field
     */
    
    public Float changeName(@WebParam(name="floatNewName") Float num){
        return num;
    }       

}
