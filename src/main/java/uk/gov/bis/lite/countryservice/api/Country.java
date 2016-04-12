package uk.gov.bis.lite.countryservice.api;

import javax.xml.bind.annotation.XmlElement;

public class Country {

    private String countryRef;

    private String countryName;

    @XmlElement(name = "COUNTRY_REF")
    public String getCountryRef() {
        return countryRef;
    }

    @XmlElement(name = "COUNTRY_NAME")
    public String getCountryName() {
        return countryName;
    }

    public void setCountryRef(String countryRef) {
        this.countryRef = countryRef;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    @Override
    public String toString() {
        return "Country{" +
                "countryRef='" + countryRef + '\'' +
                ", countryName='" + countryName + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Country country = (Country) o;

        if (countryRef != null ? !countryRef.equals(country.countryRef) : country.countryRef != null) return false;
        return countryName != null ? countryName.equals(country.countryName) : country.countryName == null;

    }

    @Override
    public int hashCode() {
        int result = countryRef != null ? countryRef.hashCode() : 0;
        result = 31 * result + (countryName != null ? countryName.hashCode() : 0);
        return result;
    }
}
