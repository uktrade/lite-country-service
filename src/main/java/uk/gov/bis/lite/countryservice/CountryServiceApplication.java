package uk.gov.bis.lite.countryservice;

import com.codahale.metrics.servlets.AdminServlet;
import com.google.inject.Injector;
import com.google.inject.Module;
import io.dropwizard.Application;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.auth.AuthValueFactoryProvider;
import io.dropwizard.auth.basic.BasicCredentialAuthFilter;
import io.dropwizard.configuration.ResourceConfigurationSourceProvider;
import io.dropwizard.configuration.SubstitutingSourceProvider;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.flywaydb.core.Flyway;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;
import org.quartz.Scheduler;
import org.quartz.impl.StdSchedulerFactory;
import ru.vyarus.dropwizard.guice.GuiceBundle;
import ru.vyarus.dropwizard.guice.module.installer.feature.health.HealthCheckInstaller;
import ru.vyarus.dropwizard.guice.module.installer.feature.jersey.ResourceInstaller;
import uk.gov.bis.lite.common.auth.admin.AdminConstraintSecurityHandler;
import uk.gov.bis.lite.common.auth.basic.SimpleAuthenticator;
import uk.gov.bis.lite.common.auth.basic.SimpleAuthorizer;
import uk.gov.bis.lite.common.auth.basic.User;
import uk.gov.bis.lite.common.jersey.filter.ContainerCorrelationIdFilter;
import uk.gov.bis.lite.common.metrics.readiness.ReadinessServlet;
import uk.gov.bis.lite.common.paas.db.CloudFoundryEnvironmentSubstitutor;
import uk.gov.bis.lite.countryservice.cache.CountryCache;
import uk.gov.bis.lite.countryservice.config.CountryApplicationConfiguration;
import uk.gov.bis.lite.countryservice.config.GuiceModule;
import uk.gov.bis.lite.countryservice.healthcheck.SpireHealthCheck;
import uk.gov.bis.lite.countryservice.resource.AdminResource;
import uk.gov.bis.lite.countryservice.resource.CountryDataResource;
import uk.gov.bis.lite.countryservice.resource.CountryResource;
import uk.gov.bis.lite.countryservice.scheduler.CountryCacheScheduler;

public class CountryServiceApplication extends Application<CountryApplicationConfiguration> {

  private GuiceBundle<CountryApplicationConfiguration> guiceBundle;

  private final Module module;

  public CountryServiceApplication() {
    super();
    this.module = new GuiceModule();
  }

  public CountryServiceApplication(Module module) {
    super();
    this.module = module;
  }

  @Override
  public String getName() {
    return "country-service";
  }

  @Override
  public void initialize(Bootstrap<CountryApplicationConfiguration> bootstrap) {
    //Load config from a resource (i.e. file within the JAR), and substitute environment variables into it
    bootstrap.setConfigurationSourceProvider(new SubstitutingSourceProvider(
        new ResourceConfigurationSourceProvider(), new CloudFoundryEnvironmentSubstitutor()));

    guiceBundle = new GuiceBundle.Builder<CountryApplicationConfiguration>()
        .modules(module)
        .installers(ResourceInstaller.class, HealthCheckInstaller.class)
        .extensions(CountryResource.class, CountryDataResource.class, SpireHealthCheck.class, AdminResource.class)
        .build();

    bootstrap.addBundle(guiceBundle);
  }

  @Override
  public void run(CountryApplicationConfiguration configuration, Environment environment) throws Exception {

    // Authorization and authentication handlers
    SimpleAuthenticator simpleAuthenticator = new SimpleAuthenticator(configuration.getAdminLogin(),
        configuration.getAdminPassword(),
        configuration.getServiceLogin(),
        configuration.getServicePassword());
    environment.jersey().register(new AuthDynamicFeature(new BasicCredentialAuthFilter.Builder<User>()
        .setAuthenticator(simpleAuthenticator)
        .setAuthorizer(new SimpleAuthorizer())
        .setRealm("Country Service Authentication")
        .buildAuthFilter()));
    environment.jersey().register(RolesAllowedDynamicFeature.class);
    environment.jersey().register(new AuthValueFactoryProvider.Binder<>(User.class));

    Injector injector = guiceBundle.getInjector();

    ReadinessServlet readinessServlet = injector.getInstance(ReadinessServlet.class);
    environment.admin().addServlet("ready", readinessServlet).addMapping("/ready");

    CountryCache countryCache = injector.getInstance(CountryCache.class);

    // Store cache reference in scheduler context to be later retrieved from the job
    Scheduler scheduler = new StdSchedulerFactory().getScheduler();
    scheduler.getContext().put("countryCache", countryCache);

    environment.lifecycle().manage(new CountryCacheScheduler(scheduler, configuration));

    flywayMigrate(configuration);

    environment.jersey().register(ContainerCorrelationIdFilter.class);

    environment.admin().addServlet("admin", new AdminServlet()).addMapping("/admin");
    //environment.admin().setSecurityHandler(new AdminConstraintSecurityHandler(configuration.getLogin(), configuration.getPassword()));
    environment.admin().setSecurityHandler(new AdminConstraintSecurityHandler(configuration.getServiceLogin(), configuration.getServicePassword()));
  }

  protected void flywayMigrate(CountryApplicationConfiguration configuration) {
    // Perform / validate flyway migration on startup
    DataSourceFactory dataSourceFactory = configuration.getDataSourceFactory();
    Flyway flyway = new Flyway();
    flyway.setDataSource(dataSourceFactory.getUrl(), dataSourceFactory.getUser(), dataSourceFactory.getPassword());
    flyway.migrate();
  }

  public static void main(String[] args) throws Exception {
    new CountryServiceApplication().run(args);
  }

}
