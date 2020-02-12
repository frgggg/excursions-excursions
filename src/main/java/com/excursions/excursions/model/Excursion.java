package com.excursions.excursions.model;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import java.time.LocalDateTime;

import static com.excursions.excursions.model.util.ValidationMessages.STRING_FIELD_NOTNULL_MIN_MAX;
import static com.excursions.excursions.model.util.ValidationMessages.STRING_FIELD_NOTNULL_MIN_MAX_DIVIDE;

@Data
@Entity
@Table(
        name = "excursion",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"name", "start_time"})}
)
public class Excursion {

    public static final String PLACE_NAME_FIELD_NAME = "name";
    public static final int PLACE_NAME_LEN_MIN = 1;
    public static final int PLACE_NAME_LEN_MAX = 90;
    public static final String PLACE_NAME_VALIDATION_MESSAGE =
            PLACE_NAME_FIELD_NAME + STRING_FIELD_NOTNULL_MIN_MAX + PLACE_NAME_LEN_MIN + STRING_FIELD_NOTNULL_MIN_MAX_DIVIDE + PLACE_NAME_LEN_MAX;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "name", length = PLACE_NAME_LEN_MAX, nullable = false)
    @NotNull(message = PLACE_NAME_VALIDATION_MESSAGE)
    @Size(min = PLACE_NAME_LEN_MIN, max = PLACE_NAME_LEN_MAX, message = PLACE_NAME_VALIDATION_MESSAGE)
    private String name;

    @Column(name = "start_time", nullable = false)
    @NotNull(message = )
    private LocalDateTime startTime;

    @Column(name = "stop_time", nullable = false)
    @NotNull(message = )
    private LocalDateTime stopTime;

}
