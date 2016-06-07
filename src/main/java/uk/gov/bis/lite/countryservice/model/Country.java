package uk.gov.bis.lite.countryservice.model;

import javax.xml.bind.annotation.XmlElement;

public class Country {

  @XmlElement(name = "COUNTRY_REF")
  private final String countryRef;

  @XmlElement(name = "COUNTRY_NAME")
  private final String countryName;

  @SuppressWarnings("unused")
  private Country() {
    this(null, null);
  }

  public Country(String countryRef, String countryName) {
    this.countryRef = countryRef;
    this.countryName = countryName;
  }

  public String getCountryRef() {
    return countryRef;
  }

  public String getCountryName() {
    return countryName;
  }

}
