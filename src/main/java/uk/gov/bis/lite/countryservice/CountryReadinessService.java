package uk.gov.bis.lite.countryservice;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import uk.gov.bis.lite.common.metrics.readiness.ReadinessService;
import uk.gov.bis.lite.countryservice.cache.CountryCache;
import uk.gov.bis.lite.countryservice.healthcheck.SpireHealthStatus;

@Singleton
public class CountryReadinessService implements ReadinessService {

  @JsonInclude(Include.NON_NULL)
  public class CountryReadiness {
    public final boolean ready;

    public final String message;

    public CountryReadiness(boolean ready, String message) {
      this.ready = ready;
      this.message = message;
    }
  }

  private final CountryCache countryCache;

  @Inject
  public CountryReadinessService(CountryCache countryCache) {
    this.countryCache = countryCache;
  }

  @Override
  public boolean isReady() {
    return getReadiness().ready;
  }

  public CountryReadiness getReadiness() {
    SpireHealthStatus cacheHealthStatus = countryCache.getHealthStatus();
    boolean ready = cacheHealthStatus.isHealthy() || countryCache.isPopulated();
    if (cacheHealthStatus.isHealthy()) {
      return new CountryReadiness(ready, null);
    } else {
      // Cache is unhealthy, but application may still be ready to respond
      return new CountryReadiness(ready,  "Country list cache is unhealthy: " + cacheHealthStatus.getErrorMessage());
    }
  }

  @Override
  public JsonNode readinessJson() {
    ObjectMapper mapper = new ObjectMapper();
    return mapper.valueToTree(getReadiness());
  }
}
