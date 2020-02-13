package com.excursions.excursions.repository;

import com.excursions.excursions.model.Excursion;
import org.springframework.data.repository.CrudRepository;

public interface ExcursionRepository extends CrudRepository<Excursion, Long> {
}
