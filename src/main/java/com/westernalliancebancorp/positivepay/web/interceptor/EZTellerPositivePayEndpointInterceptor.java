package com.westernalliancebancorp.positivepay.web.interceptor;

import org.springframework.ws.context.MessageContext;
import org.springframework.ws.soap.SoapHeaderElement;
import org.springframework.ws.soap.SoapMessage;
import org.springframework.ws.soap.server.SoapEndpointInterceptor;


public class EZTellerPositivePayEndpointInterceptor implements SoapEndpointInterceptor{
    
public boolean handleResponse(MessageContext msgContext, Object ep) 
 throws Exception {
    SoapMessage soapRequest = (SoapMessage) msgContext.getRequest();
    String soapAction = soapRequest.getSoapAction();
    SoapMessage soapResponse = (SoapMessage) msgContext.getResponse();
    soapResponse.setSoapAction(soapAction);
    return true;
 }

@Override
public boolean handleFault(MessageContext arg0, Object arg1) throws Exception {
    return true;
}

@Override
public boolean handleRequest(MessageContext mc, Object arg1) throws Exception {
    return true;
}

@Override
public boolean understands(SoapHeaderElement arg0) {
    return true;
}
 

}
