package com.chxt.tennis.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Player {
    private String playerId;
    private String firstName;
    private String lastName;
    private String fullName;
    private String nationality;
    private String countryCode;
    private Integer rank;
}
