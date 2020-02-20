package com.excursions.excursions.controller;

import com.excursions.excursions.dto.ExcursionDto;
import com.excursions.excursions.model.Excursion;
import com.excursions.excursions.service.ExcursionService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static com.excursions.excursions.log.message.ExcursionControllerLogMessages.*;

@Slf4j
@RestController
@RequestMapping("/excursion")
public class ExcursionRest {

    private ExcursionService excursionService;
    private ModelMapper modelMapper;

    @Autowired
    protected ExcursionRest(ExcursionService excursionService, ModelMapper modelMapper) {
        this.excursionService = excursionService;
        this.modelMapper = modelMapper;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ExcursionDto create(@Validated @RequestBody ExcursionDto excursionDto) {
        Excursion excursion = excursionService.save(
                excursionDto.getName(),
                excursionDto.getStart(),
                excursionDto.getStop(),
                excursionDto.getPeopleCount(),
                excursionDto.getCoinsCost(),
                excursionDto.getPlacesIds()
        );

        ExcursionDto savedExcursionDto = modelMapper.map(excursion, ExcursionDto.class);
        log.info(EXCURSION_CONTROLLER_LOG_NEW_EXCURSION, savedExcursionDto);
        return savedExcursionDto;
    }

    @GetMapping
    public List<ExcursionDto> getAll() {
        log.info(EXCURSION_CONTROLLER_LOG_FIND_ALL);
        return excursionService.findAll()
                .stream()
                .map(excursion -> modelMapper.map(excursion, ExcursionDto.class))
                .collect(Collectors.toList());
    }

    @GetMapping(value = "/{id}")
    public ExcursionDto get(@PathVariable("id") Long id) {
        Excursion excursion = excursionService.findById(id);
        log.info(EXCURSION_CONTROLLER_LOG_FIND_EXCURSION, excursion);
        return modelMapper.map(excursion, ExcursionDto.class);
    }

}
