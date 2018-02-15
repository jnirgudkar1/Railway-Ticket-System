package com.cmpe275.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.time.LocalTime;
import java.util.List;

/**
 * @author arunabh.shrivastava
 */
@Entity
@Table(name = "SEARCH")
public class Search {

    @Id
    @GeneratedValue
    private long id;

    @ManyToOne(targetEntity = Train.class)
    @JoinColumn(name = "train_id")
    private Train train;

    @ManyToOne(targetEntity = Station.class)
    @JoinColumn(name = "to_station_id")
    private Station toStation;

    @OneToMany(mappedBy = "train")
    @JsonIgnore
    private List<Ticket> ticketSet;


    @ManyToOne(targetEntity = Station.class)
    @JoinColumn(name = "from_station_id")
    private Station fromStation;
    private long price;
    private String duration;
    private long capacity;
    private String departureTime;
    private String arrivalTime;

    public Search(){}

    public Search(Train train, Station fromStation, Station toStation) {
        this.train = train;
        this.toStation = toStation;
        this.fromStation = fromStation;
        this.price = calculatePrice();
        this.departureTime = calculateDepartureTime();
        this.arrivalTime = calculateArrivalTime();
        this.duration = calculateDuration();
    }

    private long calculatePrice(){
        long rate = train.getRate();
        long noOfStation = Math.abs(fromStation.getId() - toStation.getId());
        return (long) (rate*Math.ceil(noOfStation/5.0));
    }

    private String calculateDuration(){
        long min = LocalTime.parse(departureTime).toSecondOfDay();
        LocalTime duration = LocalTime.parse(arrivalTime).minusSeconds(min);
        return duration.toString();
    }

    private String calculateArrivalTime(){
        if(train.isExpress()){
            return calculateExpressArrivalTime();
        }else{
            LocalTime startTime = LocalTime.parse(departureTime);
            long noOfStation = Math.abs(fromStation.getId() - toStation.getId());
            long minutes = 8*noOfStation;
            LocalTime arrivalTime = startTime.plusMinutes(minutes).minusMinutes(3);
            return arrivalTime.toString();
        }
    }

    private String calculateExpressArrivalTime(){
        LocalTime startTime = LocalTime.parse(departureTime);
        long noOfStation = Math.abs(fromStation.getId() - toStation.getId());
        long minutes = 33*(noOfStation/5);
        LocalTime expressArrivalTime = startTime.plusMinutes(minutes).minusMinutes(3);
        return expressArrivalTime.toString();
    }

    private String calculateDepartureTime(){
        if(train.isExpress()){
            return calculateExpressDepartureTime();
        }
        LocalTime departureTime = LocalTime.parse(train.getDepartureTime());
        long noOfStation = getNumberOfStations();
        long minutes = 8*noOfStation;
        return departureTime.plusMinutes(minutes).toString();
    }

    private long getNumberOfStations(){
        if(train.getName().contains("NB")){
            return Math.abs(fromStation.getId() - 1);
        }else{
            return Math.abs(26 - fromStation.getId());
        }
    }

    private String calculateExpressDepartureTime(){
        LocalTime expressDepartureTime = LocalTime.parse(train.getDepartureTime());
        long noOfStation = getNumberOfStations();
        long minutes = 33*(noOfStation/5);
        return expressDepartureTime.plusMinutes(minutes).toString();
    }

    public String getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(String arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public long getCapacity() {
        return capacity;
    }

    public void setCapacity(long capacity) {
        this.capacity = capacity;
    }

    public String getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(String departureTime) {
        this.departureTime = departureTime;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Train getTrain() {
        return train;
    }

    public void setTrain(Train train) {
        this.train = train;
    }

    public Station getToStation() {
        return toStation;
    }

    public void setToStation(Station toStation) {
        this.toStation = toStation;
    }

    public Station getFromStation() {
        return fromStation;
    }

    public void setFromStation(Station fromStation) {
        this.fromStation = fromStation;
    }

    long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }
}