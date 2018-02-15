package com.cmpe275.repository;

import com.cmpe275.domain.Search;
import com.cmpe275.domain.Station;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * @author arunabh.shrivastava
 */
public interface SearchRepository extends CrudRepository<Search, Long> {

    List<Search> findAllByFromStationAndToStationAndDepartureTimeOrderByArrivalTime
            (Station fromStation, Station toStation, String departureTime);

    List<Search> findTop5ByFromStationAndDepartureTimeOrderByArrivalTime
            (Station fromStation, String departureTime);

    List<Search> findAllByFromStationAndDepartureTimeOrderByArrivalTime
            (Station fromStation, String departureTime);

    List<Search> findAllByFromStationAndDepartureTimeAfterAndDepartureTimeBeforeOrderByArrivalTime
            (Station fromStation, String departureTimeAfter, String departureTimeBefore);

    List<Search> findTop5ByFromStationAndDepartureTimeAfterAndDepartureTimeBeforeOrderByArrivalTime
            (Station fromStation, String departureTimeAfter, String departureTimeBefore);

    List<Search> findAllByFromStationAndToStationAndDepartureTimeAfterAndDepartureTimeBeforeOrderByArrivalTime
            (Station fromStation, Station toStation, String departureTimeAfter, String departureTimeBefore);

    List<Search> findTop5ByFromStationAndToStationAndDepartureTimeAfterAndDepartureTimeBeforeOrderByArrivalTime
            (Station fromStation, Station toStation, String departureTimeAfter, String departureTimeBefore);
}
