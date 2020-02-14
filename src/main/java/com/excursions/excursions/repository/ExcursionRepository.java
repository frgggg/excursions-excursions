package com.excursions.excursions.repository;

import com.excursions.excursions.model.Excursion;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ExcursionRepository extends CrudRepository<Excursion, Long> {

    @Query(value = "select distinct places from  excursion_places", nativeQuery = true)
    List<Long> getAllPlaces();

    @Modifying
    @Query(value = "delete from excursion where " +
            "(stop < ?1) " +
            "and " +
            "(" +
                "id in (select ep.excursion_id from excursion_places ep where (ep.places in ?2))" +
            ")", nativeQuery = true)
    void deleteExcursionsByNotExistPlaces(LocalDateTime timeAfterDelete, List<Long> places);

    @Modifying
    @Query(value = "delete from  excursion" +
            " where (stop < ?1)", nativeQuery = true)
    void deleteEndedExcursions(LocalDateTime timeAfterDelete);
}
