package uk.gov.bis.lite.countryservice.core.scheduler;

import com.google.inject.Inject;
import io.dropwizard.lifecycle.Managed;
import org.quartz.CronTrigger;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.TriggerKey;
import uk.gov.bis.lite.countryservice.CountryServiceConfiguration;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;
import static uk.gov.bis.lite.countryservice.core.scheduler.CountryListCacheJob.JOB_PARAM_NORMAL_CRON;
import static uk.gov.bis.lite.countryservice.core.scheduler.CountryListCacheJob.JOB_PARAM_RETRY_CRON;

public class CountryListCacheScheduler implements Managed {

    static final TriggerKey TRIGGER_KEY = TriggerKey.triggerKey("countryListCacheJobTrigger");

    private final Scheduler scheduler;
    private final CountryServiceConfiguration config;

    @Inject
    public CountryListCacheScheduler(Scheduler scheduler, CountryServiceConfiguration config) {
        this.scheduler = scheduler;
        this.config = config;
    }

    @Override
    public void start() throws Exception {

        JobKey jobKey = JobKey.jobKey("countryListCacheJob");
        JobDetail jobDetail = newJob(CountryListCacheJob.class)
                .withIdentity(jobKey)
                .build();

        JobDataMap jobDataMap =  jobDetail.getJobDataMap();
        jobDataMap.put(JOB_PARAM_NORMAL_CRON, config.getCountryListCacheJobCron());
        jobDataMap.put(JOB_PARAM_RETRY_CRON, config.getCountryListCacheRetryJobCron());

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
