package com.cmpe275.domain;

import javax.persistence.*;
import java.util.List;

/**
 *
 * Created by arunabh.shrivastava on 12/1/2017.
 */
@Entity
@Table(name = "STATION")
public class Station {

    @Id
    @GeneratedValue
    private Long id;
    private String name;


    @OneToMany(targetEntity = Search.class)
    List<Search> searchList;

    public Station(){}
    public Station(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {return id;}

    public void setId(Long id) {this.id = id;}
}
