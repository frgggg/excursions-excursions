package com.excursions.excursions.service.impl;

import com.excursions.excursions.exception.ServiceException;
import com.excursions.excursions.model.Excursion;
import com.excursions.excursions.model.Ticket;
import com.excursions.excursions.repository.TicketRepository;
import com.excursions.excursions.service.ExcursionService;
import com.excursions.excursions.service.TicketService;
import com.excursions.excursions.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.validation.ConstraintViolationException;

import static com.excursions.excursions.exception.message.TicketServiceExceptionMessages.TICKET_SERVICE_EXCEPTION_WRONG_COST;
import static com.excursions.excursions.log.message.TicketServiceLogMessages.TICKET_SERVICE_LOG_NEW_TICKET;

@Slf4j
@Service
public class TicketServiceImpl implements TicketService {

    private static final String SERVICE_NAME = "TicketServiceImpl";

    private EntityManager entityManager;
    private TicketRepository ticketRepository;
    private ExcursionService excursionService;
    private UserService userService;

    protected TicketServiceImpl(TicketRepository ticketRepository, EntityManager entityManager, ExcursionService excursionService, UserService userService) {
        this.ticketRepository = ticketRepository;
        this.entityManager = entityManager;
        this.excursionService = excursionService;
        this.userService = userService;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE, rollbackFor = ServiceException.class)
    @Override
    public Ticket create(Long userId, Long excursionId, Long expectedCoinsCost) {
        Excursion excursion = excursionService.findById(excursionId);
        if(!expectedCoinsCost.equals(excursion.getCoinsCost())) {
            throw new ServiceException(SERVICE_NAME, TICKET_SERVICE_EXCEPTION_WRONG_COST);
        }

        //TODO check peopleCount

        Ticket ticketForSave = new Ticket(excursionId, expectedCoinsCost, userId);
        Ticket savedTicket = saveUtil(ticketForSave);
        userService.coinsDownByExcursion(userId, expectedCoinsCost);
        log.info(TICKET_SERVICE_LOG_NEW_TICKET, savedTicket);
        return savedTicket;
    }

    private Ticket saveUtil(Ticket ticketForSave) {
        Ticket savedTicket;
        try {
            savedTicket = ticketRepository.save(ticketForSave);
            entityManager.flush();
        } catch (ConstraintViolationException e) {
            throw new ServiceException(SERVICE_NAME, e.getConstraintViolations().iterator().next().getMessage());
        }

        return savedTicket;
    }
}
