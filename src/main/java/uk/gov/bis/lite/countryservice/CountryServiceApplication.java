package uk.gov.bis.lite.countryservice;

import com.google.inject.Injector;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.quartz.Scheduler;
import org.quartz.impl.StdSchedulerFactory;
import ru.vyarus.dropwizard.guice.GuiceBundle;
import ru.vyarus.dropwizard.guice.module.installer.feature.jersey.ResourceInstaller;
import uk.gov.bis.lite.countryservice.config.CountryApplicationConfiguration;
import uk.gov.bis.lite.countryservice.config.GuiceModule;
import uk.gov.bis.lite.countryservice.core.cache.CountryListCache;
import uk.gov.bis.lite.countryservice.core.scheduler.CountryListCacheScheduler;
import uk.gov.bis.lite.countryservice.resource.CountriesResource;

public class CountryServiceApplication extends Application<CountryApplicationConfiguration> {

  private GuiceBundle<CountryApplicationConfiguration> guiceBundle;

  @Override
  public String getName() {
    return "country-service";
  }

  @Override
  public void initialize(Bootstrap<CountryApplicationConfiguration> bootstrap) {

    guiceBundle = new GuiceBundle.Builder<CountryApplicationConfiguration>()
        .modules(new GuiceModule())
        .installers(ResourceInstaller.class)
        .extensions(CountriesResource.class)
        .build();

    bootstrap.addBundle(guiceBundle);
  }

  @Override
  public void run(CountryApplicationConfiguration configuration, Environment environment) throws Exception {
    Injector injector = guiceBundle.getInjector();

    CountryListCache countryListCache = injector.getInstance(CountryListCache.class);

    // Store cache reference in scheduler context to be later retrieved from the job
    Scheduler scheduler = new StdSchedulerFactory().getScheduler();
    scheduler.getContext().put("countryListCache", countryListCache);

    environment.lifecycle().manage(new CountryListCacheScheduler(scheduler, configuration));

    environment.jersey().register(injector.getInstance(CountriesResource.class));
  }

  public static void main(String[] args) throws Exception {
    new CountryServiceApplication().run(args);
  }

}
