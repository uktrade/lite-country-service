package uk.gov.bis.lite.countryservice.core.job;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerContext;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.bis.lite.countryservice.core.cache.CountryListCache;
import uk.gov.bis.lite.countryservice.core.exception.CountryServiceException;

public class CountryListCacheJob implements Job {

    private static final Logger LOGGER = LoggerFactory.getLogger(CountryListCacheJob.class);

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        LOGGER.info("Starting the loading of the country list cache...");

        try {
            SchedulerContext schedulerContext = jobExecutionContext.getScheduler().getContext();
            CountryListCache countryListCache =  (CountryListCache) schedulerContext.get("countryListCache");

            countryListCache.load();
        } catch (SchedulerException | CountryServiceException e) {
            // if cache failed to load then try again sooner
            LOGGER.error("Failed to load cache.", e);
        }
        LOGGER.info("Finished loading country list cache.");
    }
}
