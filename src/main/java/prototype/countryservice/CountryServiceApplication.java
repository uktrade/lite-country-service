package prototype.countryservice;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.name.Named;
import com.hubspot.dropwizard.guice.GuiceBundle;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import prototype.countryservice.core.service.SpireGetCountriesClient;
import prototype.countryservice.resources.CountriesResource;

import javax.xml.bind.JAXBException;

public class CountryServiceApplication extends Application<CountryServiceConfiguration> {

    private GuiceBundle<CountryServiceConfiguration> guiceBundle;


    public static void main(String[] args) throws Exception {
        new CountryServiceApplication().run(args);
    }

    @Override
    public String getName() {
        return "countries-service";
    }

    @Override
    public void initialize(Bootstrap<CountryServiceConfiguration> bootstrap) {

        guiceBundle = GuiceBundle.<CountryServiceConfiguration>newBuilder()
          .addModule(new AbstractModule() {
              @Override
              protected void configure() {
                  //bindConstant().annotatedWith(Names.named("cacheExpiryMinutes")).to(configuration.getCacheExpiryMinutes());
                  //bindConstant().annotatedWith(Names.named("cacheExpiryMinutes")).to(60);
                  bind(SpireGetCountriesClient.class);//.to(SpireGetCountriesClient.class);
              }

              @Provides
              @Named("soapUrl")
              String provideSoapUrl(CountryServiceConfiguration configuration) {
                  return configuration.getSoapUrl();
              }

              @Provides
              @Named("soapNamespace")
              String provideSoapNamespace(CountryServiceConfiguration configuration) {
                  return configuration.getSoapNamespace();
              }

              @Provides
              @Named("soapAction")
              String provideSoapAction(CountryServiceConfiguration configuration) {
                  return configuration.getSoapAction();
              }

              @Provides
              @Named("cacheExpiryMinutes")
              Integer provideCacheExpiryMinutes(CountryServiceConfiguration configuration) {
                return configuration.getCacheExpiryMinutes();
              }
          })
          .setConfigClass(CountryServiceConfiguration.class)
          .build();

        bootstrap.addBundle(guiceBundle);
    }

    @Override
    public void run(CountryServiceConfiguration configuration, Environment environment) throws JAXBException {
        environment.jersey().register(guiceBundle.getInjector().getInstance(CountriesResource.class));
    }

}
