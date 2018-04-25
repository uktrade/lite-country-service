package uk.gov.bis.lite.countryservice.healthcheck;

import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.vyarus.dropwizard.guice.module.installer.feature.health.NamedHealthCheck;
import uk.gov.bis.lite.countryservice.service.CountryService;

public class SpireHealthCheck extends NamedHealthCheck {

  private static final Logger LOGGER = LoggerFactory.getLogger(SpireHealthCheck.class);

  private final CountryService countryService;

  @Inject
  public SpireHealthCheck(CountryService countryService) {
    this.countryService = countryService;
  }

  @Override
  protected Result check() throws Exception {
    SpireHealthStatus healthStatus = countryService.getHealthStatus();
    if (healthStatus.isHealthy()) {
      LOGGER.info("Communication with Spire is healthy. Country list last updated at {}", healthStatus.getLastUpdated());
      return Result.healthy();
    } else {
      LOGGER.warn("Failed communication with Spire");
      return Result.unhealthy(healthStatus.getErrorMessage());
    }
  }

  @Override
  public String getName() {
    return "SpireHealthCheck";
  }
}
