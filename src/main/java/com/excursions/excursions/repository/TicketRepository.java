package com.excursions.excursions.repository;

import com.excursions.excursions.model.Ticket;
import com.excursions.excursions.model.TicketState;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface TicketRepository extends CrudRepository<Ticket, Long> {
    @Query("SELECT COUNT(t) FROM Ticket t WHERE t.excursionId=?1 and t.state=" + TicketState.ACTIVE)
    Long getActiveTicketsCountForExcursion(Long excursionId);
}
