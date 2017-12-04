package uk.gov.bis.lite.countryservice.service;

import io.dropwizard.configuration.FileConfigurationSourceProvider;
import io.dropwizard.setup.Bootstrap;
import uk.gov.bis.lite.countryservice.CountryServiceApplication;
import uk.gov.bis.lite.countryservice.config.CountryApplicationConfiguration;

public class CountryServiceIntegrationTestApplication extends CountryServiceApplication {
  @Override
  public void initialize(Bootstrap<CountryApplicationConfiguration> bootstrap) {
    super.initialize(bootstrap);

    //"undo" override in parent to load from a file (instead of a resource)
    bootstrap.setConfigurationSourceProvider(new FileConfigurationSourceProvider());
  }
}
