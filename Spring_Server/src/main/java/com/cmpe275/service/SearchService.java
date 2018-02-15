package com.cmpe275.service;

import com.cmpe275.domain.Transaction;

import java.util.List;

/**
 * @author arunabh.shrivastava
 */
public interface SearchService {

    List<Transaction> getAvailableTrains(int numberOfPassengers, String departureTime, Long fromStationId,
                                         Long toStationId, String ticketType, String connections,
                                         boolean roundTrip, String returnDate, String returnTime,
                                         String dateOfJourney, boolean exactTime);
}