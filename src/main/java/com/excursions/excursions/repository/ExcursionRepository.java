package com.excursions.excursions.repository;

import com.excursions.excursions.model.Excursion;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ExcursionRepository extends CrudRepository<Excursion, Long> {

    @Query(value = "select distinct places from  excursion_places", nativeQuery = true)
    List<Long> getAllPlaces();

    //select * from excursion where (stop < '2020-02-21 12:40:00.328' and stop > '2020-02-19 12:45:00.328');
    //select * from excursion where (id in (select ep.excursion_id from excursion_places ep where (ep.places in ( 4))));


    //void deleteExcursionsByPlacesAndNot(List<Long> places);

    //void deleteEndedExcursions(LocalDateTime timeAfterDelete);
}
