package com.excursions.excursions.repository;

import com.excursions.excursions.model.Ticket;
import org.springframework.data.repository.CrudRepository;

public interface TicketRepository extends CrudRepository<Ticket, Long> {
}
