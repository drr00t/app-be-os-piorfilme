package com.github.drr00t.gra.boundary.endpoint;

import com.github.drr00t.gra.control.AwardNomineeRepository;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.resteasy.reactive.RestQuery;

@Path("/awards")
public class AwardsResource {

    @Inject
    AwardNomineeRepository repository;

    @GET
    @Path("/producers/intervals/max-and-min")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProducersWithMinAndMaxAwardIntervals(@RestQuery int page , @RestQuery int size) {
        // TODO: Implement logic to return producer(s) with max and min intervals between awards
        return Response.ok().build();
    }

    @GET
    @Path("/producers/nominees")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getNomineesByYear(@RestQuery int year) {
        return Response.ok(repository.getAwardNomineesByYear(year)).build();
    }
}
