package com.excursions.excursions.service;

import com.excursions.excursions.exception.ServiceException;
import com.excursions.excursions.model.Excursion;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

public interface ExcursionService {

    @Transactional(isolation = Isolation.SERIALIZABLE, rollbackFor = ServiceException.class)
    Excursion save(String name, LocalDateTime start, LocalDateTime stop, Integer peopleCount, List<Long> places);

    @Transactional(isolation = Isolation.SERIALIZABLE, rollbackFor = ServiceException.class)
    void deleteEndedExcursions();

    @Transactional(isolation = Isolation.SERIALIZABLE, rollbackFor = ServiceException.class)
    void deleteExcursionsByNotExistPlaces();
}
