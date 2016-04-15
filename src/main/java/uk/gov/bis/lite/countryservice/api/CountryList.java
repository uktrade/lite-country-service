package uk.gov.bis.lite.countryservice.api;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Collections;
import java.util.List;

@XmlRootElement(name = "COUNTRY_LIST")
public class CountryList {

    @XmlElement(name = "COUNTRY")
    private final List<Country> countries;

    @SuppressWarnings("unused")
    private CountryList() {
        this(null);
    }

    public CountryList(List<Country> countries) {
        this.countries = countries;
    }

    public List<Country> getCountries() {
        return countries;
    }

    @Override
    public String toString() {
        return "CountryList{" +
                "countries=" + countries +
                '}';
    }
}
