package uk.gov.bis.lite.countryservice.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class CountryView {

  private final String countryRef;
  private final String countryName;
  private final List<String> synonyms;

  @JsonCreator
  public CountryView(@JsonProperty("countryRef") String countryRef,
                     @JsonProperty("countryName") String countryName,
                     @JsonProperty("synonyms") List<String> synonyms) {
    this.countryRef = countryRef;
    this.countryName = countryName;
    this.synonyms = synonyms;
  }

  public String getCountryRef() {
    return countryRef;
  }

  public String getCountryName() {
    return countryName;
  }

  public List<String> getSynonyms() {
    return synonyms;
  }
}
