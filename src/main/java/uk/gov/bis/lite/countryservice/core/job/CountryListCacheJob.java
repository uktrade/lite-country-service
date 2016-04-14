package uk.gov.bis.lite.countryservice.core.job;

import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerContext;
import org.quartz.SchedulerException;
import org.quartz.TriggerKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.bis.lite.countryservice.core.cache.CountryListCache;
import uk.gov.bis.lite.countryservice.core.exception.CountryServiceException;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.TriggerBuilder.newTrigger;
import static org.quartz.TriggerKey.triggerKey;

public class CountryListCacheJob implements Job {

    private static final Logger LOGGER = LoggerFactory.getLogger(CountryListCacheJob.class);

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        LOGGER.info("Starting the loading of the country list cache...");

        try {
            SchedulerContext schedulerContext = jobExecutionContext.getScheduler().getContext();
            CountryListCache countryListCache = (CountryListCache) schedulerContext.get("countryListCache");

            countryListCache.load();
        } catch (SchedulerException | CountryServiceException e) {
            // if cache failed to load then try again sooner
            LOGGER.error("Failed to load cache.", e);

            TriggerKey triggerKey = TriggerKey.triggerKey("countryListCacheJobTrigger");
            CronTrigger trigger = newTrigger()
                    .withIdentity(triggerKey)
                    .withSchedule(cronSchedule("0 0/1 * * * ?"))
                    .build();

            try {
                jobExecutionContext.getScheduler().rescheduleJob(triggerKey("countryListCacheJobTrigger"), trigger);
            } catch (SchedulerException e2) {
                throw new JobExecutionException(e2);
            }
        }
        LOGGER.info("Finished loading country list cache.");
    }
}
