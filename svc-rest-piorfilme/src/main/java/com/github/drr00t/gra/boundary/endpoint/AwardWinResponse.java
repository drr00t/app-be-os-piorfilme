package com.github.drr00t.gra.boundary.endpoint;

public record AwardWinResponse(String producer, int interval, int previousWin, int followingWin) {
}
