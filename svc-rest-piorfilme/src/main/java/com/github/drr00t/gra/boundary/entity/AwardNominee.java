package com.github.drr00t.gra.boundary.entity;

// entidade que representa um filme indicado ao premio
// ponto importante aqui ja temos apenas um produtor por indicaço de filme
// essa entidade e imutavel

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class AwardNominee {
    private final int year;
    private final String title;
    private final String studios;
    private final String producer;
    private final String winner;

    private AwardNominee(int year, String title, String studios, String producer, String winner) {
        this.year = year;
        this.title = title;
        this.studios = studios;
        this.producer = producer;
        this.winner = winner;
    }

    public int year() {
        return year;
    }

    public String title() {
        return title;
    }

    public String studios() {
        return studios;
    }

    public String producer() {
        return producer;
    }

    public String winner() {
        return winner;
    }

    // construço da entidade award nomiees lida do arquivo CSV
    public static AwardNominee of(int year, String title, String studios, String producers, String winner) {
        return new AwardNominee(year, title, studios, producers, winner);
    }
}
