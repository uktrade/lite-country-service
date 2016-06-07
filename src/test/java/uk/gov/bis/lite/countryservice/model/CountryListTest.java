package uk.gov.bis.lite.countryservice.model;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.jackson.Jackson;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static io.dropwizard.testing.FixtureHelpers.fixture;
import static org.assertj.core.api.Assertions.assertThat;

public class CountryListTest {

  private static final String COUNTRY_LIST_JSON = "countrylist.json";
  private static final ObjectMapper MAPPER = Jackson.newObjectMapper();

  @Test
  public void serializesToJSON() throws Exception {

    String countryListJSON = MAPPER.writeValueAsString(MAPPER.readValue(fixture(COUNTRY_LIST_JSON),
        new TypeReference<List<Country>>() {
        }));

    List<Country> countryList = createCountryList();
    String result = MAPPER.writeValueAsString(countryList);

    assertThat(result).isEqualTo(countryListJSON);
  }

  @Test
  public void deserializesFromJSON() throws Exception {

    List<Country> countryList = createCountryList();

    List<Country> result = MAPPER.readValue(fixture(COUNTRY_LIST_JSON), new TypeReference<List<Country>>() {
    });

    assertThat(result).isEqualTo(countryList);
  }

  private List<Country> createCountryList() {
    return Arrays.asList(new Country("CTRY3", "Abu Dhabi"), new Country("CTRY909", "Barbados"),
        new Country("CTRY1030", "Colombia"), new Country("CTRY1771", "India"));
  }

}