package prototype.countryservice;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.name.Names;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import prototype.countryservice.core.service.CountryListFactory;
import prototype.countryservice.core.service.GetCountriesService;
import prototype.countryservice.core.service.SpireGetCountriesClient;
import prototype.countryservice.resources.CountriesResource;

import javax.xml.bind.JAXBException;

public class CountryServiceApplication extends Application<CountryServiceConfiguration> {

    public static void main(String[] args) throws Exception {
        new CountryServiceApplication().run(args);
    }

    @Override
    public String getName() {
        return "countries-service";
    }

    @Override
    public void initialize(Bootstrap<CountryServiceConfiguration> bootstrap) {
        // nothing to do yet
    }

    @Override
    public void run(CountryServiceConfiguration configuration, Environment environment) throws JAXBException {

        Injector injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bindConstant().annotatedWith(Names.named("cacheExpiryMinutes")).to(configuration.getCacheExpiryMinutes());
                bind(SpireGetCountriesClient.class).toInstance(new SpireGetCountriesClient(configuration.getSoapUrl(),
                        configuration.getSoapNamespace(),
                        configuration.getSoapAction()));
            }
        });
        environment.jersey().register(injector.getInstance(CountriesResource.class));
    }

}
