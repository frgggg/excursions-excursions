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
    @ResponseBody
    public ExcursionDto create(@Validated @RequestBody ExcursionDto excursionDto) {
        Excursion excursion = excursionService.save(
                excursionDto.getName(),
                excursionDto.getStart(),
                excursionDto.getStop(),
                excursionDto.getPeopleCount(),
                excursionDto.getPlaces()
        );

        ExcursionDto savedExcursionDto = modelMapper.map(excursion, ExcursionDto.class);
        return savedExcursionDto;
    }

}
