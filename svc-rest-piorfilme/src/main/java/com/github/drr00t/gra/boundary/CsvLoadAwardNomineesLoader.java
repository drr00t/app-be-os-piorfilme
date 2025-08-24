package com.github.drr00t.gra.boundary;

import com.github.drr00t.gra.boundary.entity.AwardNominee;
import com.github.drr00t.gra.control.AwardNomineesLoaderRepository;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

@ApplicationScoped
public class CsvLoadAwardNomineesLoader {
    private static final Logger LOGGER = Logger.getLogger(CsvLoadAwardNomineesLoader.class);
    @ConfigProperty(name = "csv.file.path")
    File csvFile;

    @ConfigProperty(name = "csv.headers")
    List<String> headers;

    @Inject
    AwardNomineesLoaderRepository loadNomineesRepository;

    public void onStart(@Observes StartupEvent event) {
        LOGGER.info("Iniciando a leitura do arquivo CSV: " + csvFile.getAbsolutePath());

        readMovieList();

        LOGGER.info("Carga dos indicados ao premio concluida.");
    }

    private void readMovieList() {
        try (Reader reader = new FileReader(csvFile)) {
            CSVFormat csvFormat = CSVFormat.DEFAULT.builder()
                    .setHeader(headers.toArray(new String[0]))
                    .setDelimiter(';')
                    .setSkipHeaderRecord(true)
                    .build();

            Iterable<CSVRecord> records = csvFormat.parse(reader);
            for (CSVRecord record : records) {
                List<AwardNominee> nominees =  processRecord(record);

                LOGGER.info(String.format("Registro com %d produtores", nominees.size()));

                loadNomineesRepository.loadAwardNominees(nominees);
            }

        } catch (IOException e) {
            LOGGER.error("Erro ao ler o arquivo CSV", e);
            throw new RuntimeException("arquivo Movielist.csv nao encontrado.", e);
        }
    }

    private List<AwardNominee> processRecord(CSVRecord record) {
        // Compile the pattern once
        Pattern pattern = Pattern.compile(",|\\s+and\\s+");

        int year = Integer.parseInt(record.get("year").trim());
        String title = record.get("title").trim();
        String studios = record.get("studios").trim();
        String producers = record.get("producers").trim();
        String winner = record.get("winner").trim();

        return Arrays.stream(pattern.split(producers))
                .map(producer -> AwardNominee.of(year, title, studios, producer, winner))
                .toList();
//        return List.of(AwardNominee.of(0, "title", "studios", "producer", "winner"));
    }
}
