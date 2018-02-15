package com.cmpe275.service;

import com.cmpe275.domain.*;
import com.cmpe275.repository.PassengerRepository;
import com.cmpe275.repository.TicketRepository;
import com.cmpe275.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author arunabh.shrivastava
 */

@Service
public class TransactionService {

    private final
    PassengerRepository passengerRepository;
    private final
    TicketService ticketService;
    private final
    TransactionRepository transactionRepository;
    private final
    TicketRepository ticketRepository;
    private final
    NotificationService notificationService;


    @Autowired
    public TransactionService(PassengerRepository passengerRepository,
                              TicketService ticketService, TransactionRepository transactionRepository, TicketRepository ticketRepository, NotificationService notificationService) {
        this.passengerRepository = passengerRepository;
        this.ticketService = ticketService;
        this.transactionRepository = transactionRepository;
        this.ticketRepository = ticketRepository;
        this.notificationService = notificationService;
    }


    public Transaction makeTransaction(Long userId, Transaction transaction){
        int count = transaction.getTickets().size();
        int iteration = 0;

        for(Ticket ticket: transaction.getTickets()){
            if(ticketService.isTrainAvailable(ticket.getTrain(), ticket.getDateOfJourney(), ticket.getNumberOfPassengers())){
                iteration++;
            }
        }

        if(count == iteration) {
            Passenger passenger = passengerRepository.findOne(userId);
            transaction.setPassenger(passenger);
            transactionRepository.save(transaction);
            passenger.addTransaction(transaction);

            for(Ticket ticket: transaction.getTickets()){
                ticket.setTransaction(transaction);
            }

            passengerRepository.save(passenger);
            ticketRepository.save(transaction.getTickets());
            notificationService.sendBookingNotification(passenger, transaction);
            return transaction;
        }
        else {
            return null;
        }
    }


    public List<Transaction> getTransactions(Long userId) {
        List<Transaction> transactions = new ArrayList<>();

        for(Transaction trans: transactionRepository.findAll()) {

            if(trans.getPassenger().getId() == userId) {
                transactions.add(trans);
            }
        }
        return transactions;
    }


    public Transaction deleteTransaction(Long userId, Long transactionId) {

        Transaction transaction = transactionRepository.findOne(transactionId);
        Passenger passenger = passengerRepository.findOne(userId);
        passenger.removeTransaction(transaction);

        for(Ticket ticket: transaction.getTickets()){
            ticketRepository.delete(ticket.getId());
        }

        transactionRepository.delete(transaction.getId());
        return transaction;
    }
}
