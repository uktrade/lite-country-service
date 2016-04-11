package prototype.countryservice.api;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "COUNTRY_LIST")
public class CountryList {

    @XmlElement(name = "COUNTRY")
    private final List<Country> countries;

    private CountryList() {
        this.countries = null;
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
