package com.github.drr00t.gra.boundary;

import com.github.drr00t.gra.boundary.entity.AwardNominee;
import com.github.drr00t.gra.control.AwardNomineesLoaderRepository;
import com.github.drr00t.gra.control.AwardNomineesRepository;
import io.agroal.api.AgroalDataSource;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class AgroalAwardNomineesRepository implements AwardNomineesRepository, AwardNomineesLoaderRepository {
    @ConfigProperty(name = "quarkus.datasource.jdbc.url")
    String jdbcUrl;

    @Inject
    AgroalDataSource dataSource;

    @Override
    public void loadAwardNominee(AwardNominee awardNominee) {

    }
}
