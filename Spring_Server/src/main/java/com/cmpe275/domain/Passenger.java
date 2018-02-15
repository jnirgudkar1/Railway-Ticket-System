package com.cmpe275.domain;

import javax.persistence.*;
import java.util.List;

/**
 * @author arunabh.shrivastava
 *
 */
@Entity
@Table(name = "PASSENGER")
public class Passenger {

    @Id
    @GeneratedValue
    private Long id;
    @Column(unique = true)
    private String email;

    @OneToMany(fetch = FetchType.EAGER)
    private
    List<Transaction> transactions;

    private String password;



    private String firstName;

    private String lastName;


    public Passenger(){}
    public Passenger(String email) {
        this.email = email;
    }

    public Passenger(String firstName,String lastName,String email,String password)
    {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    private List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    public void addTransaction(Transaction transaction){
        this.transactions.add(transaction);
    }

    public void removeTransaction(Transaction transaction){
        this.transactions.remove(transaction);
    }

    public void removeAllTransactions() {
        this.getTransactions().clear();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}