
package com.harlandfs.ezteller.nativepositivepay;

import java.math.BigDecimal;
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
 *         &lt;element name="strEmployeeFirst" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="strEmployeeLast" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="nBankID" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="nBranchID" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="strStationID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="nTellerID" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="strAccountNumber" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="strCheckNumber" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="dCheckAmount" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
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
    "strEmployeeFirst",
    "strEmployeeLast",
    "nBankID",
    "nBranchID",
    "strStationID",
    "nTellerID",
    "strAccountNumber",
    "strCheckNumber",
    "dCheckAmount"
})
@XmlRootElement(name = "ReverseOnUsCheck")
public class ReverseOnUsCheck {

    protected String strEmployeeFirst;
    protected String strEmployeeLast;
    protected int nBankID;
    protected int nBranchID;
    protected String strStationID;
    protected int nTellerID;
    @XmlElement(required = true)
    protected String strAccountNumber;
    @XmlElement(required = true)
    protected String strCheckNumber;
    @XmlElement(required = true)
    protected BigDecimal dCheckAmount;

    /**
     * Gets the value of the strEmployeeFirst property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStrEmployeeFirst() {
        return strEmployeeFirst;
    }

    /**
     * Sets the value of the strEmployeeFirst property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStrEmployeeFirst(String value) {
        this.strEmployeeFirst = value;
    }

    /**
     * Gets the value of the strEmployeeLast property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStrEmployeeLast() {
        return strEmployeeLast;
    }

    /**
     * Sets the value of the strEmployeeLast property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStrEmployeeLast(String value) {
        this.strEmployeeLast = value;
    }

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

    /**
     * Gets the value of the nBranchID property.
     * 
     */
    public int getNBranchID() {
        return nBranchID;
    }

    /**
     * Sets the value of the nBranchID property.
     * 
     */
    public void setNBranchID(int value) {
        this.nBranchID = value;
    }

    /**
     * Gets the value of the strStationID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStrStationID() {
        return strStationID;
    }

    /**
     * Sets the value of the strStationID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStrStationID(String value) {
        this.strStationID = value;
    }

    /**
     * Gets the value of the nTellerID property.
     * 
     */
    public int getNTellerID() {
        return nTellerID;
    }

    /**
     * Sets the value of the nTellerID property.
     * 
     */
    public void setNTellerID(int value) {
        this.nTellerID = value;
    }

    /**
     * Gets the value of the strAccountNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStrAccountNumber() {
        return strAccountNumber;
    }

    /**
     * Sets the value of the strAccountNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStrAccountNumber(String value) {
        this.strAccountNumber = value;
    }

    /**
     * Gets the value of the strCheckNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStrCheckNumber() {
        return strCheckNumber;
    }

    /**
     * Sets the value of the strCheckNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStrCheckNumber(String value) {
        this.strCheckNumber = value;
    }

    /**
     * Gets the value of the dCheckAmount property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getDCheckAmount() {
        return dCheckAmount;
    }

    /**
     * Sets the value of the dCheckAmount property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setDCheckAmount(BigDecimal value) {
        this.dCheckAmount = value;
    }

}
