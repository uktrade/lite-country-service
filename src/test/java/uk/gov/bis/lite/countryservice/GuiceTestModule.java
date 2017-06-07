package uk.gov.bis.lite.countryservice;

import com.google.inject.AbstractModule;
import uk.gov.bis.lite.countryservice.mocks.CountriesServiceMock;
import uk.gov.bis.lite.countryservice.service.CountriesService;

public class GuiceTestModule extends AbstractModule {
  @Override
  protected void configure() {
    bind(CountriesService.class).to(CountriesServiceMock.class);
  }
}
