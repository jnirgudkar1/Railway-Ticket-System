package com.cmpe275.repository;

import com.cmpe275.domain.Transaction;
import org.springframework.data.repository.CrudRepository;

/**
 * @author arunabh.shrivastava
 */
public interface TransactionRepository extends CrudRepository<Transaction, Long>{
}
