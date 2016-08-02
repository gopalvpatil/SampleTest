
package com.harlandfs.ezteller.nativepositivepay;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
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
 *         &lt;element name="nBankID" type="{http://www.w3.org/2001/XMLSchema}int"/>
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
    "nBankID"
})
@XmlRootElement(name = "IsSystemAvailable")
public class IsSystemAvailable {

    protected int nBankID;

    /**
     * Gets the value of the nBankID property.
     * 
     */
    public int getNBankID() {
        return nBankID;
    }

    /**
     * Sets the value of the nBankID property.
     * 
     */
    public void setNBankID(int value) {
        this.nBankID = value;
    }

}
