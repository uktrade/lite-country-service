package uk.gov.bis.lite.countryservice.config;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.name.Names;
import ru.vyarus.dropwizard.guice.module.support.ConfigurationAwareModule;
import uk.gov.bis.lite.countryservice.spire.SpireGetCountriesClient;

public class GuiceModule  extends AbstractModule implements ConfigurationAwareModule<CountryApplicationConfiguration> {

  private CountryApplicationConfiguration configuration;

  @Override
  protected void configure() {
    bindConstant().annotatedWith(Names.named("cacheExpirySeconds")).to(configuration.getCacheExpirySeconds());
  }

  @Override
  public void setConfiguration(CountryApplicationConfiguration configuration) {
    this.configuration = configuration;
  }


  @Provides
  public SpireGetCountriesClient spireGoodsCheckerClient(CountryApplicationConfiguration config) {
    return new SpireGetCountriesClient(config.getSoapUrl(), config.getSpireCredentials());
  }
}