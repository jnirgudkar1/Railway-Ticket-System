package com.cmpe275.repository;

import com.cmpe275.domain.Train;
import org.springframework.data.repository.CrudRepository;

/**
 * @author arunabh.shrivastava
 */
public interface TrainRepository extends CrudRepository<Train, Long> {
    Train findByName(String name);
}
