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
public class Player {
    private String playerId;
    private String firstName;
    private String lastName;
    private String nationality;
    private Integer rank;
    private LocalDate birthDate;
    private String gender;
    private String hand;
}
