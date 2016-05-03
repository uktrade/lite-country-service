package uk.gov.bis.lite.countryservice;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.name.Names;
import de.thomaskrille.dropwizard_template_config.TemplateConfigBundle;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;
import uk.gov.bis.lite.countryservice.core.cache.CountryListCache;
import uk.gov.bis.lite.countryservice.core.exception.CountryServiceException;
import uk.gov.bis.lite.countryservice.core.scheduler.CountryListCacheScheduler;
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
        bootstrap.addBundle(new TemplateConfigBundle());
    }

    @Override
    public void run(CountryServiceConfiguration configuration, Environment environment) throws JAXBException,
            CountryServiceException,
            SchedulerException {

        Injector injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bindConstant().annotatedWith(Names.named("cacheExpirySeconds")).to(configuration.getCacheExpirySeconds());

                SpireGetCountriesClient spireGetCountriesClient = new SpireGetCountriesClient(configuration.getSoapUrl(),
                        configuration.getSoapNamespace(),
                        configuration.getSoapAction(),
                        configuration.getSpireCredentials());

                bind(SpireGetCountriesClient.class).toInstance(spireGetCountriesClient);
            }
        });

        CountryListCache countryListCache = injector.getInstance(CountryListCache.class);

        // Store cache reference in scheduler context to be later retrieved from the job
        Scheduler scheduler = new StdSchedulerFactory().getScheduler();
        scheduler.getContext().put("countryListCache", countryListCache);

        environment.lifecycle().manage(new CountryListCacheScheduler(scheduler, configuration));

        environment.jersey().register(injector.getInstance(CountriesResource.class));
    }

}
