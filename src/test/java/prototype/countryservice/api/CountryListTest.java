package prototype.countryservice.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.jackson.Jackson;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static io.dropwizard.testing.FixtureHelpers.fixture;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class CountryListTest {

    private static final ObjectMapper MAPPER = Jackson.newObjectMapper();

    @Test
    public void serializesToJSON() throws Exception {

        String countryListJSON = MAPPER.writeValueAsString(MAPPER.readValue(fixture("countrylist.json"),
                new TypeReference<List<Country>>(){}));

        List<Country> countryList = createCountryList();
        String result = MAPPER.writeValueAsString(countryList);

        assertThat(result, is(countryListJSON));
    }

    @Test
    public void deserializesFromJSON() throws Exception {

        List<Country> countryList = createCountryList();

        List<Country> result = MAPPER.readValue(fixture("countrylist.json"), new TypeReference<List<Country>>(){});

        assertThat(result, is(countryList));
    }

    private List<Country> createCountryList(){
        Country country = new Country("CTRY3", "Abu Dhabi");
        Country country2 = new Country("CTRY909", "Barbados");
        return Arrays.asList(country, country2);
    }
}