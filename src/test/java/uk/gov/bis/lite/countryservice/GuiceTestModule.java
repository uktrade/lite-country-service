package uk.gov.bis.lite.countryservice;

import com.google.inject.AbstractModule;
import uk.gov.bis.lite.countryservice.mocks.CountryServiceMock;
import uk.gov.bis.lite.countryservice.service.CountryService;

public class GuiceTestModule extends AbstractModule {
  @Override
  protected void configure() {
    bind(CountryService.class).to(CountryServiceMock.class);
  }
}
