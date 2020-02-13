package com.excursions.excursions.service.impl;

import com.excursions.excursions.client.PlaceClient;
import com.excursions.excursions.model.Excursion;
import com.excursions.excursions.repository.ExcursionRepository;
import com.excursions.excursions.service.ExcursionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.List;

import static com.excursions.excursions.exception.ServiceException.serviceExceptionExistEntity;
import static com.excursions.excursions.exception.ServiceException.serviceExceptionWrongEntity;
import static com.excursions.excursions.log.message.ServiceLogMessages.SERVICE_LOG_NEW_ENTITY;

@Slf4j
@Service
public class ExcursionServiceImpl implements ExcursionService {

    private String SERVICE_NAME = "ExcursionServiceImpl";

    private ExcursionRepository excursionRepository;
    private EntityManager entityManager;

    @Autowired
    protected ExcursionServiceImpl(ExcursionRepository excursionRepository, EntityManager entityManager) {
        this.excursionRepository = excursionRepository;
        this.entityManager = entityManager;
    }

    @Override
    public Excursion save(String name, LocalDateTime start, LocalDateTime stop, Integer peopleCount, List<Long> places) {
        Excursion savedExcursion;
        Excursion excursionForSave = new Excursion(name, start, stop, peopleCount, places);

        try {
            savedExcursion = excursionRepository.save(excursionForSave);
            entityManager.flush();
        } catch (ConstraintViolationException e) {
            throw serviceExceptionWrongEntity(SERVICE_NAME, e.getConstraintViolations().iterator().next().getMessage());
        } catch (PersistenceException e) {
            throw serviceExceptionExistEntity(SERVICE_NAME);
        }

        log.debug(SERVICE_LOG_NEW_ENTITY, savedExcursion);
        return savedExcursion;
    }
}
