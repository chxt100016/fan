package com.chxt.tennis.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Tournament {
    private String tournamentId;
    private String name;
    private String surface;
    private String category;
    private String city;
    private String country;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer year;
    private String status;
}
