package com.excursions.excursions.validation.impl;

import com.excursions.excursions.client.PlaceClient;
import com.excursions.excursions.model.Excursion;
import com.excursions.excursions.validation.ExcursionValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;
import java.util.List;

import static com.excursions.excursions.model.Excursion.*;

public class ExcursionValidationImpl implements ConstraintValidator<ExcursionValidation, Excursion> {

    @Autowired
    private PlaceClient placeClient;

    @Override
    public void initialize(ExcursionValidation constraintAnnotation) {
        SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
    }

    @Override
    public boolean isValid(Excursion excursion, ConstraintValidatorContext context) {

        LocalDateTime start = excursion.getStart();
        LocalDateTime stop = excursion.getStop();
        List<Long> places = excursion.getPlaces();

        if(start == null) {
            context.disableDefaultConstraintViolation();
            context
                    .buildConstraintViolationWithTemplate(EXCURSION_START_VALIDATION_MESSAGE)
                    .addConstraintViolation();
            return false;
        }

        if(start.isBefore(LocalDateTime.now())) {
            context.disableDefaultConstraintViolation();
            context
                    .buildConstraintViolationWithTemplate(EXCURSION_START_VALIDATION_MESSAGE)
                    .addConstraintViolation();
            return false;
        }

        if(stop == null) {
            context.disableDefaultConstraintViolation();
            context
                    .buildConstraintViolationWithTemplate(EXCURSION_STOP_VALIDATION_MESSAGE)
                    .addConstraintViolation();
            return false;
        }

        if(stop.isBefore(start)) {
            context.disableDefaultConstraintViolation();
            context
                    .buildConstraintViolationWithTemplate(EXCURSION_STOP_VALIDATION_MESSAGE)
                    .addConstraintViolation();
            return false;
        }

        if(places == null) {
            context.disableDefaultConstraintViolation();
            context
                    .buildConstraintViolationWithTemplate(EXCURSION_PLACES_VALIDATION_MESSAGE)
                    .addConstraintViolation();
            return false;
        }

        System.out.println("placeClient = " + placeClient);
        System.out.println("places = " + places);
        System.out.println("placeClient.check(places) = " + placeClient.check(places));

        if(placeClient.check(places).size() > 0) {
            context.disableDefaultConstraintViolation();
            context
                    .buildConstraintViolationWithTemplate(EXCURSION_PLACES_VALIDATION_MESSAGE)
                    .addConstraintViolation();
            return false;
        }

        return true;
    }
}
