package uk.gov.bis.lite.countryservice;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import uk.gov.bis.lite.common.metrics.readiness.ReadinessService;
import uk.gov.bis.lite.countryservice.cache.CountryCache;

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
    return countryCache.isPopulated();
  }

  @Override
  public JsonNode readinessJson() {
    CountryReadiness countryReadiness;
    if (isReady()) {
      countryReadiness = new CountryReadiness(true, null);
    } else {
      countryReadiness = new CountryReadiness(false, "Country list cache is not populated.");
    }
    ObjectMapper mapper = new ObjectMapper();
    return mapper.valueToTree(countryReadiness);
  }
}
