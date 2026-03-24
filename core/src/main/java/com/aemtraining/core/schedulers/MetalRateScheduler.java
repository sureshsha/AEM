package com.aemtraining.core.schedulers;


import com.aemtraining.core.configs.MetalPriceApiConfig;
import com.aemtraining.core.services.MetalRateService;
import org.apache.sling.commons.scheduler.ScheduleOptions;
import org.apache.sling.commons.scheduler.Scheduler;
import org.osgi.service.component.annotations.*;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(service = Runnable.class, immediate = true)
@Designate(ocd = MetalPriceApiConfig.class)
public class MetalRateScheduler implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(MetalRateScheduler.class);

    @Reference
    private Scheduler scheduler;

    @Reference
    private MetalRateService metalRateService;

    private volatile MetalPriceApiConfig config;
    private String jobName;

    @Activate
    @Modified
    protected void activate(MetalPriceApiConfig config) {
        this.config = config;
        this.jobName = config.schedulerName();

        scheduler.unschedule(jobName);

        if (config.enabled()) {
            ScheduleOptions options = scheduler.EXPR(config.cronExpression());
            options.name(jobName);
            options.canRunConcurrently(false);
            scheduler.schedule(this, options);

            LOG.info("MetalRateScheduler started with cron {}", config.cronExpression());
        }
    }

    @Deactivate
    protected void deactivate() {
        if (jobName != null) {
            scheduler.unschedule(jobName);
        }
    }

    @Override
    public void run() {
        LOG.info("Running MetalpriceAPI scheduler...");
        metalRateService.fetchAndStoreRates();
    }
}
