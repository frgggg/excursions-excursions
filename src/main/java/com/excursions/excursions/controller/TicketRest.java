package com.excursions.excursions.controller;

import com.excursions.excursions.dto.TicketDto;
import com.excursions.excursions.model.Ticket;
import com.excursions.excursions.service.ExcursionService;
import com.excursions.excursions.service.TicketService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.excursions.excursions.log.message.TicketControllerLogMessages.*;

@Slf4j
@RestController
@RequestMapping("/ticket")
public class TicketRest {
    private TicketService ticketService;
    private ModelMapper modelMapper;

    @Autowired
    protected TicketRest(TicketService ticketService, ModelMapper modelMapper) {
        this.ticketService = ticketService;
        this.modelMapper = modelMapper;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TicketDto create(
            @RequestParam(name = "user-id", required = true) Long userId,
            @RequestParam(name = "excursion-id", required = true) Long excursionId,
            @RequestParam(name = "coins", required = true) Long coins
    ) {
        Ticket ticket = ticketService.create(
                userId,
                excursionId,
                coins
        );

        TicketDto savedTicketDto = modelMapper.map(ticket, TicketDto.class);

        log.info(TICKET_CONTROLLER_LOG_NEW_TICKET, savedTicketDto);
        return savedTicketDto;
    }

    @GetMapping("/for-user")
    public List<TicketDto> findTicketsForUser(@RequestParam(name = "user-id", required = true) Long userId) {
        List<Ticket> tickets = ticketService.findTicketsCountForUserById(userId);
        List<TicketDto> ticketDtos = null;
        if(tickets != null) {
            if(tickets.size() > 0) {
                ticketDtos = tickets
                        .stream()
                        .map(ticket -> modelMapper.map(ticket, TicketDto.class))
                        .collect(Collectors.toList());
            }
        }

        if(ticketDtos == null) {
            ticketDtos = new ArrayList<TicketDto>();
        }
        log.info(TICKET_CONTROLLER_LOG_FIND_TICKETS_FOR_USER, ticketDtos, userId);
        return ticketDtos;
    }

    @GetMapping
    public List<TicketDto> findAll() {
        List<Ticket> tickets = ticketService.findAll();
        List<TicketDto> ticketDtos = null;

        if(tickets != null) {
            if(tickets.size() > 0) {
                ticketDtos = tickets
                        .stream()
                        .map(ticket -> modelMapper.map(ticket, TicketDto.class))
                        .collect(Collectors.toList());
            }
        }

        if(ticketDtos == null) {
            ticketDtos = new ArrayList<TicketDto>();
        }

        log.info(TICKET_CONTROLLER_LOG_FIND_ALL);
        return ticketDtos;
    }

    @GetMapping(value = "/{id}")
    public TicketDto findById(@PathVariable("id") Long id) {
        Ticket ticket = ticketService.findById(id);
        TicketDto ticketDto =  modelMapper.map(ticket, TicketDto.class);
        log.info(TICKET_CONTROLLER_LOG_FIND_EXCURSION, ticketDto);
        return ticketDto;
    }

    @DeleteMapping(value = "/{id}")
    public void deleteByUser(@PathVariable("id") Long id) {
        ticketService.setActiveTicketsAsDropByUser(id);
        log.info(TICKET_CONTROLLER_LOG_TICKET_DROP_BY_USER, id);
    }
}
