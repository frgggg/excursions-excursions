package com.excursions.excursions.service.impl;

import com.excursions.excursions.exception.ServiceException;
import com.excursions.excursions.model.Excursion;
import com.excursions.excursions.repository.ExcursionRepository;
import com.excursions.excursions.service.ExcursionService;
import com.excursions.excursions.service.PlaceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.List;

import static com.excursions.excursions.exception.message.ExcursionServiceExceptionMessages.EXCURSION_SERVICE_EXCEPTION_SAVE_OR_UPDATE_EXIST_PLACE;

@Slf4j
@Service
public class ExcursionServiceImpl implements ExcursionService {

    private static final String SERVICE_NAME = "ExcursionServiceImpl";

    private ExcursionRepository excursionRepository;
    private EntityManager entityManager;
    private PlaceService placeService;

    @Value("${excursion.ended.after-day}")
    private String deleteEndedExcursionsAfterDay;

    @Autowired
    protected ExcursionServiceImpl(ExcursionRepository excursionRepository, EntityManager entityManager, PlaceService placeService) {
        this.excursionRepository = excursionRepository;
        this.entityManager = entityManager;
        this.placeService = placeService;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE, rollbackFor = ServiceException.class)
    @Override
    public Excursion save(String name, LocalDateTime start, LocalDateTime stop, Integer peopleCount, Long coinsCost, List<Long> placesIds) {
        Excursion excursionForSave = new Excursion(name, start, stop, peopleCount, coinsCost, placesIds);
        return saveUtil(excursionForSave);
    }

    @Override
    public void deleteEndedExcursions() {

    }

    @Transactional(isolation = Isolation.SERIALIZABLE, rollbackFor = ServiceException.class)
    @Override
    public void deleteNotEndedExcursionsByNotExistPlaces() {
        List<Long> allPlacesIds = excursionRepository.getAllPlacesIds();
        System.out.println("allPlacesIds = " + allPlacesIds);

        if(allPlacesIds != null) {
            if(allPlacesIds.size() > 0) {
                List<Long> notExistPlacesIds = placeService.getNotExistPlacesIds(allPlacesIds);
                System.out.println("notExistPlacesIds = " + notExistPlacesIds);

                if (notExistPlacesIds.size() > 0) {
                    //List<Long> notEndedExcursionsIdsByNotExistPlaces = excursionRepository.getNotEndedExcursionsIdsByNotExistPlaces(notExistPlacesIds);//, LocalDateTime.now());
                    List<Excursion> notEndedExcursionsIdsByNotExistPlaces = excursionRepository.findByPlacesIdsInAndStartAfter(notExistPlacesIds, LocalDateTime.now());
                    for(Excursion e: notEndedExcursionsIdsByNotExistPlaces){
                        System.out.println("exc = " + e);
                    }
                }
            }
        }
    }

    private Excursion saveUtil(Excursion excursionForSave) {
        Excursion savedExcursion;
        try {
            savedExcursion = excursionRepository.save(excursionForSave);
            entityManager.flush();
        } catch (ConstraintViolationException e) {
            throw new ServiceException(SERVICE_NAME, e.getConstraintViolations().iterator().next().getMessage());
        } catch (PersistenceException e) {
            throw new ServiceException(SERVICE_NAME, EXCURSION_SERVICE_EXCEPTION_SAVE_OR_UPDATE_EXIST_PLACE);
        }

        return savedExcursion;
    }
}
