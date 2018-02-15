package com.cmpe275.repository;

import com.cmpe275.domain.Passenger;
import org.springframework.data.repository.CrudRepository;

import java.util.Set;

/**
 * @author arunabh.shrivastava
 */
public interface PassengerRepository extends CrudRepository<Passenger, Long> {

    Set<Passenger> findPassengerByEmail(String email);

    Passenger findPassengerByEmailAndPassword(String email, String password);

}
