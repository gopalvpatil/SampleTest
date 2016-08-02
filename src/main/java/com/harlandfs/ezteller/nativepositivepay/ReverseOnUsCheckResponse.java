
package com.harlandfs.ezteller.nativepositivepay;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ReverseOnUsCheckResult" type="{http://www.harlandfs.com/EZTeller/NativePositivePay}ReturnResult"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "reverseOnUsCheckResult"
})
@XmlRootElement(name = "ReverseOnUsCheckResponse")
public class ReverseOnUsCheckResponse {

    @XmlElement(name = "ReverseOnUsCheckResult", required = true, nillable = true)
    protected ReturnResult reverseOnUsCheckResult;

    /**
     * Gets the value of the reverseOnUsCheckResult property.
     * 
     * @return
     *     possible object is
     *     {@link ReturnResult }
     *     
     */
    public ReturnResult getReverseOnUsCheckResult() {
        return reverseOnUsCheckResult;
    }

    /**
     * Sets the value of the reverseOnUsCheckResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link ReturnResult }
     *     
     */
    public void setReverseOnUsCheckResult(ReturnResult value) {
        this.reverseOnUsCheckResult = value;
    }

}
