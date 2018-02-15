package com.cmpe275.domain;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

/**
 * @author arunabh.shrivastava
 */
@Entity
@Table(name = "TRANSACTION")
public class Transaction {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JsonIgnore
    private Passenger passenger;

    @OneToMany(mappedBy = "transaction")
    private List<Ticket> tickets;
    private long price;
    private String duration;
    private final long TRANSACTION_FEE = 1;

    @ElementCollection
    private List<String> listOfPassengers;

    public Transaction(){}

    public Transaction(List<Ticket> tickets, long price, String duration) {
        this.tickets = tickets;
        this.price = TRANSACTION_FEE+price;
        this.duration = duration;
    }

    public Transaction(Passenger passenger, List<Ticket> tickets, long price, String duration) {
        this.passenger = passenger;
        this.tickets = tickets;
        this.price = TRANSACTION_FEE+price;
        this.duration = duration;
    }

    public List<String> getListOfPassengers() {
        return listOfPassengers;
    }

    public void setListOfPassengers(List<String> listOfPassengers) {
        this.listOfPassengers = listOfPassengers;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Passenger getPassenger() {
        return passenger;
    }

    public void setPassenger(Passenger passenger) {
        this.passenger = passenger;
    }

    public List<Ticket> getTickets() {
        return tickets;
    }

    public void setTickets(List<Ticket> tickets) {
        this.tickets = tickets;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }
}