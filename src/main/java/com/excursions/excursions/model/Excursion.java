package com.excursions.excursions.model;

import com.excursions.excursions.validation.ExcursionValidation;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.List;

import static com.excursions.excursions.validation.message.ValidationMessagesComponents.*;

@Data
@ExcursionValidation
@Entity
@Table(
        name = "excursion",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"name", "start"})}
)
public class Excursion {

    public static final String EXCURSION_NAME_FIELD_NAME = "name";
    public static final int EXCURSION_NAME_LEN_MIN = 1;
    public static final int EXCURSION_NAME_LEN_MAX = 90;
    public static final String EXCURSION_NAME_VALIDATION_MESSAGE =
            EXCURSION_NAME_FIELD_NAME + STRING_FIELD_NOTNULL_MIN_MAX + EXCURSION_NAME_LEN_MIN + STRING_FIELD_NOTNULL_MIN_MAX_DIVIDE + EXCURSION_NAME_LEN_MAX;

    public static final String EXCURSION_START_FIELD_NAME = "start";
    public static final String EXCURSION_START_VALIDATION_MESSAGE = EXCURSION_START_FIELD_NAME + LOCAL_DATA_TIME_FIELD_NOTNULL_AFTER_NOW;

    public static final String EXCURSION_STOP_FIELD_NAME = "stop";
    public static final String EXCURSION_STOP_VALIDATION_MESSAGE = EXCURSION_STOP_FIELD_NAME + LOCAL_DATA_TIME_FIELD_NOTNULL_AFTER_ANOTHER_TIME + EXCURSION_START_FIELD_NAME;

    public static final String EXCURSION_PEOPLE_COUNT_FIELD_NAME = "peopleCount";
    public static final int EXCURSION_PEOPLE_COUNT_VALUE_MIN = 1;
    public static final int EXCURSION_PEOPLE_COUNT_VALUE_MAX = 100;
    public static final String EXCURSION_PEOPLE_COUNT_VALIDATION_MESSAGE =
            EXCURSION_PEOPLE_COUNT_FIELD_NAME + INTEGER_FIELD_NOTNULL_MIN_MAX + EXCURSION_PEOPLE_COUNT_VALUE_MIN + INTEGER_FIELD_NOTNULL_MIN_MAX_DIVIDE + EXCURSION_PEOPLE_COUNT_VALUE_MAX;

    public static final String EXCURSION_PLACES_FIELD_NAME = "places";
    public static final String EXCURSION_PLACES_VALIDATION_MESSAGE = EXCURSION_PLACES_FIELD_NAME + LIST_ID_FIELD_NOTNULL_NOT_EMPTY_EXIST;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "name", length = EXCURSION_NAME_LEN_MAX, nullable = false)
    @NotNull(message = EXCURSION_NAME_VALIDATION_MESSAGE)
    @Size(min = EXCURSION_NAME_LEN_MIN, max = EXCURSION_NAME_LEN_MAX, message = EXCURSION_NAME_VALIDATION_MESSAGE)
    private String name;

    @Column(name = "start", nullable = false)
    private LocalDateTime start;

    @Column(name = "stop", nullable = false)
    private LocalDateTime stop;

    @Column(name = "people_count", nullable = false)
    @Min(value = EXCURSION_PEOPLE_COUNT_VALUE_MIN, message = EXCURSION_PEOPLE_COUNT_VALIDATION_MESSAGE)
    @Max(value = EXCURSION_PEOPLE_COUNT_VALUE_MAX, message = EXCURSION_PEOPLE_COUNT_VALIDATION_MESSAGE)
    private Integer peopleCount;

    @ElementCollection(targetClass = Long.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "excursion_places", joinColumns = @JoinColumn(name = "excursion_id"))
    private List<Long> places;

    protected Excursion() {}

    public Excursion(String name,  LocalDateTime start,  LocalDateTime stop, Integer peopleCount, List<Long> places) {
        this.name = name;
        this.start = start;
        this.stop = stop;
        this.peopleCount = peopleCount;
        this.places = places;
    }
}
