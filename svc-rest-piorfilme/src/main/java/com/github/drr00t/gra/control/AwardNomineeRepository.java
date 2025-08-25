package com.github.drr00t.gra.control;

import com.github.drr00t.gra.boundary.endpoint.AwardNomineeResponse;
import com.github.drr00t.gra.boundary.endpoint.AwardWinResponse;

import java.util.List;

public interface AwardNomineeRepository {
    List<AwardNomineeResponse> getAwardNomineesByYear(int year);

    List<AwardWinResponse> getProducerWinsOrderedByInterval(boolean orderByMin);
}
