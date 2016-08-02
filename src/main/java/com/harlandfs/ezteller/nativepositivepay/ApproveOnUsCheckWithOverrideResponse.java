
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
 *         &lt;element name="ApproveOnUsCheckWithOverrideResult" type="{http://www.harlandfs.com/EZTeller/NativePositivePay}ReturnResult"/>
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
    "approveOnUsCheckWithOverrideResult"
})
@XmlRootElement(name = "ApproveOnUsCheckWithOverrideResponse")
public class ApproveOnUsCheckWithOverrideResponse {

    @XmlElement(name = "ApproveOnUsCheckWithOverrideResult", required = true, nillable = true)
    protected ReturnResult approveOnUsCheckWithOverrideResult;

    /**
     * Gets the value of the approveOnUsCheckWithOverrideResult property.
     * 
     * @return
     *     possible object is
     *     {@link ReturnResult }
     *     
     */
    public ReturnResult getApproveOnUsCheckWithOverrideResult() {
        return approveOnUsCheckWithOverrideResult;
    }

    /**
     * Sets the value of the approveOnUsCheckWithOverrideResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link ReturnResult }
     *     
     */
    public void setApproveOnUsCheckWithOverrideResult(ReturnResult value) {
        this.approveOnUsCheckWithOverrideResult = value;
    }

}
