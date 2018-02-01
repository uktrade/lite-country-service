package uk.gov.bis.lite.countryservice.service;

import com.google.inject.util.Modules;
import uk.gov.bis.lite.countryservice.CountryServiceApplication;
import uk.gov.bis.lite.countryservice.GuiceTestModule;
import uk.gov.bis.lite.countryservice.config.CountryApplicationConfiguration;
import uk.gov.bis.lite.countryservice.config.GuiceModule;

public class TestCountryServiceApplication extends CountryServiceApplication {

  public TestCountryServiceApplication() {
    super(Modules.override(new GuiceModule()).with(new GuiceTestModule()));
  }

  public <T> T getInstance(Class<T> type) {
    return getGuiceBundle().getInjector().getInstance(type);
  }

  @Override
  protected void flywayMigrate(CountryApplicationConfiguration configuration) {
    //NO DB to migrate
  }
}
