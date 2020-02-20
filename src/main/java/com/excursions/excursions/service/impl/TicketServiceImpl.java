package com.excursions.excursions.service.impl;

import com.excursions.excursions.exception.ServiceException;
import com.excursions.excursions.model.Excursion;
import com.excursions.excursions.model.Ticket;
import com.excursions.excursions.model.TicketState;
import com.excursions.excursions.repository.TicketRepository;
import com.excursions.excursions.service.ExcursionService;
import com.excursions.excursions.service.TicketService;
import com.excursions.excursions.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.validation.ConstraintViolationException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.excursions.excursions.exception.message.TicketServiceExceptionMessages.*;
import static com.excursions.excursions.log.message.TicketServiceLogMessages.*;

@Slf4j
@Service
public class TicketServiceImpl implements TicketService {

    private static final String SERVICE_NAME = "TicketServiceImpl";

    @Value("${ticket.drop-by-user-before-stop.day}")
    private String deleteByUserBeforeStartMinusDay;

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

        checkExcursionForStart(excursion);

        if(!expectedCoinsCost.equals(excursion.getCoinsCost())) {
            throw new ServiceException(SERVICE_NAME, TICKET_SERVICE_EXCEPTION_WRONG_COST);
        }

        if(!excursion.getEnableNewTickets()) {
            throw new ServiceException(SERVICE_NAME, TICKET_SERVICE_EXCEPTION_NEW_TICKET_NOT_ENABLE);
        }

        if(ticketRepository.countByExcursionIdAndStatus(excursionId, TicketState.ACTIVE) >= excursion.getPeopleCount()) {
            throw new ServiceException(SERVICE_NAME, TICKET_SERVICE_EXCEPTION_MAX_PEOPLE_COUNT);
        }

        Ticket ticketForSave = new Ticket(excursionId, expectedCoinsCost, userId);
        Ticket savedTicket = saveUtil(ticketForSave);
        userService.coinsDownByExcursion(userId, expectedCoinsCost);
        log.info(TICKET_SERVICE_LOG_NEW_TICKET, savedTicket);
        return savedTicket;
    }

    @Override
    public List<Ticket> findAll() {
        List<Ticket> tickets = new ArrayList<>();
        ticketRepository.findAll().forEach(tickets::add);
        log.info(TICKET_SERVICE_LOG_FIND_ALL);
        return null;
    }

    @Override
    public Ticket findById(Long id) {
        Optional<Ticket> optionalTicket = ticketRepository.findById(id);
        if(!optionalTicket.isPresent()) {
            throw new ServiceException(SERVICE_NAME, String.format(TICKET_SERVICE_EXCEPTION_NOT_EXIST_EXCURSION, id));
        }
        Ticket findByIdTicket = optionalTicket.get();
        log.info(TICKET_SERVICE_LOG_FIND_EXCURSION, findByIdTicket);
        return findByIdTicket;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE, rollbackFor = ServiceException.class)
    @Override
    public void setActiveTicketsAsDropByUser(Long id) {
        Ticket ticket = findById(id);
        if(!TicketState.ACTIVE.equals(ticket.getState())) {
            throw new ServiceException(SERVICE_NAME, TICKET_SERVICE_EXCEPTION_TICKET_IS_NOT_ACTIVE);
        }

        if(LocalDateTime.now().plusDays(new Integer(deleteByUserBeforeStartMinusDay)).isAfter(excursion.getStart())) {
            throw new ServiceException(SERVICE_NAME, TICKET_SERVICE_EXCEPTION_EXCURSION_STARTED);
        }

        ticket.setState(TicketState.DROP_BY_USER);
        saveUtil(ticket);
        log.info(TICKET_SERVICE_LOG_TICKET_DROP_BY_USER, ticket.getId());
    }

    @Transactional(isolation = Isolation.SERIALIZABLE, rollbackFor = ServiceException.class)
    @Override
    public void setActiveTicketsAsDropByNotEndedExcursions(Long id) {
        Ticket ticket = findById(id);
        if(!TicketState.ACTIVE.equals(ticket.getState())) {
            throw new ServiceException(SERVICE_NAME, TICKET_SERVICE_EXCEPTION_TICKET_IS_NOT_ACTIVE);
        }
        checkExcursionForStart(excursionService.findById(ticket.getExcursionId()));

        ticket.setState(TicketState.DROP_BY_NOT_ENDED_EXCURSION);
        saveUtil(ticket);
        log.info(TICKET_SERVICE_LOG_TICKET_DROP_BY_NOT_ENDED_EXCURSION, ticket.getId());
    }

    @Override
    public void deleteNotActiveTickets() {
        deleteNotActiveTicketsNoBackCoins();
        deleteNotActiveTicketsBackCoins();
        log.info(TICKET_SERVICE_LOG_DELETE_NOT_ACTIVE_TICKETS);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE, rollbackFor = ServiceException.class)
    @Override
    public void setActiveTicketsAsDropByEndedExcursions(List<Excursion> endedExcursions) {
        if(endedExcursions != null) {
            if (endedExcursions.size() > 0) {
                List<Long> endedExcursionIds = endedExcursions.stream().map(Excursion::getId).collect(Collectors.toList());
                List<Ticket> tickets = ticketRepository.findByExcursionIdInAndState(endedExcursionIds, TicketState.ACTIVE);
                if(tickets != null) {
                    if(tickets.size() > 0) {
                        for(int i = 0; i < tickets.size(); i++) {
                            Ticket ticket = tickets.get(i);
                            ticket.setState(TicketState.DROP_BY_ENDED_EXCURSION);
                            tickets.set(i, ticket);
                        }
                    }
                }
            }
        }

        log.info(TICKET_SERVICE_LOG_TICKET_DROP_BY_ENDED_EXCURSIONS, endedExcursionIds);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE, rollbackFor = ServiceException.class)
    @Override
    public void setActiveTicketsAsDropByWrongExcursions(List<Excursion> wrongExcursions) {
        if(wrongExcursions != null) {
            if(wrongExcursions.size() > 0) {
                List<Long> wrongExcursionsIds = wrongExcursions.stream().map(Excursion::getId).collect(Collectors.toList());
                List<Ticket> tickets = ticketRepository.findByExcursionIdInAndState(wrongExcursionsIds, TicketState.ACTIVE);
                if(tickets != null) {
                    if(tickets.size() > 0) {
                        for(int i = 0; i < tickets.size(); i++) {
                            Ticket ticket = tickets.get(i);
                            ticket.setState(TicketState.DROP_BY_WRONG_EXCURSION);
                            tickets.set(i, ticket);
                        }
                    }
                }
            }
        }

        log.info(TICKET_SERVICE_LOG_TICKET_DROP_BY_WRONG_EXCURSIONS, wrongExcursionsIds);
    }

    @Override
    public Long findTicketsCountForUserById(Long userId) {
        Long count = ticketRepository.countByUserId(userId);
        log.info(TICKET_SERVICE_LOG_FIND_TICKETS_COUNT_FOR_USER, count, userId);
        return count;
    }

    private void deleteNotActiveTicketsBackCoins() {
        ArrayList<TicketState> ticketStatesNoBackCoins = new ArrayList<>();
        ticketStatesNoBackCoins.add(TicketState.ACTIVE);
        ticketStatesNoBackCoins.add(TicketState.DROP_BY_ENDED_EXCURSION);

        List<Ticket> tickets = ticketRepository.findByStateNotIn(ticketStatesNoBackCoins);
        List<Ticket> ticketsForDelete = new ArrayList<>();

        //userService.coinsUpByExcursion(t.getUserId(), t.getCoinsCost());
        for(Ticket t: tickets) {

        }
     }

    @Transactional(isolation = Isolation.SERIALIZABLE, rollbackFor = ServiceException.class)
    private void deleteNotActiveTicketsNoBackCoins() {
        ticketRepository.deleteByState(TicketState.DROP_BY_ENDED_EXCURSION);
    }

    private void checkExcursionForStart(Excursion excursion) {
        if(LocalDateTime.now().isAfter(excursion.getStart())) {
            throw new ServiceException(SERVICE_NAME, TICKET_SERVICE_EXCEPTION_EXCURSION_STARTED);
        }
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
