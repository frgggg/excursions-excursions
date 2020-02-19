package com.excursions.excursions.service;

import com.excursions.excursions.model.Excursion;
import com.excursions.excursions.model.Ticket;

import java.util.List;

public interface TicketService {

    Ticket create(Long userId, Long excursionId, Long expectedCoinsCost);
    void setActiveTicketsAsDropByUser(Long id);

    void deleteNotActiveTickets();

    void setActiveTicketsAsDropByEndedExcursions(List<Excursion> endedExcursions);
    void setActiveTicketsAsDropByWrongExcursions(List<Excursion> wrongExcursions);

    void setActiveTicketsAsDropByNotEndedExcursions(Long id);
    Long findTicketsCountForUserById(Long id);
}
