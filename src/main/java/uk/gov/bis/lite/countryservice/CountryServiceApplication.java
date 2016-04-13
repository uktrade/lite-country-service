package uk.gov.bis.lite.countryservice;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.name.Names;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.knowm.dropwizard.sundial.SundialBundle;
import org.knowm.dropwizard.sundial.SundialConfiguration;
import uk.gov.bis.lite.countryservice.core.cache.CountryListCache;
import uk.gov.bis.lite.countryservice.core.exception.CountryServiceException;
import uk.gov.bis.lite.countryservice.core.service.SpireGetCountriesClient;
import uk.gov.bis.lite.countryservice.resources.CountriesResource;

import javax.xml.bind.JAXBException;

public class CountryServiceApplication extends Application<CountryServiceConfiguration> {

    public static void main(String[] args) throws Exception {
        new CountryServiceApplication().run(args);
    }

    @Override
    public String getName() {
        return "country-service";
    }

    @Override
    public void initialize(Bootstrap<CountryServiceConfiguration> bootstrap) {
        bootstrap.addBundle(new SundialBundle<CountryServiceConfiguration>() {

            @Override
            public SundialConfiguration getSundialConfiguration(CountryServiceConfiguration configuration) {
                return configuration.getSundialConfiguration();
            }
        });
    }

    @Override
    public void run(CountryServiceConfiguration configuration, Environment environment) throws JAXBException {

        Injector injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bindConstant().annotatedWith(Names.named("cacheExpirySeconds")).to(configuration.getCacheExpirySeconds());
                bind(SpireGetCountriesClient.class).toInstance(new SpireGetCountriesClient(configuration.getSoapUrl(),
                        configuration.getSoapNamespace(),
                        configuration.getSoapAction()));
            }
        });

        CountryListCache countryListCache = injector.getInstance(CountryListCache.class);

        environment.getApplicationContext().setAttribute("countryListCache", countryListCache);

        environment.jersey().register(injector.getInstance(CountriesResource.class));
    }

}
