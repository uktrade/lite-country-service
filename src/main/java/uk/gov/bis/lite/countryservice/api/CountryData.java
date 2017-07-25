package uk.gov.bis.lite.countryservice.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CountryData {

  private final String countryRef;
  private final List<String> synonyms;

  @JsonCreator
  public CountryData(@JsonProperty("countryRef") String countryRef,
                     @JsonProperty("synonyms") List<String> synonyms) {
    this.countryRef = countryRef;
    this.synonyms = synonyms;
  }

  public String getCountryRef() {
    return countryRef;
  }

  public List<String> getSynonyms() {
    return synonyms;
  }
}
