package prototype.countryservice.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.xml.bind.annotation.XmlElement;

public class Country {

    @XmlElement(name = "COUNTRY_REF")
    private final String countryRef;

    @XmlElement(name = "COUNTRY_NAME")
    private final String countryName;

    public String getCountryRef() {
        return countryRef;
    }

    public String getCountryName() {
        return countryName;
    }

    /* Required by JAXB for unmarshalling */
    private Country() {
        this(null, null);
    }

    @JsonCreator
    public Country(@JsonProperty("countryRef") String countryRef, @JsonProperty("countryName") String countryName) {
        this.countryRef = countryRef;
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
