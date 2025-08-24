package com.github.drr00t.gra.boundary;

import com.github.drr00t.gra.boundary.entity.AwardNominee;
import com.github.drr00t.gra.control.AwardNomineesLoaderRepository;
import io.agroal.api.AgroalDataSource;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import java.util.List;

@ApplicationScoped
public class AgroalAwardNomineesRepository implements AwardNomineesLoaderRepository {
    private static final Logger LOGGER = Logger.getLogger(AgroalAwardNomineesRepository.class);

    @ConfigProperty(name = "quarkus.datasource.jdbc.url")
    String jdbcUrl;

    @Inject
    AgroalDataSource dataSource;

    @Override
    public void loadAwardNominees(List<AwardNominee> awardNominees) {
        LOGGER.info(String.format("Conectando ao banco de dados: %s", awardNominees.size()));
    }
}
