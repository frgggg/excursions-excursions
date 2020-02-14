package com.excursions.excursions.validation.impl;

import com.excursions.excursions.exception.ServiceException;
import com.excursions.excursions.model.Excursion;
import com.excursions.excursions.service.PlaceService;
import com.excursions.excursions.validation.ExcursionValidation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;
import java.util.List;

import static com.excursions.excursions.model.Excursion.*;

public class ExcursionValidationImpl implements ConstraintValidator<ExcursionValidation, Excursion> {

    private static PlaceService placeService = null;

    public static void initPlaceService(PlaceService placeService) {
        if(ExcursionValidationImpl.placeService == null) {
            ExcursionValidationImpl.placeService = placeService;
        }
    }

    @Override
    public boolean isValid(Excursion excursion, ConstraintValidatorContext context) {

        LocalDateTime start = excursion.getStart();
        LocalDateTime stop = excursion.getStop();
        List<Long> places = excursion.getPlaces();

        if(start == null) {
            setContextForWrongStart(context);
            return false;
        } else if(start.isBefore(LocalDateTime.now())) {
            setContextForWrongStart(context);
            return false;
        }

        if(stop == null) {
            setContextForWrongStop(context);
            return false;
        } else if(stop.isBefore(start)) {
            setContextForWrongStop(context);
            return false;
        }

        if(places == null) {
            setContextForWrongPlaceList(context);
            return false;
        }

        try {
            if (placeService.getNotExistPlacesIds(places).size() > 0) {
                setContextForWrongPlaceList(context);
                return false;
            }
        } catch (ServiceException e) {
            setContextForWrongPlaceList(context);
            return false;
        }

        return true;
    }

    private void setContextForWrongStart(ConstraintValidatorContext context) {
        context.disableDefaultConstraintViolation();
        context
                .buildConstraintViolationWithTemplate(EXCURSION_START_VALIDATION_MESSAGE)
                .addConstraintViolation();
    }

    private void setContextForWrongStop(ConstraintValidatorContext context) {
        context.disableDefaultConstraintViolation();
        context
                .buildConstraintViolationWithTemplate(EXCURSION_STOP_VALIDATION_MESSAGE)
                .addConstraintViolation();
    }

    private void setContextForWrongPlaceList(ConstraintValidatorContext context) {
        context.disableDefaultConstraintViolation();
        context
                .buildConstraintViolationWithTemplate(EXCURSION_PLACES_VALIDATION_MESSAGE)
                .addConstraintViolation();
    }
}
