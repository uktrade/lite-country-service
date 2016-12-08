package uk.gov.bis.lite.countryservice.scheduler;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.bis.lite.countryservice.cache.CountryListCache;
import uk.gov.bis.lite.countryservice.exception.CacheLoadingException;

public class CountryListCacheJob implements Job {

  private static final Logger LOGGER = LoggerFactory.getLogger(CountryListCacheJob.class);

  @Override
  public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
    LOGGER.info("Starting the loading of the country list cache...");

    Scheduler scheduler = jobExecutionContext.getScheduler();

    try {
      CountryListCache countryListCache = (CountryListCache) scheduler.getContext().get("countryListCache");
      countryListCache.load();

    } catch (SchedulerException | CacheLoadingException e) {
      // if cache failed to load then try again sooner
      LOGGER.error("Failed to load country list cache.", e);
    }
    LOGGER.info("Finished loading country list cache.");
  }

}
