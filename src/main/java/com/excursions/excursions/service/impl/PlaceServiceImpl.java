package com.excursions.excursions.service.impl;

import com.excursions.excursions.repository.PlaceRepository;
import com.excursions.excursions.service.PlaceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.excursions.excursions.exception.ServiceException.serviceExceptionWrongInputArgs;
import static com.excursions.excursions.exception.ServiceException.serviceExceptionWrongResponse;
import static com.excursions.excursions.log.message.ServiceLogMessages.SERVICE_LOG_GET_NOT_EXIST_ENTITIES_IDS;

@Slf4j
@Service
public class PlaceServiceImpl implements PlaceService {

    private String SERVICE_NAME = "PlaceServiceImpl";

    private PlaceRepository placeClientService;

    @Autowired
    protected PlaceServiceImpl(PlaceRepository placeClientService) {
        this.placeClientService = placeClientService;
    }

    @Override
    public List<Long> getNotExistPlacesIds(List<Long> placesIdsForCheck) {

        if(placesIdsForCheck == null) {
            throw serviceExceptionWrongInputArgs(SERVICE_NAME, SERVICE_LOG_GET_NOT_EXIST_ENTITIES_IDS);
        } else if(placesIdsForCheck.size() < 1) {
            throw serviceExceptionWrongInputArgs(SERVICE_NAME, SERVICE_LOG_GET_NOT_EXIST_ENTITIES_IDS);
        }

        List<Long> notExistPlacesIds;

        try {
           notExistPlacesIds = placeClientService.getNotExistPlacesIds(placesIdsForCheck);
        } catch (IllegalStateException e) {
            throw serviceExceptionWrongResponse(SERVICE_NAME, SERVICE_LOG_GET_NOT_EXIST_ENTITIES_IDS, e.getMessage());
        }

        log.debug(SERVICE_NAME, SERVICE_LOG_GET_NOT_EXIST_ENTITIES_IDS);

        return notExistPlacesIds;
    }
}
