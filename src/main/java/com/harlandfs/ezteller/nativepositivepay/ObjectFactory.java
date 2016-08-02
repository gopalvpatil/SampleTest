
package com.harlandfs.ezteller.nativepositivepay;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.harlandfs.ezteller.nativepositivepay package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _ReturnResult_QNAME = new QName("http://www.harlandfs.com/EZTeller/NativePositivePay", "ReturnResult");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.harlandfs.ezteller.nativepositivepay
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link ApproveOnUsCheck }
     * 
     */
    public ApproveOnUsCheck createApproveOnUsCheck() {
        return new ApproveOnUsCheck();
    }

    /**
     * Create an instance of {@link ReverseOnUsCheckResponse }
     * 
     */
    public ReverseOnUsCheckResponse createReverseOnUsCheckResponse() {
        return new ReverseOnUsCheckResponse();
    }

    /**
     * Create an instance of {@link ApproveOnUsCheckWithOverride }
     * 
     */
    public ApproveOnUsCheckWithOverride createApproveOnUsCheckWithOverride() {
        return new ApproveOnUsCheckWithOverride();
    }

    /**
     * Create an instance of {@link IsSystemAvailableResponse }
     * 
     */
    public IsSystemAvailableResponse createIsSystemAvailableResponse() {
        return new IsSystemAvailableResponse();
    }

    /**
     * Create an instance of {@link ReverseOnUsCheck }
     * 
     */
    public ReverseOnUsCheck createReverseOnUsCheck() {
        return new ReverseOnUsCheck();
    }

    /**
     * Create an instance of {@link IsSystemAvailable }
     * 
     */
    public IsSystemAvailable createIsSystemAvailable() {
        return new IsSystemAvailable();
    }

    /**
     * Create an instance of {@link ApproveOnUsCheckResponse }
     * 
     */
    public ApproveOnUsCheckResponse createApproveOnUsCheckResponse() {
        return new ApproveOnUsCheckResponse();
    }

    /**
     * Create an instance of {@link ReturnResult }
     * 
     */
    public ReturnResult createReturnResult() {
        return new ReturnResult();
    }

    /**
     * Create an instance of {@link ApproveOnUsCheckWithOverrideResponse }
     * 
     */
    public ApproveOnUsCheckWithOverrideResponse createApproveOnUsCheckWithOverrideResponse() {
        return new ApproveOnUsCheckWithOverrideResponse();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ReturnResult }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.harlandfs.com/EZTeller/NativePositivePay", name = "ReturnResult")
    public JAXBElement<ReturnResult> createReturnResult(ReturnResult value) {
        return new JAXBElement<ReturnResult>(_ReturnResult_QNAME, ReturnResult.class, null, value);
    }

}
