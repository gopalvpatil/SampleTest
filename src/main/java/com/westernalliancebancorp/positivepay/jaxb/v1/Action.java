
package com.westernalliancebancorp.positivepay.jaxb.v1;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
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
 *       &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="isAdminAction" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="description" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="targetStatusName" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="paymentStatus" use="optional" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="preExecutionCallback" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="postExecutionCallback" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "action", namespace = "http://v1.jaxb.positivepay.westernalliancebancorp.com")
public class Action {

    @XmlAttribute(name = "name", required = true)
    protected String name;
    @XmlAttribute(name = "isAdminAction")
    protected Boolean isAdminAction;
    @XmlAttribute(name = "isPresentable")
    protected Boolean isPresentable;
    @XmlAttribute(name = "description", required = true)
    protected String description;
    @XmlAttribute(name = "targetStatusName", required = true)
    protected String targetStatusName;
    @XmlAttribute(name = "paymentStatus", required = true)
    protected String paymentStatus;
    @XmlAttribute(name = "preExecutionCallback")
    protected String preExecutionCallback;
    @XmlAttribute(name = "postExecutionCallback")
    protected String postExecutionCallback;

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the isAdminAction property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isIsAdminAction() {
        return isAdminAction;
    }

    /**
     * Sets the value of the isAdminAction property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIsPresentable(Boolean value) {
        this.isPresentable = value;
    }

    /**
     * Gets the value of the isAdminAction property.
     *
     * @return
     *     possible object is
     *     {@link Boolean }
     *
     */
    public Boolean isIsPresentable() {
        return isPresentable;
    }

    /**
     * Sets the value of the isAdminAction property.
     *
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *
     */
    public void setIsAdminAction(Boolean value) {
        this.isAdminAction = value;
    }

    /**
     * Gets the value of the description property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the value of the description property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescription(String value) {
        this.description = value;
    }

    /**
     * Gets the value of the targetStatusName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTargetStatusName() {
        return targetStatusName;
    }

    /**
     * Sets the value of the targetStatusName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTargetStatusName(String value) {
        this.targetStatusName = value;
    }

    /**
     * Gets the value of the paymentStatus property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getPaymentStatus() {
        return paymentStatus;
    }

    /**
     * Sets the value of the paymentStatus property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setPaymentStatus(String value) {
        this.paymentStatus = value;
    }

    /**
     * Gets the value of the preExecutionCallback property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPreExecutionCallback() {
        return preExecutionCallback;
    }

    /**
     * Sets the value of the preExecutionCallback property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPreExecutionCallback(String value) {
        this.preExecutionCallback = value;
    }

    /**
     * Gets the value of the postExecutionCallback property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPostExecutionCallback() {
        return postExecutionCallback;
    }

    /**
     * Sets the value of the postExecutionCallback property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPostExecutionCallback(String value) {
        this.postExecutionCallback = value;
    }

}
