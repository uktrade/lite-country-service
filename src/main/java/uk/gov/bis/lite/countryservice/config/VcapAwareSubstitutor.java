package uk.gov.bis.lite.countryservice.config;

import io.dropwizard.configuration.EnvironmentVariableLookup;
import io.pivotal.labs.cfenv.CloudFoundryEnvironment;
import io.pivotal.labs.cfenv.CloudFoundryEnvironmentException;
import io.pivotal.labs.cfenv.CloudFoundryService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.StrSubstitutor;

import java.util.Optional;

public class VcapAwareSubstitutor extends StrSubstitutor {

  public static final String VCAP_JDBC_ENV_VAR = "VCAP_JDBC_URL"; //TODO defaults only, allow configuration
  //public static final String VCAP_SERVICES_ENV_VAR = "VCAP_SERVICES";

  public VcapAwareSubstitutor(boolean strict) {
    super(new VcapLookup(strict));
    this.setEnableSubstitutionInVariables(false);
  }

  private static final class VcapLookup extends EnvironmentVariableLookup {

    public VcapLookup(boolean strict) {
      super(strict);
    }

    @Override
    public String lookup(String key) {

      if(VCAP_JDBC_ENV_VAR.equals(key)) {

        CloudFoundryEnvironment cfEnvironment;
        try {
          cfEnvironment = new CloudFoundryEnvironment(System::getenv);
        } catch (CloudFoundryEnvironmentException e) {
          throw new RuntimeException("Failed to initialise CloudFoundryEnvironment", e);
        }

        Optional<CloudFoundryService> pgService = cfEnvironment.getServiceNames().stream()
            .map(cfEnvironment::getService)
            .filter(e -> e.getTags().contains("postgres")) //TODO config
            .findFirst();

        if (pgService.isPresent()) {
          String jdbcUri = pgService.get().getCredentials().get("jdbcuri").toString();
          if (StringUtils.isNoneBlank(jdbcUri)) {
            return jdbcUri;
          } else {
            throw new RuntimeException(String.format("No jdbcUri found in credentials (credentials available: %s)",
                pgService.get().getCredentials().keySet()));
          }
        } else {
          throw new RuntimeException("No service with tag 'postgres' found in CloudFoundryEnvironment");
        }
      } else {
        return super.lookup(key);
      }

    }
  }
}
