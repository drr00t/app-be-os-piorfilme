package com.github.drr00t.gra.boundary;

import com.github.drr00t.gra.boundary.endpoint.AwardNomineeMinAndMaxIntervalResponse;
import com.github.drr00t.gra.boundary.endpoint.AwardWinResponse;
import com.github.drr00t.gra.control.AwardNomineeRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ApplicationScoped
public class AwardWinsServce {
    @Inject
    AwardNomineeRepository respoitory;

    public AwardNomineeMinAndMaxIntervalResponse getAwardWins() {
        List<AwardWinResponse> allWins = respoitory.getProducerWinsOrderedByInterval(true);

        Optional<AwardWinResponse> optMin = allWins.stream().min(Comparator.comparing(AwardWinResponse::interval));
        List<AwardWinResponse> listMins = optMin.stream().map(
                min -> allWins.stream()
                                    .filter(win -> win.interval() == min.interval())
                                    .collect(Collectors.toList())
                ).findAny().orElse(Collections.emptyList());


        Optional<AwardWinResponse> optMax = allWins.stream().max(Comparator.comparing(AwardWinResponse::interval));
        List<AwardWinResponse> listMax = optMax.stream().map(
                maxVal -> allWins.stream()
                                    .filter(win -> win.interval() == maxVal.interval())
                                    .collect(Collectors.toList())
                ).findAny().orElse(Collections.emptyList());

        return new AwardNomineeMinAndMaxIntervalResponse(listMins, listMax);
    }
}
