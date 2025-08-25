package com.github.drr00t.gra.boundary.endpoint;

import java.util.List;

public record AwardNomineeMinAndMaxIntervalResponse(List<AwardWinResponse> min, List<AwardWinResponse> max) {
}
