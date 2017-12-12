package uk.gov.bis.lite.countryservice.service;

import com.google.inject.util.Modules;
import io.dropwizard.configuration.FileConfigurationSourceProvider;
import io.dropwizard.setup.Bootstrap;
import uk.gov.bis.lite.countryservice.CountryServiceApplication;
import uk.gov.bis.lite.countryservice.GuiceTestModule;
import uk.gov.bis.lite.countryservice.config.CountryApplicationConfiguration;
import uk.gov.bis.lite.countryservice.config.GuiceModule;

public class CountryServicePactTestApplication extends CountryServiceApplication {
  public CountryServicePactTestApplication() {
    super(Modules.override(new GuiceModule()).with(new GuiceTestModule()));
  }

  public <T> T getInstance(Class<T> type) {
    return getGuiceBundle().getInjector().getInstance(type);
  }

  @Override
  public void initialize(Bootstrap<CountryApplicationConfiguration> bootstrap) {
    super.initialize(bootstrap);

    //"undo" override in parent to load from a file (instead of a resource)
    bootstrap.setConfigurationSourceProvider(new FileConfigurationSourceProvider());
  }

  @Override
  protected void flywayMigrate(CountryApplicationConfiguration configuration) {
    //NO DB to migrate
  }
}
