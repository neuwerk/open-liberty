/**
 * This is a wsdl validator plugin used for the respectbinding tests.
 * Once installed (copy jar file to plugins dir, osgicfginit, recycle server)
 * wsdl binding extensions whose local namespace begins with "good" will be validated as supported.
 * 
 *  
 * 
 */
package com.ibm.wsfvt.wsdlvalidator;

import java.util.Iterator;
import java.util.Set;

import javax.wsdl.Definition;

import org.apache.axis2.jaxws.description.EndpointDescription;
import org.apache.axis2.jaxws.common.config.WSDLExtensionValidator;
import org.apache.axis2.jaxws.common.config.WSDLValidatorElement;

import com.ibm.ejs.ras.Tr;
import com.ibm.ejs.ras.TraceComponent;
import com.ibm.ws.websvcs.Constants;

public class WEVPlugin implements WSDLExtensionValidator, Constants {

    private static final TraceComponent _tc = Tr.register(WEVPlugin.class, TR_GROUP, TR_RESOURCE_BUNDLE);

    @Override
    public void validate(Set<WSDLValidatorElement> extensionSet, Definition wsdlDefinition, EndpointDescription endptDesc) {
        if (_tc.isEntryEnabled()) {
            Tr.entry(_tc, "validate");
        }

        // Print out the names of the elements that were passed in.
        if (_tc.isDebugEnabled()) {
            Tr.debug(_tc, "\tWSDLValidatorElements:");
        }
        for (Iterator<WSDLValidatorElement> i = extensionSet.iterator(); i.hasNext();) {
            WSDLValidatorElement e = i.next();
            if (_tc.isDebugEnabled()) {
                Tr.debug(_tc, "\t\t" + e.getExtensionElement().getElementType().toString());
            }

            // Retrieve the element's local part.
            String elementName = e.getExtensionElement().getElementType().getLocalPart();
            
            // If the local part starts with "good" then set the state to "SUPPORTED" so
            // we'll pass the validation.
            if (elementName.startsWith("good")) {
                e.setState(WSDLValidatorElement.State.SUPPORTED);
            }
        }
        
        if (_tc.isEntryEnabled()) {
            Tr.exit(_tc, "validate");
        }
    }
}
