package com.excursions.excursions.quartz;

import com.excursions.excursions.service.ExcursionService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import static com.excursions.excursions.log.message.QuartzLogMessages.QUARTZ_LOG_JOB_IN_PROCESS;

@Slf4j
public class DeleteWrongExcursionsByPlaces implements Job {
    private ExcursionService excursionService;

    private DeleteWrongExcursionsByPlaces(ExcursionService excursionService) {
        this.excursionService = excursionService;
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        excursionService.deleteExcursionsByNotExistPlaces();
        log.debug(QUARTZ_LOG_JOB_IN_PROCESS, this.getClass().getSimpleName());
    }
}
