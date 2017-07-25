package uk.gov.bis.lite.countryservice.scheduler;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.bis.lite.countryservice.cache.CountryCache;

public class CountryCacheJob implements Job {

  private static final Logger LOGGER = LoggerFactory.getLogger(CountryCacheJob.class);

  @Override
  public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
    LOGGER.info("Attempting to load the country list cache...");

    Scheduler scheduler = jobExecutionContext.getScheduler();

    try {
      CountryCache countryCache = (CountryCache) scheduler.getContext().get("countryCache");
      countryCache.load();
      LOGGER.info("Successfully finished loading the country list cache.");
    } catch (SchedulerException e) {
      LOGGER.error("Failed to load country list cache.", e);
    }
  }

}
