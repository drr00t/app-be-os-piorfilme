package com.github.drr00t.gra;

import com.github.drr00t.gra.boundary.CsvLoadAwardNomineesLoader;
import com.github.drr00t.gra.boundary.entity.AwardNominee;
import io.quarkus.runtime.StartupEvent;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
class AwardsResourceTest {
    @ConfigProperty(name = "csv.file.path")
    File csvFile; // Configure this in application.properties

    @ConfigProperty(name = "csv.headers")
    List<String> headers;

    @Test
    void testLoadAwardCsvFile() {
        try (Reader reader = new FileReader(csvFile)) {
            CSVFormat csvFormat = CSVFormat.DEFAULT.builder()
                    .setHeader(headers.toArray(new String[0]))
                    .setDelimiter(';')
                    .setSkipHeaderRecord(true)
                    .build();

            Iterable<CSVRecord> records = csvFormat.parse(reader);
            for (CSVRecord record : records) {
                Pattern pattern = Pattern.compile(",|\\s+and\\s+");
                int year = Integer.parseInt(record.get("year").trim());
                String title = record.get("title").trim();
                String studios = record.get("studios").trim();
                String producers = record.get("producers").trim();
                String winner = record.get("winner").trim();

                List<AwardNominee> nominees = Arrays.stream(pattern.split(producers))
                        .map(producer -> AwardNominee.of(year, title, studios, producer, winner))
                        .toList();

                Assertions.assertFalse(nominees.isEmpty());

            }

        } catch (IOException e) {
            Assertions.fail();
        }
    }

    @Test
    void testAwardsGetProducersJustMinAndMaxEndpoint() {
        given()
          .when().get("/awards/producers/intervals/max-and-min")
          .then()
             .statusCode(200)
             .body(is("{\"min\":[" +
                 "{\"producer\":\"Producer 1\",\"interval\":1,\"previousWin\":2008,\"followingWin\":2009}," +
                 "{\"producer\":\"Producer 2\",\"interval\":1,\"previousWin\":2018,\"followingWin\":2019}" +
                 "],\"max\":[" +
                 "{\"producer\":\"Producer 1\",\"interval\":99,\"previousWin\":1900,\"followingWin\":1999}," +
                 "{\"producer\":\"Producer 2\",\"interval\":99,\"previousWin\":2000,\"followingWin\":2099}" +
                 "]}"));
    }

}