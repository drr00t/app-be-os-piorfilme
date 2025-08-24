package com.github.drr00t.gra.boundary;

import com.github.drr00t.gra.boundary.endpoint.AwardNomineeResponse;
import com.github.drr00t.gra.boundary.entity.AwardNominee;
import com.github.drr00t.gra.control.AwardNomineeRepository;
import com.github.drr00t.gra.control.AwardNomineesLoaderRepository;
import io.agroal.api.AgroalDataSource;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class AgroalAwardNomineesRepository implements AwardNomineesLoaderRepository, AwardNomineeRepository {
    private static final Logger LOGGER = Logger.getLogger(AgroalAwardNomineesRepository.class);

    @ConfigProperty(name = "quarkus.datasource.jdbc.url")
    String jdbcUrl;

    @Inject
    AgroalDataSource dataSource;

    @Override
    public void loadAwardNominees(List<AwardNominee> awardNominees) {
        LOGGER.info(String.format("Conectando ao banco de dados: %s", awardNominees.size()));

        String insertNomineesBatchFormat = "INSERT INTO Award_Nominees (year, title, studios, producer, winner) VALUES %s";
        String insertValues = awardNominees.stream()
                .map(nominee -> String.format("(%d, '%s', '%s', '%s', '%s')",
                        nominee.year(),
                        nominee.title().replace("'", "''"),
                        nominee.studios().replace("'", "''"),
                        nominee.producer().replace("'", "''"),
                        nominee.winner()))
                .reduce((a, b) -> a + ", " + b)
                .orElse("");

        try (var conn = dataSource.getConnection();
             var stmt = conn.createStatement()) {

            LOGGER.info(String.format("SQL: %s", String.format(insertNomineesBatchFormat,insertValues)));

            stmt.addBatch( String.format(insertNomineesBatchFormat,insertValues));
            stmt.executeBatch();

        } catch (SQLException e) {
            LOGGER.error("Erro ao conectar ou operar no banco de dados: ", e);
            throw new RuntimeException("Erro ao conectar ou operar no banco de dados.", e);
        }
    }

    @Override
    public List<AwardNomineeResponse> getAwardNomineesByYear(int fromYear) {
        String selectNomineesByYearFormat = "SELECT year, title, studios, producer, winner FROM Award_Nominees WHERE year = %d";
        try (var conn = dataSource.getConnection();
             var stmt = conn.createStatement()) {

            LOGGER.info(String.format("SQL: %s", String.format(selectNomineesByYearFormat,fromYear)));

            ResultSet result = stmt.executeQuery( String.format(selectNomineesByYearFormat,fromYear));
            ArrayList<AwardNomineeResponse> nominees = new ArrayList<>();
            while(result.next())
            {
                // read the result set
                int year = result.getInt("year");
                String title = result.getString("title");
                String studios = result.getString("studios");
                String producer = result.getString("producer");
                String winner = result.getString("winner");
                nominees.add(new AwardNomineeResponse(year, title, studios, producer, winner));
            }

            return nominees;

        } catch (SQLException e) {
            LOGGER.error("Erro ao conectar ou operar no banco de dados: ", e);
            throw new RuntimeException("Erro ao conectar ou operar no banco de dados.", e);
        }
    }

    @Override
    public List<AwardNomineeResponse> getAwardNomineesWinners() {
        return List.of();
    }
}
