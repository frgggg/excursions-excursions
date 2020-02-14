package com.excursions.excursions.config;

import com.excursions.excursions.quartz.DeleteEndedExcursions;
import com.excursions.excursions.quartz.DeleteWrongExcursionsByPlaces;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QuartzConfig {

    @Value("${excursion.ended.quartz}")
    private String deleteEndedExcursionsCircleConfig;

    @Value("${excursion.wrong-by-places.quartz}")
    private String deleteWrongExcursionsByPlacesCircleConfig;

    private static final String JOB_IDENTITY_POSTFIX = "-JobDetail-Identity";
    private static final String TRIGGER_IDENTITY_POSTFIX = "-JobDetail-Identity";

    @Bean("deleteEndedExcursionsJobDetail")
    public JobDetail deleteEndedExcursionsJobDetail(){
        return JobBuilder.newJob(DeleteEndedExcursions.class)
                .withIdentity(DeleteEndedExcursions.class.getSimpleName() + JOB_IDENTITY_POSTFIX)
                .storeDurably().build();
    }

    @Bean
    public Trigger deleteEndedExcursionsJobTrigger(@Qualifier("deleteEndedExcursionsJobDetail") JobDetail findDebtorsJobDetail)
    {
        return TriggerBuilder.newTrigger().forJob(findDebtorsJobDetail)
                .withIdentity(DeleteEndedExcursions.class.getSimpleName() + TRIGGER_IDENTITY_POSTFIX)
                .withSchedule(CronScheduleBuilder.cronSchedule(deleteEndedExcursionsCircleConfig))
                .build();
    }

    @Bean("deleteWrongExcursionsByPlacesJobDetail")
    public JobDetail deleteWrongExcursionsByPlacesJobDetail(){
        return JobBuilder.newJob(DeleteWrongExcursionsByPlaces.class)
                .withIdentity(DeleteWrongExcursionsByPlaces.class.getSimpleName() + JOB_IDENTITY_POSTFIX)
                .storeDurably().build();
    }

    @Bean
    public Trigger deleteWrongExcursionsByPlacesJobTrigger(@Qualifier("deleteWrongExcursionsByPlacesJobDetail") JobDetail findDebtorsJobDetail)
    {
        return TriggerBuilder.newTrigger().forJob(findDebtorsJobDetail)
                .withIdentity(DeleteWrongExcursionsByPlaces.class.getSimpleName() + TRIGGER_IDENTITY_POSTFIX)
                .withSchedule(CronScheduleBuilder.cronSchedule(deleteWrongExcursionsByPlacesCircleConfig))
                .build();
    }
}
