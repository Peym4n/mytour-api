package org.fhtw.mytourapi.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.fhtw.mytourapi.config.ImageStorageProperties;
import org.fhtw.mytourapi.config.OpenRouteServiceProperties;
import org.fhtw.mytourapi.dto.CoverImageDto;
import org.fhtw.mytourapi.dto.ImportResultDto;
import org.fhtw.mytourapi.dto.ImportedTourDto;
import org.fhtw.mytourapi.dto.TourDetailDto;
import org.fhtw.mytourapi.dto.TourExportDto;
import org.fhtw.mytourapi.dto.TourImportRequest;
import org.fhtw.mytourapi.dto.TourLogDto;
import org.fhtw.mytourapi.exception.ImportValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TourImportServiceTest {

    private final ObjectMapper objectMapper = new ObjectMapper()
            .findAndRegisterModules()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    @TempDir
    private Path tempDirectory;

    @Test
    void importToursRestoresExportedRoutesLogsWeatherAndComputedAttributes() throws Exception {
        TourFixture source = tourFixture();
        TourFixture target = tourFixture();
        TourImportRequest request = importRequest(source.exportService().exportTours());

        ImportResultDto result = target.importService().importTours(request);

        assertThat(result.importedTours()).isEqualTo(4);
        assertThat(result.importedLogs()).isEqualTo(9);
        assertThat(result.createdTourIds()).containsExactly(5L, 6L, 7L, 8L);

        TourDetailDto importedTour = target.tourService().getTour(5L).orElseThrow();
        assertThat(importedTour.name()).isEqualTo("Danube Island Evening Ride");
        assertThat(importedTour.plannedDistanceM()).isEqualByComparingTo("18200");
        assertThat(importedTour.estimatedDurationS()).isEqualTo(4200);
        assertThat(importedTour.route().routeSource()).isEqualTo("OPENROUTESERVICE");
        assertThat(importedTour.coverImage().path()).isEqualTo("intermediate/danube-island.jpg");
        assertThat(importedTour.computedAttributes().logCount()).isEqualTo(3);

        List<TourLogDto> importedLogs = target.logService().listLogs(5L).orElseThrow();
        assertThat(importedLogs).hasSize(3);
        assertThat(importedLogs)
                .anySatisfy((log) -> {
                    assertThat(log.performedAt()).isEqualTo(Instant.parse("2026-05-10T17:45:00Z"));
                    assertThat(log.weather().provider()).isEqualTo("OPEN_METEO");
                    assertThat(log.weather().weatherDescription()).isEqualTo("clear sky");
                    assertThat(log.weather().fetchedAt()).isEqualTo(Instant.parse("2026-05-10T18:00:30Z"));
                });
    }

    @Test
    void importRejectsUnsupportedSchemaBeforeCreatingTours() throws Exception {
        TourFixture fixture = tourFixture();
        TourImportRequest exportedRequest = importRequest(fixture.exportService().exportTours());
        TourImportRequest request = new TourImportRequest(99, exportedRequest.tours());

        assertThatThrownBy(() -> fixture.importService().importTours(request))
                .isInstanceOfSatisfying(ImportValidationException.class, (exception) ->
                        assertThat(exception.validationErrors())
                                .containsExactly("schemaVersion: unsupported version 99; expected 1"));

        assertThat(fixture.tourService().getTour(5L)).isEmpty();
    }

    @Test
    void importRejectsUnsafeCoverImagePath() throws Exception {
        TourFixture fixture = tourFixture();
        TourImportRequest exportedRequest = importRequest(fixture.exportService().exportTours());
        ImportedTourDto originalTour = exportedRequest.tours().get(0);
        ImportedTourDto unsafeTour = new ImportedTourDto(
                originalTour.tour(),
                originalTour.route(),
                new CoverImageDto("../outside.jpg", "outside.jpg", "image/jpeg", 12L),
                originalTour.plannedDistanceM(),
                originalTour.estimatedDurationS(),
                originalTour.logs()
        );
        TourImportRequest request = new TourImportRequest(1, List.of(unsafeTour));

        assertThatThrownBy(() -> fixture.importService().importTours(request))
                .isInstanceOfSatisfying(ImportValidationException.class, (exception) ->
                        assertThat(exception.validationErrors())
                                .containsExactly("tours[0].coverImage.path: must be a safe relative path"));

        assertThat(fixture.tourService().getTour(5L)).isEmpty();
    }

    private TourImportRequest importRequest(TourExportDto export) throws Exception {
        String exportJson = objectMapper.writeValueAsString(export);
        return objectMapper.readValue(exportJson, TourImportRequest.class);
    }

    private TourFixture tourFixture() {
        IntermediateTourSearchIndex tourSearchIndex = new IntermediateTourSearchIndex();
        IntermediateTourService tourService = new IntermediateTourService(
                routeCalculationService(),
                coverImageStorageService(),
                new TourAttributeCalculator(),
                tourSearchIndex
        );
        IntermediateTourLogService logService = new IntermediateTourLogService(tourService, tourSearchIndex);
        TourExportService exportService = new TourExportService(tourService, logService);
        TourImportService importService = new TourImportService(tourService, logService);

        return new TourFixture(tourService, logService, exportService, importService);
    }

    private RouteCalculationService routeCalculationService() {
        return new RouteCalculationService(
                new OpenRouteServiceProperties(),
                (profile, startCoordinate, endCoordinate, fetchedAt) -> {
                    throw new AssertionError("OpenRouteService client must not be used during import tests.");
                }
        );
    }

    private CoverImageStorageService coverImageStorageService() {
        ImageStorageProperties properties = new ImageStorageProperties();
        properties.setBaseDirectory(tempDirectory);
        return new CoverImageStorageService(properties);
    }

    private record TourFixture(
            IntermediateTourService tourService,
            IntermediateTourLogService logService,
            TourExportService exportService,
            TourImportService importService
    ) {
    }
}
