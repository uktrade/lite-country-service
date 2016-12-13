package uk.gov.bis.lite.countryservice.model;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.jackson.Jackson;
import org.junit.Test;
import uk.gov.bis.lite.countryservice.spire.model.SpireCountry;

import java.util.Arrays;
import java.util.List;

import static io.dropwizard.testing.FixtureHelpers.fixture;
import static org.assertj.core.api.Assertions.assertThat;

public class SpireCountryListTest {

  private static final String COUNTRY_LIST_JSON = "countrylist.json";
  private static final ObjectMapper MAPPER = Jackson.newObjectMapper();

  @Test
  public void serializesToJSON() throws Exception {

    String countryListJSON = MAPPER.writeValueAsString(MAPPER.readValue(fixture(COUNTRY_LIST_JSON),
        new TypeReference<List<SpireCountry>>() {
        }));

    List<SpireCountry> spireCountryList = createCountryList();
    String result = MAPPER.writeValueAsString(spireCountryList);

    assertThat(result).isEqualTo(countryListJSON);
  }

  @Test
  public void deserializesFromJSON() throws Exception {

    List<SpireCountry> result = MAPPER.readValue(fixture(COUNTRY_LIST_JSON), new TypeReference<List<SpireCountry>>() {
    });

    assertThat(result).isNotEmpty();
    assertThat(result.size()).isEqualTo(4);
  }

  private List<SpireCountry> createCountryList() {
    return Arrays.asList(new SpireCountry("CTRY3", "Abu Dhabi"), new SpireCountry("CTRY909", "Barbados"),
        new SpireCountry("CTRY1030", "Colombia"), new SpireCountry("CTRY1771", "India"));
  }

}