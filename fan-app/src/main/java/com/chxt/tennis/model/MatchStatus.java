package com.chxt.tennis.model;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum MatchStatus {
    FINISHED("F"),
    PENDING("S"),
    COMING("C"),
    LIVE("P"),
    UPCOMING("U");

    private final String code;

    MatchStatus(String code) {
        this.code = code;
    }

    public static MatchStatus fromCode(String code) {
        if (code == null) return null;
        return Arrays.stream(values())
                .filter(s -> s.code.equals(code))
                .findFirst()
                .orElse(null);
    }

    public static String toStatus(String code) {
        MatchStatus status = fromCode(code);
        return status != null ? status.name() : null;
    }
}
