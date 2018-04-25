package uk.gov.bis.lite.countryservice.scheduler;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

import com.google.inject.Inject;
import io.dropwizard.lifecycle.Managed;
import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.TriggerKey;
import uk.gov.bis.lite.countryservice.config.CountryApplicationConfiguration;

public class CountryCacheScheduler implements Managed {

  private final Scheduler scheduler;
  private final CountryApplicationConfiguration config;

  @Inject
  public CountryCacheScheduler(Scheduler scheduler, CountryApplicationConfiguration config) {
    this.scheduler = scheduler;
    this.config = config;
  }

  @Override
  public void start() throws Exception {

    JobKey jobKey = JobKey.jobKey("countryCacheJob");
    JobDetail jobDetail = newJob(CountryCacheJob.class)
        .withIdentity(jobKey)
        .build();

    TriggerKey triggerKey = TriggerKey.triggerKey("countryCacheJobTrigger");

    CronTrigger trigger = newTrigger()
        .withIdentity(triggerKey)
        .withSchedule(cronSchedule(config.getCountryCacheJobCron()))
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
