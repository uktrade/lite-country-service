package uk.gov.bis.lite.countryservice.core.scheduler;

import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.bis.lite.countryservice.cache.CountryListCache;
import uk.gov.bis.lite.countryservice.exception.CountryServiceException;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.TriggerBuilder.newTrigger;
import static uk.gov.bis.lite.countryservice.core.scheduler.CountryListCacheScheduler.TRIGGER_KEY;

public class CountryListCacheJob implements Job {

  private static final Logger LOGGER = LoggerFactory.getLogger(CountryListCacheJob.class);
  private final AtomicBoolean resetTriggerFlag = new AtomicBoolean();

  static final String JOB_PARAM_NORMAL_CRON = "normalJobCron";
  static final String JOB_PARAM_RETRY_CRON = "retryJobCron";

  @Override
  public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
    LOGGER.info("Starting the loading of the country list cache...");

    JobDataMap jobDataMap = jobExecutionContext.getJobDetail().getJobDataMap();
    String normalJobCron = (String) jobDataMap.get(JOB_PARAM_NORMAL_CRON);
    String retryJobCron = (String) jobDataMap.get(JOB_PARAM_RETRY_CRON);

    Scheduler scheduler = jobExecutionContext.getScheduler();

    try {
      CountryListCache countryListCache = (CountryListCache) scheduler.getContext().get("countryListCache");
      countryListCache.load();

      if (resetTriggerFlag.get()) {
        scheduler.rescheduleJob(TRIGGER_KEY, buildTrigger(normalJobCron));
        resetTriggerFlag.set(false);
      }

    } catch (SchedulerException | CountryServiceException e) {
      // if cache failed to load then try again sooner
      LOGGER.error("Failed to load country list cache.", e);

      changeTriggerToRunSooner(scheduler, retryJobCron);
    }
    LOGGER.info("Finished loading country list cache.");
  }

  private void changeTriggerToRunSooner(Scheduler scheduler, String retryJobCron) throws JobExecutionException {

    try {
      scheduler.rescheduleJob(TRIGGER_KEY, buildTrigger(retryJobCron));
      resetTriggerFlag.set(true);
    } catch (SchedulerException e2) {
      throw new JobExecutionException(e2);
    }
  }

  private CronTrigger buildTrigger(String retryJobCron) {
    return newTrigger()
        .withIdentity(TRIGGER_KEY)
        .withSchedule(cronSchedule(retryJobCron))
        .build();
  }

}
