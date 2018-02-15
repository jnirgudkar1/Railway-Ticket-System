package com.cmpe275.domain;

import java.util.Date;

import com.cmpe275.service.Utilities;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

/**
 * @author arunabh.shrivastava
 *
 */
@Entity
@Table(name = "TICKET")
public class Ticket {

    @Id
    @GeneratedValue
    private Long id;
    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    private Transaction transaction;
    @ManyToOne(fetch=FetchType.EAGER)
    private Search train;
    private Long price;
    private String duration;
    @Temporal(TemporalType.DATE)
    private Date dateOfJourney;
    private int numberOfPassengers;

    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    public int getNumberOfPassengers() {
        return numberOfPassengers;
    }

    public void setNumberOfPassengers(int numberOfPassengers) {
        this.numberOfPassengers = numberOfPassengers;
    }

    public Ticket(){}

    public Ticket(Search search){
        this.train = search;
        this.price = search.getPrice();
        this.duration = search.getDuration();
    }

    public Ticket(Search search, Date dateOfJourney, int numberOfPassengers ) {
        this.train = search;
        this.numberOfPassengers = numberOfPassengers;
        this.price = search.getPrice()*numberOfPassengers;
        this.duration = search.getDuration();
        this.dateOfJourney = dateOfJourney;
    }

    public Search getTrain() {
        return train;
    }

    public void setTrain(Search train) {
        this.train = train;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getDateOfJourney() {
        return Utilities.dateToString(dateOfJourney);
    }

    public void setDateOfJourney(String dateOfJourney) {
        this.dateOfJourney = Utilities.stringToDate(dateOfJourney);
    }
}
