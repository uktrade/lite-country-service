package prototype.countryservice.api;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "COUNTRY_LIST")
public class CountryList {

    private List<Country> countries;

    @XmlElement(name = "COUNTRY")
    public List<Country> getCountries() {
        return countries;
    }

    public void setCountries(List<Country> countries) {
        this.countries = countries;
    }

    @Override
    public String toString() {
        return "CountryList{" +
                "countries=" + countries +
                '}';
    }
}
