package com.cmpe275.service;

import com.cmpe275.domain.*;
import com.cmpe275.repository.PassengerRepository;
import com.cmpe275.repository.TicketRepository;
import com.cmpe275.repository.TrainRepository;
import com.cmpe275.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.cmpe275.constant.Constants.*;

/**
 * Created by vedant on 12/17/17.
 */

@Service
public class AdminService {

    private final TransactionRepository transactionRepository;
    private final TicketRepository ticketRepository;
    private final TrainRepository trainRepository;
    private final PassengerRepository passengerRepository;
    private final SearchService searchService;
    private final TransactionService transactionService;
    private final NotificationService notificationService;

    @Autowired
    public AdminService(TransactionRepository transactionRepository, TicketRepository ticketRepository, TrainRepository trainRepository, PassengerRepository passengerRepository, SearchService searchService, TransactionService transactionService, NotificationService notificationService) {
        this.transactionRepository = transactionRepository;
        this.ticketRepository = ticketRepository;
        this.trainRepository = trainRepository;
        this.passengerRepository = passengerRepository;
        this.searchService = searchService;
        this.transactionService = transactionService;
        this.notificationService = notificationService;
    }


    public void reset() {
        for(Passenger passenger: passengerRepository.findAll()) {
            passenger.removeAllTransactions();
        }
        ticketRepository.deleteAll();
        transactionRepository.deleteAll();
    }


    public boolean updateTrainCapacity(Long capacity) {
        if(capacity >= 5 && capacity <=trainRepository.findOne(1L).getCapacity()) {
            for(Train train: trainRepository.findAll()) {
                train.setCapacity(capacity);
                trainRepository.save(train);
            }
            return true;
        }else{
            return false;
        }
    }


    public void getTrainId(String trainName, String dateOfJourney) {
        Long trainId = trainRepository.findByName(trainName).getId();
        System.out.println("======="+ trainId + "   " + trainName);
        cancelTrain(trainId, dateOfJourney);
    }


    public void cancelTrain(Long trainId, String dateOfJourney){

        Date date = Utilities.stringToDate(dateOfJourney);
        for(Ticket ticket: ticketRepository.findAllByDateOfJourney(date)){

            Station fromStation;
            Station toStation;
            String departureTime;
            int numberOfPassenger;
            List<String> passengers = new ArrayList<>();
            Passenger passenger = ticket.getTransaction().getPassenger();

            if(ticket.getTrain().getTrain().getId().equals(trainId)){
                fromStation = ticket.getTrain().getFromStation();
                toStation = ticket.getTrain().getToStation();
                departureTime = ticket.getTrain().getDepartureTime();
                numberOfPassenger = ticket.getNumberOfPassengers();

                passengers.addAll(ticket.getTransaction().getListOfPassengers());

                ticketRepository.delete(ticket.getId());
                rebookTicketInAnotherTrain(passenger,fromStation, toStation, departureTime, numberOfPassenger, passengers,
                        dateOfJourney);
            }
        }
    }

    private void rebookTicketInAnotherTrain(Passenger passenger, Station fromStation, Station toStation, String departureTime,
                                            int numberOfPassengers, List<String> passengers, String dateOfJourney){

        List<Transaction> transactionList = searchService.getAvailableTrains(numberOfPassengers,departureTime
                ,fromStation.getId(),toStation.getId(),PARAMETER_VALUE_ANY, PARAMETER_VALUE_ANY, false,
                PARAMETER_VALUE_EMPTY, PARAMETER_VALUE_EMPTY,
                dateOfJourney, false);

        Transaction rebookingTransaction = null;
        if(transactionList.size()>0){
            Transaction transaction = transactionList.get(0);
            transaction.setPassenger(passenger);
            transaction.setListOfPassengers(passengers);
            printTransaction(transaction);
            rebookingTransaction = transactionService.makeTransaction(passenger.getId(), transaction);
        }

        if(rebookingTransaction == null){
            notificationService.sendCancellationNotification(passenger);
        }
    }

    private void printTransaction(Transaction transaction){
        System.out.println("List of Tickets ::");
        for(Ticket ticket: transaction.getTickets()){
            System.out.println(ticket.getTrain().getFromStation().getName() + "  to  " +ticket.getTrain().getToStation().getName());
            System.out.println(ticket.getTrain().getDepartureTime() + "  to  " +ticket.getTrain().getArrivalTime());
        }
        System.out.println(":::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::");
    }

    public Map<String, Integer> calculateTrainReservationRate(String date){
        return null;
    }

    public Map<String, Integer> calculateSystemReservationRate(String date){
        return null;
    }

    public Map<String, Integer> calculateTicketReservationRate(String date) {
        return null;
    }
}
