package uk.gov.bis.lite.countryservice;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import org.hibernate.validator.constraints.NotEmpty;

public class CountryServiceConfiguration extends Configuration {

    @NotEmpty
    private String soapUrl;

    @NotEmpty
    private String soapAction;

    @NotEmpty
    private String soapNamespace;

    private Integer cacheExpirySeconds;

    private String countryListCacheJobCron;

    @JsonProperty
    public String getSoapUrl() {
        return soapUrl;
    }

    @JsonProperty
    public void setSoapUrl(String soapUrl) {
        this.soapUrl = soapUrl;
    }

    @JsonProperty
    public String getSoapAction() {
        return soapAction;
    }

    @JsonProperty
    public void setSoapAction(String soapAction) {
        this.soapAction = soapAction;
    }

    @JsonProperty
    public String getSoapNamespace() {
        return soapNamespace;
    }

    @JsonProperty
    public void setSoapNamespace(String soapNamespace) {
        this.soapNamespace = soapNamespace;
    }

    @JsonProperty
    public Integer getCacheExpirySeconds() {
        return cacheExpirySeconds;
    }

    @JsonProperty
    public void setCacheExpirySeconds(Integer cacheExpirySeconds) {
        this.cacheExpirySeconds = cacheExpirySeconds;
    }

//    @Valid
//    @NotNull
//    public SundialConfiguration sundialConfiguration = new SundialConfiguration();
//
//    @JsonProperty("sundial")
//    public SundialConfiguration getSundialConfiguration() {
//        return sundialConfiguration;
//    }

    @NotEmpty
    public String getCountryListCacheJobCron() {
        return countryListCacheJobCron;
    }
}
