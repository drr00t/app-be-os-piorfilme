package com.github.drr00t.gra.boundary;

import com.github.drr00t.gra.boundary.endpoint.AwardNomineeMinAndMaxIntervalResponse;
import com.github.drr00t.gra.boundary.endpoint.AwardNomineeResponse;
import com.github.drr00t.gra.boundary.endpoint.AwardWinResponse;
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
    public List<AwardNomineeResponse> getAwardNomineesByYear(int FROMYear) {
        String selectNomineesByYearFormat = "SELECT year, title, studios, producer, winner FROM Award_Nominees WHERE year = %d";
        try (var conn = dataSource.getConnection();
             var stmt = conn.createStatement()) {

            LOGGER.info(String.format("SQL: %s", String.format(selectNomineesByYearFormat,FROMYear)));

            ResultSet result = stmt.executeQuery( String.format(selectNomineesByYearFormat,FROMYear));
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
    public List<AwardWinResponse> getProducerWinsOrderedByInterval(boolean orderByMin) {

//    mudar firstPara previeus se no houve data de referncia usa a primeira, se passar usa que for passada
        String selectNomineesByYearFormat =
        "SELECT "
            + "Award_Nominees.producer,"
            + "(SELECT count(*) FROM Award_Nominees as f_s "
            +  "WHERE f_s.producer = Award_Nominees.producer and f_s.winner = \"yes\") as wins,"
            + "("
            + "(SELECT min(year) FROM Award_Nominees as f_s_nw "
            +       "WHERE f_s_nw.producer = Award_Nominees.producer and f_s_nw.winner = \"yes\" "
            +               "AND f_s_nw.year not in (SELECT min(year) FROM Award_Nominees as f_s_fw "
            +                      "WHERE f_s_fw.producer = Award_Nominees.producer AND f_s_fw.winner = \"yes\")) -"
            + "(SELECT min(year) FROM Award_Nominees as f_s_fw WHERE f_s_fw.producer = Award_Nominees.producer and f_s_fw.winner = \"yes\")) as interval,"
            + "(SELECT min(year) FROM Award_Nominees as f_s_fw WHERE f_s_fw.producer = Award_Nominees.producer and f_s_fw.winner = \"yes\") as previousWin,"
            + "(SELECT min(year) FROM Award_Nominees as f_s_nw WHERE f_s_nw.producer = Award_Nominees.producer and f_s_nw.winner = \"yes\" and f_s_nw.year not in (SELECT min(year) FROM Award_Nominees as f_s_fw WHERE f_s_fw.producer = Award_Nominees.producer AND f_s_fw.winner = \"yes\")) AS followingWin "
        + "FROM Award_Nominees "
        + "GROUP by Award_Nominees.producer HAVING interval > 0 "
        + "ORDER by interval " + (orderByMin ? "ASC" : "DESC");
        try (var conn = dataSource.getConnection();
             var stmt = conn.createStatement()) {

            LOGGER.info(String.format("SQL: %s", String.format(selectNomineesByYearFormat)));

            ResultSet result = stmt.executeQuery( String.format(selectNomineesByYearFormat));
            ArrayList<AwardWinResponse> awardWins = new ArrayList<>();
            while(result.next())
            {
                // read the result set
                String producer = result.getString("producer");
                int interval = result.getInt("interval");
                int previousWin = result.getInt("previousWin");
                int followingWin = result.getInt("followingWin");
                awardWins.add(new AwardWinResponse(producer, interval, previousWin, followingWin));
            }

            return awardWins;

        } catch (SQLException e) {
            LOGGER.error("Erro ao conectar ou operar no banco de dados: ", e);
            throw new RuntimeException("Erro ao conectar ou operar no banco de dados.", e);
        }
    }
}
