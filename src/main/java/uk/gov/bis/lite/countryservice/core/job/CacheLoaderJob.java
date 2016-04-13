package uk.gov.bis.lite.countryservice.core.job;

import com.google.inject.Inject;
import org.knowm.sundial.Job;
import org.knowm.sundial.SundialJobScheduler;
import org.knowm.sundial.annotations.CronTrigger;
import org.knowm.sundial.exceptions.JobInterruptException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.bis.lite.countryservice.api.CountryList;
import uk.gov.bis.lite.countryservice.core.cache.CountryListCache;
import uk.gov.bis.lite.countryservice.core.exception.CountryServiceException;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static com.sun.xml.internal.ws.spi.db.BindingContextFactory.LOGGER;

@CronTrigger(cron = "0 0/1 * * * ?")
public class CacheLoaderJob extends Job {

    private static final Logger LOGGER = LoggerFactory.getLogger(CacheLoaderJob.class);

    @Override
    public void doRun() throws JobInterruptException {
        LOGGER.info("Starting the loading of the country list cache...");

        CountryListCache countryListCache = (CountryListCache) SundialJobScheduler.getServletContext().getAttribute("countryListCache");
        try {
            countryListCache.load();
        } catch (CountryServiceException e) {
            // if cache failed to load then try again sooner
            LOGGER.error("Failed to load cache.", e);
        }

        LOGGER.info("Finished loading country list cache.");
    }
}
