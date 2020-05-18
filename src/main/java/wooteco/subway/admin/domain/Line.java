package wooteco.subway.admin.domain;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.annotation.Id;

public class Line {

    public static final Long START_STATION_ID = null;

    @Id
    private Long id;
    private String name;
    private LocalTime startTime;
    private LocalTime endTime;
    private int intervalTime;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Set<LineStation> stations = new HashSet<>();

    public Line() {
    }

    public Line(Long id, String name, LocalTime startTime, LocalTime endTime, int intervalTime) {
        this.name = name;
        this.startTime = startTime;
        this.endTime = endTime;
        this.intervalTime = intervalTime;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Line(String name, LocalTime startTime, LocalTime endTime, int intervalTime) {
        this(null, name, startTime, endTime, intervalTime);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public int getIntervalTime() {
        return intervalTime;
    }

    public Set<LineStation> getStations() {
        return stations;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void update(Line line) {
        if (line.getName() != null) {
            this.name = line.getName();
        }
        if (line.getStartTime() != null) {
            this.startTime = line.getStartTime();
        }
        if (line.getEndTime() != null) {
            this.endTime = line.getEndTime();
        }
        if (line.getIntervalTime() != 0) {
            this.intervalTime = line.getIntervalTime();
        }

        this.updatedAt = LocalDateTime.now();
    }

    public Optional<LineStation> findByStationId(Long stationId) {
        return stations.stream().filter(it -> Objects.equals(it.getStationId(), stationId))
            .findAny();
    }

    public Optional<LineStation> findByPreStationId(Long preStationId) {
        return stations.stream().filter(it -> Objects.equals(it.getPreStationId(), preStationId))
            .findAny();
    }

    public void addLineStation(LineStation lineStation) {
        findByPreStationId(lineStation.getPreStationId())
            .ifPresent(it -> it.updatePreLineStation(lineStation.getStationId()));

        stations.add(lineStation);
    }

    public void removeLineStationById(Long stationId) {
        while (true) {
            Optional<LineStation> lineStation = findByStationId(stationId);

            if (!lineStation.isPresent()) {
                return;
            }
            LineStation targetLineStation = lineStation.orElseThrow(RuntimeException::new);

            findByPreStationId(stationId).ifPresent(it -> it.updatePreLineStation(targetLineStation.getPreStationId()));

            stations.remove(targetLineStation);
        }
    }

    public List<Long> getLineStationsId() {
        if (stations.isEmpty()) {
            return new ArrayList<>();
        }

        LineStation firstLineStation = findByPreStationId(START_STATION_ID).orElseThrow(RuntimeException::new);

        List<Long> stationIds = new ArrayList<>();
        stationIds.add(firstLineStation.getStationId());

        while (true) {
            Long lastStationId = stationIds.get(stationIds.size() - 1);
            Optional<LineStation> nextLineStation = stations.stream()
                .filter(it -> Objects.equals(it.getPreStationId(), lastStationId))
                .findFirst();

            if (!nextLineStation.isPresent()) {
                break;
            }

            stationIds.add(nextLineStation.get().getStationId());
        }

        return stationIds;
    }

    public List<Station> getMatchingStations(List<Station> wholeStations) {
        List<Long> stationIds = stations.stream()
            .map(LineStation::getStationId)
            .collect(Collectors.toList());

        return wholeStations.stream()
            .filter(station -> stationIds.contains(station.getId()))
            .collect(Collectors.toList());
    }
}
