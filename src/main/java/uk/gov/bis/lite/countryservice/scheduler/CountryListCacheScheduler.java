package uk.gov.bis.lite.countryservice.scheduler;

import com.google.inject.Inject;
import io.dropwizard.lifecycle.Managed;
import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.TriggerKey;
import uk.gov.bis.lite.countryservice.config.CountryApplicationConfiguration;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

public class CountryListCacheScheduler implements Managed {

  private final Scheduler scheduler;
  private final CountryApplicationConfiguration config;

  @Inject
  public CountryListCacheScheduler(Scheduler scheduler, CountryApplicationConfiguration config) {
    this.scheduler = scheduler;
    this.config = config;
  }

  @Override
  public void start() throws Exception {

    JobKey jobKey = JobKey.jobKey("countryListCacheJob");
    JobDetail jobDetail = newJob(CountryListCacheJob.class)
        .withIdentity(jobKey)
        .build();

    TriggerKey TRIGGER_KEY = TriggerKey.triggerKey("countryListCacheJobTrigger");

    CronTrigger trigger = newTrigger()
        .withIdentity(TRIGGER_KEY)
        .withSchedule(cronSchedule(config.getCountryListCacheJobCron()))
        .build();

    scheduler.scheduleJob(jobDetail, trigger);
    scheduler.start();
    scheduler.triggerJob(jobKey);
  }

  @Override
  public void stop() throws Exception {
    scheduler.shutdown(true);
  }

}
