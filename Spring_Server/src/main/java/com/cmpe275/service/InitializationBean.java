package com.cmpe275.service;

import com.cmpe275.domain.Search;
import com.cmpe275.domain.Station;
import com.cmpe275.domain.Train;
import com.cmpe275.repository.SearchRepository;
import com.cmpe275.repository.StationRepository;
import com.cmpe275.repository.TrainRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.*;

import static com.cmpe275.constant.Constants.*;
/**
 * @author arunabh.shrivastava
 */

@Service
public class InitializationBean {

    private final
    TrainRepository trainRepository;
    private final
    StationRepository stationRepository;
    private
    SearchRepository searchRepository;
    @Autowired
    public InitializationBean(TrainRepository trainRepository, StationRepository stationRepository, SearchRepository searchRepository){
        this.trainRepository = trainRepository;
        this.stationRepository = stationRepository;
        this.searchRepository = searchRepository;
        this.searchRepository = searchRepository;

        if(stationRepository.count()<1){
            createStations();
        }
        if(trainRepository.count()<1){
            createTrains();
        }
/*
        createStations();;
        createTrains();
*/
    }

    private void createTrains(){
        LocalTime startTime = LocalTime.of(6, 0);
        while(startTime.isBefore(LocalTime.of(21,15))){

            String TRAIN_NUMBER = startTime.toString().replace(":","");

            String northBoundName = NORTHBOUND_NAME_PREFIX + TRAIN_NUMBER;
            String southBoundName = SOUTHBOUND_NAME_PREFIX + TRAIN_NUMBER;

            boolean isExpress = false;
            long priceRate = 1;
            LocalTime departureTime = startTime;
            if(startTime.getMinute() == 0){
                isExpress = true;
                priceRate = 2;
            }
            String expressStationNameList = "AFKPUZ";
            List<Station> stations = (List<Station>) stationRepository.findAll();

            Train northboundTrain = new Train(northBoundName, DEFAULT_TRAIN_CAPACITY ,isExpress, departureTime.toString() , priceRate);
            Train southboundTrain = new Train(southBoundName, DEFAULT_TRAIN_CAPACITY ,isExpress, departureTime.toString(),  priceRate);

            System.out.println(northBoundName + " -- " + isExpress + " -- " + startTime + " -- ");
            System.out.println(southBoundName + " -- " + isExpress + " -- " + startTime + " -- ");

            trainRepository.save(northboundTrain);
            trainRepository.save(southboundTrain);
            for(Station fromStation: stations){
                for(Station toStation: stations){
                    if((isExpress)){
                        if((expressStationNameList.contains(toStation.getName())) &&
                                (expressStationNameList.contains(fromStation.getName()))){
                            saveSearch(northboundTrain,southboundTrain,fromStation,toStation);
                        }
                    }else{
                        saveSearch(northboundTrain,southboundTrain,fromStation,toStation);
                    }
                }
            }
            startTime = startTime.plusMinutes(15);
        }
    }

    private void saveSearch(Train northboundTrain, Train southboundTrain, Station fromStation, Station toStation){
        Search search;
        Search search2;
        if((toStation.getId() > fromStation.getId())){
            search = new Search(northboundTrain, fromStation, toStation);
            searchRepository.save(search);
        }
        if(toStation.getId() < fromStation.getId()){
            search2 = new Search(southboundTrain, fromStation, toStation);
            searchRepository.save(search2);
        }
    }

    private void createStations(){
        for (char alphabet = 'A'; alphabet <= 'Z'; alphabet++) {
            Station station = new Station(String.valueOf(alphabet));
            stationRepository.save(station);
        }
    }
}
