//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-833 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2012.01.16 at 12:47:42 AM MEZ 
//


package de.jollyday.config;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Weekday.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="Weekday">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="MONDAY"/>
 *     &lt;enumeration value="TUESDAY"/>
 *     &lt;enumeration value="WEDNESDAY"/>
 *     &lt;enumeration value="THURSDAY"/>
 *     &lt;enumeration value="FRIDAY"/>
 *     &lt;enumeration value="SATURDAY"/>
 *     &lt;enumeration value="SUNDAY"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "Weekday")
@XmlEnum
public enum Weekday {

    MONDAY,
    TUESDAY,
    WEDNESDAY,
    THURSDAY,
    FRIDAY,
    SATURDAY,
    SUNDAY;

    public String value() {
        return name();
    }

    public static Weekday fromValue(String v) {
        return valueOf(v);
    }

}