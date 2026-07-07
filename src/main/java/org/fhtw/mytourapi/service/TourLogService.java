package org.fhtw.mytourapi.service;

import org.fhtw.mytourapi.dto.CreateTourLogRequest;
import org.fhtw.mytourapi.dto.ImportedTourLogDto;
import org.fhtw.mytourapi.dto.TourLogDto;
import org.fhtw.mytourapi.dto.TourLogWeatherDto;
import org.fhtw.mytourapi.dto.UpdateTourLogRequest;
import org.fhtw.mytourapi.domain.TourEntity;
import org.fhtw.mytourapi.domain.TourLogEntity;
import org.fhtw.mytourapi.mapper.TourPersistenceMapper;
import org.fhtw.mytourapi.repository.TourLogRepository;
import org.fhtw.mytourapi.repository.TourRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class TourLogService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TourLogService.class);

    private final TourService tourService;
    private final TourSearchIndex tourSearchIndex;
    private final WeatherSnapshotService weatherSnapshotService;
    private final TourRepository tourRepository;
    private final TourLogRepository tourLogRepository;
    private final TourPersistenceMapper persistenceMapper;

    public TourLogService(
            TourService tourService,
            TourSearchIndex tourSearchIndex,
            WeatherSnapshotService weatherSnapshotService,
            TourRepository tourRepository,
            TourLogRepository tourLogRepository,
            TourPersistenceMapper persistenceMapper
    ) {
        this.tourService = tourService;
        this.tourSearchIndex = tourSearchIndex;
        this.weatherSnapshotService = weatherSnapshotService;
        this.tourRepository = tourRepository;
        this.tourLogRepository = tourLogRepository;
        this.persistenceMapper = persistenceMapper;
    }

    @Transactional(readOnly = true)
    public Optional<List<TourLogDto>> listLogs(Long tourId) {
        Optional<Long> userId = tourService.currentUserIdIfPresent();
        if (userId.isEmpty() || !tourRepository.existsByIdAndUser_Id(tourId, userId.get())) {
            return Optional.empty();
        }

        List<TourLogDto> logs = tourLogRepository
                .findAllByTour_IdAndTour_User_IdOrderByPerformedAtDesc(tourId, userId.get())
                .stream()
                .map(persistenceMapper::toLog)
                .toList();

        return Optional.of(logs);
    }

    @Transactional(readOnly = true)
    public List<TourLogDto> listLogsForExport(Long tourId) {
        return tourService.currentUserIdIfPresent()
                .map((userId) -> tourLogRepository
                        .findAllByTour_IdAndTour_User_IdOrderByPerformedAtDesc(tourId, userId).stream()
                        .map(persistenceMapper::toLog)
                        .sorted(Comparator.comparing(TourLogDto::performedAt).thenComparing(TourLogDto::id))
                        .toList())
                .orElseGet(List::of);
    }

    @Transactional
    public Optional<TourLogDto> createLog(Long tourId, CreateTourLogRequest request) {
        Optional<TourEntity> tour = findOwnedTour(tourId);
        if (tour.isEmpty()) {
            return Optional.empty();
        }

        Instant now = Instant.now();
        TourLogEntity log = new TourLogEntity();
        log.setTour(tour.get());
        applyLogFields(log, request.performedAt(), request.comment(), request.difficulty(), request.totalDistanceM(),
                request.totalTimeS(), request.rating());

        TourLogEntity savedLog = tourLogRepository.saveAndFlush(log);
        TourLogWeatherDto weather = weatherSnapshotService.snapshotFor(
                savedLog.getId(),
                persistenceMapper.toDetail(tour.get()),
                request.performedAt(),
                now
        );
        persistenceMapper.applyWeather(savedLog, weather);
        savedLog = tourLogRepository.saveAndFlush(savedLog);

        refreshDerivedTourState(tourId);
        LOGGER.info(
                "Created tour log tourId={} logId={} weatherProvider={}",
                tourId,
                savedLog.getId(),
                weather.provider()
        );
        return Optional.of(persistenceMapper.toLog(savedLog));
    }

    @Transactional
    public Optional<List<TourLogDto>> importLogs(Long tourId, List<ImportedTourLogDto> importedLogs) {
        Optional<TourEntity> tour = findOwnedTour(tourId);
        if (tour.isEmpty()) {
            return Optional.empty();
        }

        List<TourLogDto> logs = importedLogs.stream()
                .map((importedLog) -> {
                    CreateTourLogRequest request = importedLog.log();
                    TourLogEntity log = new TourLogEntity();
                    log.setTour(tour.get());
                    applyLogFields(log, request.performedAt(), request.comment(), request.difficulty(),
                            request.totalDistanceM(), request.totalTimeS(), request.rating());

                    TourLogEntity savedLog = tourLogRepository.saveAndFlush(log);
                    persistenceMapper.applyImportedWeather(savedLog, importedLog.weather());
                    savedLog = tourLogRepository.saveAndFlush(savedLog);
                    return persistenceMapper.toLog(savedLog);
                })
                .toList();

        refreshDerivedTourState(tourId);
        LOGGER.info("Imported tour logs tourId={} count={}", tourId, logs.size());
        return Optional.of(logs);
    }

    @Transactional(readOnly = true)
    public Optional<TourLogDto> getLog(Long tourId, Long logId) {
        return findPersistedLog(tourId, logId)
                .map(persistenceMapper::toLog);
    }

    @Transactional
    public Optional<TourLogDto> updateLog(Long tourId, Long logId, UpdateTourLogRequest request) {
        return findPersistedLog(tourId, logId)
                .map((log) -> {
                    Instant now = Instant.now();
                    applyLogFields(log, request.performedAt(), request.comment(), request.difficulty(),
                            request.totalDistanceM(), request.totalTimeS(), request.rating());

                    TourLogWeatherDto weather = weatherSnapshotService.snapshotFor(
                            logId,
                            persistenceMapper.toDetail(log.getTour()),
                            request.performedAt(),
                            now
                    );
                    persistenceMapper.applyWeather(log, weather);
                    TourLogEntity savedLog = tourLogRepository.saveAndFlush(log);
                    refreshDerivedTourState(tourId);

                    LOGGER.info("Updated tour log tourId={} logId={} version={}",
                            tourId, logId, savedLog.getVersion());
                    return persistenceMapper.toLog(savedLog);
                });
    }

    @Transactional
    public boolean deleteLog(Long tourId, Long logId) {
        Optional<TourLogEntity> log = findPersistedLog(tourId, logId);
        if (log.isEmpty()) {
            return false;
        }

        tourLogRepository.delete(log.get());
        tourLogRepository.flush();
        refreshDerivedTourState(tourId);
        LOGGER.info("Deleted tour log tourId={} logId={}", tourId, logId);
        return true;
    }

    @Transactional
    public Optional<TourLogWeatherDto> refreshWeather(Long tourId, Long logId) {
        return findPersistedLog(tourId, logId)
                .map((log) -> {
                    Instant now = Instant.now();
                    TourLogWeatherDto weather = weatherSnapshotService.snapshotFor(
                            logId,
                            persistenceMapper.toDetail(log.getTour()),
                            log.getPerformedAt(),
                            now
                    );
                    persistenceMapper.applyWeather(log, weather);
                    tourLogRepository.saveAndFlush(log);
                    tourSearchIndex.replaceLogs(tourId, persistedLogsForTour(tourId));

                    LOGGER.info(
                            "Refreshed tour log weather tourId={} logId={} provider={}",
                            tourId,
                            logId,
                            weather.provider()
                    );
                    return weather;
                });
    }

    private Optional<TourLogEntity> findPersistedLog(Long tourId, Long logId) {
        return tourService.currentUserIdIfPresent()
                .flatMap((userId) -> tourLogRepository.findByIdAndTour_IdAndTour_User_Id(logId, tourId, userId));
    }

    private Optional<TourEntity> findOwnedTour(Long tourId) {
        return tourService.currentUserIdIfPresent()
                .flatMap((userId) -> tourRepository.findByIdAndUser_Id(tourId, userId));
    }

    private void applyLogFields(
            TourLogEntity log,
            Instant performedAt,
            String comment,
            Short difficulty,
            BigDecimal totalDistanceM,
            Integer totalTimeS,
            Short rating
    ) {
        log.setPerformedAt(performedAt);
        log.setComment(normalizeComment(comment));
        log.setDifficulty(difficulty);
        log.setTotalDistanceM(totalDistanceM);
        log.setTotalTimeS(totalTimeS);
        log.setRating(rating);
    }

    private void refreshDerivedTourState(Long tourId) {
        List<TourLogDto> logs = persistedLogsForTour(tourId);
        tourService.refreshComputedAttributes(tourId, logs);
        tourSearchIndex.replaceLogs(tourId, logs);
    }

    private List<TourLogDto> persistedLogsForTour(Long tourId) {
        return tourService.currentUserIdIfPresent()
                .map((userId) -> tourLogRepository
                        .findAllByTour_IdAndTour_User_IdOrderByPerformedAtDesc(tourId, userId).stream()
                        .map(persistenceMapper::toLog)
                        .toList())
                .orElseGet(List::of);
    }

    private static String normalizeComment(String comment) {
        if (comment == null || comment.isBlank()) {
            return null;
        }

        return comment.trim();
    }
}
