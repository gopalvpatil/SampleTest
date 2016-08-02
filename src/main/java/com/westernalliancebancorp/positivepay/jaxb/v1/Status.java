
package com.westernalliancebancorp.positivepay.jaxb.v1;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
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
 *         &lt;element name="documentation" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element ref="{http://v1.jaxb.positivepay.westernalliancebancorp.com}action" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="description" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="onArrivalCallback" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="onDepartureCallback" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="isExceptionalStatus" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "documentation",
    "action"
})
@XmlRootElement(name = "status", namespace = "http://v1.jaxb.positivepay.westernalliancebancorp.com")
public class Status {

    protected String documentation;
    @XmlElement(namespace = "http://v1.jaxb.positivepay.westernalliancebancorp.com")
    protected List<Action> action;
    @XmlAttribute(name = "name", required = true)
    protected String name;
    @XmlAttribute(name = "description", required = true)
    protected String description;
    @XmlAttribute(name = "onArrivalCallback")
    protected String onArrivalCallback;
    @XmlAttribute(name = "onDepartureCallback")
    protected String onDepartureCallback;
    @XmlAttribute(name = "isExceptionalStatus")
    protected Boolean isExceptionalStatus;

    /**
     * Gets the value of the documentation property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDocumentation() {
        return documentation;
    }

    /**
     * Sets the value of the documentation property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDocumentation(String value) {
        this.documentation = value;
    }

    /**
     * Gets the value of the action property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the action property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAction().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Action }
     * 
     * 
     */
    public List<Action> getAction() {
        if (action == null) {
            action = new ArrayList<Action>();
        }
        return this.action;
    }

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
     * Gets the value of the onArrivalCallback property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOnArrivalCallback() {
        return onArrivalCallback;
    }

    /**
     * Sets the value of the onArrivalCallback property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOnArrivalCallback(String value) {
        this.onArrivalCallback = value;
    }

    /**
     * Gets the value of the onDepartureCallback property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOnDepartureCallback() {
        return onDepartureCallback;
    }

    /**
     * Sets the value of the onDepartureCallback property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOnDepartureCallback(String value) {
        this.onDepartureCallback = value;
    }

    /**
     * Gets the value of the isExceptionalStatus property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isIsExceptionalStatus() {
        return isExceptionalStatus;
    }

    /**
     * Sets the value of the isExceptionalStatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIsExceptionalStatus(Boolean value) {
        this.isExceptionalStatus = value;
    }

}
