package uk.gov.bis.lite.countryservice.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;

public class CountryApplicationConfiguration extends Configuration {

  @NotEmpty
  @JsonProperty
  private String soapUrl;

  @NotNull
  @JsonProperty
  private Integer cacheExpirySeconds;

  @NotEmpty
  @JsonProperty
  private String countryListCacheJobCron;

  @NotEmpty
  @JsonProperty
  private String countryListCacheRetryJobCron;

  @NotEmpty
  @JsonProperty
  private String spireCredentials;

  public String getSoapUrl() {
    return soapUrl;
  }

  public Integer getCacheExpirySeconds() {
    return cacheExpirySeconds;
  }

  public String getCountryListCacheJobCron() {
    return countryListCacheJobCron;
  }

  public String getCountryListCacheRetryJobCron() {
    return countryListCacheRetryJobCron;
  }

  public String getSpireCredentials() {
    return spireCredentials;
  }
}
