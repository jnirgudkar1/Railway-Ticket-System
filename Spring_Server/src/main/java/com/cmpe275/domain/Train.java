package com.cmpe275.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.EqualsAndHashCode;
import org.apache.tomcat.jni.Local;

import javax.persistence.*;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author arunabh.shrivastava
 *
 */
@EqualsAndHashCode
@Entity
@Table(name = "TRAIN")
public class Train {

    @Id
    @GeneratedValue
    private Long id;
    @Column(unique = true)
    private String name;

    @ManyToOne(targetEntity = Search.class)
    List<Search> searchList;

    /*
    @OneToMany(mappedBy = "train")
    @JsonIgnore
    private Set<Ticket> ticketSet;
*/
    private Long capacity;

    private boolean isExpress;
    private String departureTime;
    private long rate;

    public Train() {}

    public Train(String name, Long capacity, boolean isExpress, String departureTime, long rate) {
        this.name = name;
        this.capacity = capacity;
        this.isExpress = isExpress;
        this.departureTime = departureTime;
        this.rate = rate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

/*
    public Set<Ticket> getTicketSet() {
        return ticketSet;
    }

    public void setTicketSet(Set<Ticket> ticketSet) {
        this.ticketSet = ticketSet;
    }
*/

    public Long getCapacity() {
        return capacity;
    }

    public void setCapacity(Long capacity) {
        this.capacity = capacity;
    }

    public boolean isExpress() {
        return isExpress;
    }

    public void setExpress(boolean express) {
        isExpress = express;
    }

    public String getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(String departureTime) {
        this.departureTime = departureTime;
    }

    public long getRate() {
        return rate;
    }

    public void setRate(long rate) {
        this.rate = rate;
    }
}
