package com.cmpe275.service;

import com.cmpe275.domain.*;
import com.cmpe275.repository.SearchRepository;
import com.cmpe275.repository.StationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.*;

import static com.cmpe275.constant.Constants.*;

/**
 * @author arunabh.shrivastava
 */
@Service
public class SearchServiceImpl implements SearchService {

    private final
    StationRepository stationRepository;
    private final
    SearchRepository searchRepository;
    private final
    TicketService ticketService;
    @Autowired
    public SearchServiceImpl(StationRepository stationRepository, SearchRepository searchRepository, TicketService ticketService) {
        this.stationRepository = stationRepository;
        this.searchRepository = searchRepository;
        this.ticketService = ticketService;
    }


    public List<Transaction> getAvailableTrains(int numberOfPassengers, String departureTime, Long fromStationId,
                                               Long toStationId, String ticketType, String connections,
                                               boolean roundTrip, String returnDate, String returnTime,
                                               String dateOfJourney,boolean exactTime) {


        Station fromStation = stationRepository.findOne(fromStationId);
        Station toStation = stationRepository.findOne(toStationId);

        List<Transaction> transactionSet = filterTrainByConnections(ticketType,connections, fromStation, toStation,
                departureTime, numberOfPassengers, dateOfJourney, roundTrip, returnDate, returnTime, exactTime);

        printTransaction(transactionSet);
        return transactionSet;
    }

    private List<Transaction> filterTrainByConnections(String ticketType, String connections, Station fromStation,
            Station toStation, String departureTime, int numberOfPassengers, String dateOfJourney, boolean roundTrip,
                                                      String returnDate, String returnTime, boolean exactTime){
        if(connections.equalsIgnoreCase(CONNECTION_TYPE_NONE)){
            return getTrainsWithNoStop(fromStation, toStation, ticketType, departureTime,numberOfPassengers ,roundTrip,
                    returnDate, returnTime, dateOfJourney, exactTime);
        }
        else if(connections.equalsIgnoreCase(CONNECTION_TYPE_ONE)){
            return getTrainsWithOneStop(fromStation, toStation,ticketType, departureTime, numberOfPassengers, roundTrip,
                    returnDate, returnTime, dateOfJourney,exactTime);
        }else{
            return getAllAvailableTrains(fromStation, toStation, ticketType, departureTime, numberOfPassengers, roundTrip,
                    returnDate, returnTime, dateOfJourney, exactTime);
        }
    }


    private List<Transaction> getAllAvailableTrains(Station fromStation, Station toStation, String ticketType,
            String departureTime, int numberOfPassengers, boolean roundTrip, String returnDate, String returnTime,
                                                    String dateOfJourney, boolean exactTime){
        List<List<Search>> trainsList = getTwoStopTrainsList(fromStation, toStation,ticketType, departureTime,
                dateOfJourney, numberOfPassengers, exactTime);
        List<Transaction> transactionList = new ArrayList<>();
        transactionList.addAll(getTrainsWithNoStop(fromStation,toStation,ticketType,departureTime,numberOfPassengers
                ,roundTrip,returnDate,returnTime,dateOfJourney, exactTime));
        transactionList.addAll(getTrainsWithOneStop(fromStation,toStation,ticketType,departureTime,numberOfPassengers
                ,roundTrip,returnDate,returnTime,dateOfJourney,exactTime));

        if(roundTrip){
            List<List<Search>> returnTrainList = getTwoStopTrainsList(toStation, fromStation,ticketType, returnTime,
                    returnDate, numberOfPassengers, exactTime);
            transactionList.addAll(createTransactionForRoundTripTwoStopTrain(trainsList, returnTrainList, dateOfJourney,
                    numberOfPassengers, returnDate));
            //printTransaction(transactionList);

        }else{
            transactionList.addAll(createTransactionForSingleTripTwoStopTrains(trainsList, numberOfPassengers, dateOfJourney));
            //printTransaction(transactionList);
        }


        return transactionList;
    }

    private List<Transaction> createTransactionForRoundTripTwoStopTrain(List<List<Search>> trainsList,
            List<List<Search>> returnTrainList, String dateOfJourney, int numberOfPassengers, String returnDate){

        List<Transaction> transactionList = new ArrayList<>();
        Transaction transaction;

        for(int i=0;i<5;i++){
            List<Ticket> tickets = new ArrayList<>();

            if(trainsList.size() > i && returnTrainList.size() > i){
                tickets.addAll(getAllTicketsForRoundTripTwoStopTrains(trainsList.get(i), dateOfJourney, numberOfPassengers));
                tickets.addAll(getAllTicketsForRoundTripTwoStopTrains(returnTrainList.get(i), returnDate, numberOfPassengers));

                long price = getAllTicketsPriceForRoundTripTwoStopTrains(tickets);
                String duration = getAllTicketsDurationForRoundTripTwoStopTrains(tickets);
                transaction = new Transaction(tickets, price, duration);
                transactionList.add(transaction);
            }
        }
        return transactionList;
    }

    private long getAllTicketsPriceForRoundTripTwoStopTrains(List<Ticket> tickets){
        long price = 0;
        for(Ticket ticket: tickets){
            price+=ticket.getPrice();
        }
        return price;
    }

    private String getAllTicketsDurationForRoundTripTwoStopTrains(List<Ticket> tickets){
        String duration = "00:00";
        for(Ticket ticket: tickets){
            duration = LocalTime.parse(duration).plusSeconds(LocalTime.parse(ticket.getDuration()).toSecondOfDay()).toString();
        }
        return duration;
    }

    private List<Ticket> getAllTicketsForRoundTripTwoStopTrains(List<Search> trainsList, String dateOfJourney,
                                                                int numberOfPassengers){
        List<Ticket> tickets = new ArrayList<>();
        Ticket ticket;
        for(Search search: trainsList){
            if(ticketService.isTrainAvailable(search, dateOfJourney, numberOfPassengers)){
                Date date = Utilities.stringToDate(dateOfJourney);
                ticket = new Ticket(search, date, numberOfPassengers);
                tickets.add(ticket);
            }
        }
        return tickets;
    }

    private List<List<Search>> getTwoStopTrainsList(Station fromStation, Station toStation, String ticketType, String departureTime,
                                                    String dateOfJourney, int numberOfPassengers, boolean exactTime){
        List<Search> firstStopTrains = getTop5TrainsFromStations(fromStation, departureTime,exactTime);
        firstStopTrains = filterTrainsByTicketType(firstStopTrains, ticketType);
        List<Search> secondStopTrains;
        List<Search> thirdStopTrains;

        List<List<Search>> trainsList = new ArrayList<>();
        for(Search search : firstStopTrains){
            secondStopTrains = getTop5TrainsFromStations(search.getToStation(), search.getArrivalTime(),false);
            for(Search search1: secondStopTrains){
                String departureTimeBefore = LocalTime.parse(search1.getArrivalTime()).
                        plusMinutes(DEPARTURE_TIME_TWO_HOUR_WAITING_TIME).toString();
                thirdStopTrains = searchRepository.findTop5ByFromStationAndToStationAndDepartureTimeAfterAndDepartureTimeBeforeOrderByArrivalTime
                        (search1.getToStation(), toStation, search1.getDepartureTime(), departureTimeBefore);

                List<Search> fastestRoute = getFastestConnectingTwoStopsTrain(firstStopTrains, secondStopTrains,
                        thirdStopTrains,dateOfJourney, numberOfPassengers);

                if(trainsList.size() < 5){
                    //printFastestRoute(fastestRoute);
                    trainsList.add(fastestRoute);
                }else {
                    break;
                }
            }
        }
        return trainsList;
    }

    private List<Transaction> createTransactionForSingleTripTwoStopTrains(List<List<Search>> trainsList,
                                                                          int numberOfPassengers, String dateOfJourney){

        List<Transaction> transactionList = new ArrayList<>();
        List<Ticket> tickets;
        Transaction transaction;
        Ticket ticket;

        for(List<Search> searches: trainsList){
            tickets = new ArrayList<>();
            String duration;
            long price;
            for(Search search: searches){
                Date date = Utilities.stringToDate(dateOfJourney);
                ticket = new Ticket(search, date, numberOfPassengers);
                tickets.add(ticket);
            }

            price=getAllTicketsPriceForRoundTripTwoStopTrains(tickets);
            duration= getAllTicketsDurationForRoundTripTwoStopTrains(tickets);

            transaction = new Transaction(tickets, price, duration);
            transactionList.add(transaction);
        }
        return transactionList;
    }


    private List<Search> getFastestConnectingTwoStopsTrain(List<Search> firstStopTrains, List<Search> secondStopTrain,
                                                           List<Search> thirdStopTrains, String dateOfJourney, int numberOfPassengers){

        List<Search> fastestTrainsList = new ArrayList<>();
        LocalTime maxArrivalTime = LocalTime.of(23, 0);
        for(Search firstTrain: firstStopTrains){
            for(Search secondTrain : secondStopTrain){
                for(Search thirdTrain: thirdStopTrains){
                    if (firstTrain.getToStation().equals(secondTrain.getFromStation()) &&
                            secondTrain.getToStation().equals(thirdTrain.getFromStation())) {

                        LocalTime arrivalTime = LocalTime.parse(thirdTrain.getArrivalTime());
                        if (arrivalTime.isBefore(maxArrivalTime) && ticketService.isTrainAvailable(firstTrain,
                                dateOfJourney, numberOfPassengers) && ticketService.isTrainAvailable(secondTrain,
                                dateOfJourney, numberOfPassengers) && ticketService.isTrainAvailable(thirdTrain,
                                dateOfJourney, numberOfPassengers)) {
                            fastestTrainsList = new ArrayList<>();
                            fastestTrainsList.add(firstTrain);
                            fastestTrainsList.add(secondTrain);
                            fastestTrainsList.add(thirdTrain);
                            maxArrivalTime = arrivalTime;
                        }
                    }
                }
            }
        }
        return fastestTrainsList;
    }



    private List<Transaction> getTrainsWithOneStop(Station fromStation, Station toStation,
            String ticketType, String departureTime, int numberOfPassengers, boolean roundTrip, String returnDate,
                                                  String returnTime, String dateOfJourney, boolean exactTime){

        Map<Integer, List<Search>> connectingTrainTimeMap =
                getOneStopTrainsTimeMap(fromStation, toStation, ticketType, departureTime, exactTime);
        if(roundTrip){
            Map<Integer, List<Search>> returnConnectingTrainTimeMap =
                    getOneStopTrainsTimeMap(toStation, fromStation, ticketType, returnTime,exactTime);

            return createTransactionForRoundTripOneStopTrains(connectingTrainTimeMap, returnConnectingTrainTimeMap,
                    numberOfPassengers, dateOfJourney, returnDate);
        }else{
            return createTransactionForSingleTripOneStopTrains(connectingTrainTimeMap, numberOfPassengers, dateOfJourney);
        }
    }

    private List<Transaction> createTransactionForRoundTripOneStopTrains(Map<Integer, List<Search>> connectingTrainTimeMap,
                                                                         Map<Integer, List<Search>> returnConnectingTrainTimeMap, int numberOfPassengers,
                                                                         String dateOfJourney, String returnDate){

        Map<Integer, List<Ticket>> ticketMap = getTicketMapForOneStopTrains(connectingTrainTimeMap, numberOfPassengers, dateOfJourney);
        Map<Integer, List<Ticket>> returnTicketMap = getTicketMapForOneStopTrains(returnConnectingTrainTimeMap, numberOfPassengers, returnDate);

        List<Transaction> transactionList = new ArrayList<>();
        Transaction transaction;
        List<Ticket> tickets;
        for(Integer integer: ticketMap.keySet()){
            tickets = new ArrayList<>();

            tickets.addAll(ticketMap.get(integer));
            tickets.addAll(returnTicketMap.get(integer));

            long totalPrice = ticketMap.get(integer).get(0).getPrice() + ticketMap.get(integer).get(1).getPrice() +
                    returnTicketMap.get(integer).get(0).getPrice() + returnTicketMap.get(integer).get(1).getPrice();
                transaction = new Transaction(tickets, totalPrice,"");

            transactionList.add(transaction);
        }
        return transactionList;
    }


    private void printTransaction(List<Transaction> transactionList){

        for(Transaction transaction: transactionList){
            System.out.println("List of Tickets ::");
            for(Ticket ticket: transaction.getTickets()){
                System.out.println(ticket.getTrain().getFromStation().getName() + "  to  " +ticket.getTrain().getToStation().getName());
                System.out.println(ticket.getTrain().getDepartureTime() + "  to  " +ticket.getTrain().getArrivalTime());
            }
            System.out.println(":::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::");
        }
    }

    private Map<Integer, List<Search>> getOneStopTrainsTimeMap(Station fromStation, Station toStation,
                                                               String ticketType, String departureTime, boolean exactTime){

        List<Search> allTrainFromSource = getAllTrainsFromStations(fromStation, departureTime, exactTime);
        allTrainFromSource = filterTrainsByTicketType(allTrainFromSource, ticketType);

        Map<Search, List<Search>> connectingTrainsMap = new HashMap<>();
        List<Search> connectingTrains;

        for(Search search : allTrainFromSource){
            connectingTrains = new ArrayList<>();

            String departureTimeBefore = LocalTime.parse(search.getArrivalTime())
                    .plusMinutes(DEPARTURE_TIME_TWO_HOUR_WAITING_TIME).toString();
            String secondTrainDepartureTime = LocalTime.parse(search.getArrivalTime())
                    .plusMinutes(DEPARTURE_TIME_MIN_WAITING_TIME).toString();

            connectingTrains.addAll(searchRepository.findAllByFromStationAndToStationAndDepartureTimeAfterAndDepartureTimeBeforeOrderByArrivalTime
                    (search.getToStation(),toStation, secondTrainDepartureTime , departureTimeBefore));
            connectingTrainsMap.put(search, connectingTrains);

        }
        return getFastestConnectingTrains(connectingTrainsMap);
    }

    private List<Search> getAllTrainsFromStations(Station fromStation, String departureTime, boolean exactTime){
        if(exactTime){
            return searchRepository.findAllByFromStationAndDepartureTimeOrderByArrivalTime
                    (fromStation, departureTime);
        }else{
            departureTime = LocalTime.parse(departureTime).plusMinutes(DEPARTURE_TIME_MIN_WAITING_TIME).toString();
            String twoHoursFromDepartureTime = LocalTime.parse(departureTime).plusMinutes(DEPARTURE_TIME_TWO_HOUR_WAITING_TIME).toString();

            return searchRepository.findAllByFromStationAndDepartureTimeAfterAndDepartureTimeBeforeOrderByArrivalTime
                    (fromStation, departureTime, twoHoursFromDepartureTime);
        }
    }


    private List<Search> getTop5TrainsFromStations(Station fromStation, String departureTime, boolean exactTime){
        if(exactTime){
            return searchRepository.findTop5ByFromStationAndDepartureTimeOrderByArrivalTime
                    (fromStation, departureTime);
        }else{
            departureTime = LocalTime.parse(departureTime).plusMinutes(DEPARTURE_TIME_MIN_WAITING_TIME).toString();
            String twoHoursFromDepartureTime = LocalTime.parse(departureTime).plusMinutes(DEPARTURE_TIME_TWO_HOUR_WAITING_TIME).toString();
            return searchRepository.findTop5ByFromStationAndDepartureTimeAfterAndDepartureTimeBeforeOrderByArrivalTime
                    (fromStation, departureTime, twoHoursFromDepartureTime);
        }
    }

    private Map<Integer, List<Search>> getFastestConnectingTrains(Map<Search, List<Search>> connectingTrainsMap) {

        Map<Integer, List<Search>> connectingTrainTimeMap = new HashMap<>();
        LocalTime maxArrivalTime = LocalTime.of(23, 0);
        for(Search firstConnectingTrain: connectingTrainsMap.keySet()){
            for(Search secondConnectingTrain : connectingTrainsMap.get(firstConnectingTrain)){
                if (firstConnectingTrain.getToStation().equals(secondConnectingTrain.getFromStation())) {
                    LocalTime arrivalTime = LocalTime.parse(secondConnectingTrain.getArrivalTime());
                    int arrivalTimeInSeconds = arrivalTime.toSecondOfDay();
                    if (arrivalTime.isBefore(maxArrivalTime)) {
                        ArrayList<Search> connectingTrainList = new ArrayList<>();
                        connectingTrainList.add(firstConnectingTrain);
                        connectingTrainList.add(secondConnectingTrain);
                        if(connectingTrainTimeMap.containsKey(arrivalTimeInSeconds)){
                            if(connectingTrainTimeMap.get(arrivalTimeInSeconds).get(1).getFromStation().getId() >
                                    secondConnectingTrain.getFromStation().getId()){
                                connectingTrainTimeMap.put(arrivalTimeInSeconds, connectingTrainList);
                            }
                        }else {
                            connectingTrainTimeMap.put(arrivalTimeInSeconds, connectingTrainList);
                        }
                    }
                }
            }
        }
        return connectingTrainTimeMap;
    }

    private List<Transaction> getTrainsWithNoStop(Station fromStation, Station toStation, String ticketType,
                                                 String departureTime, int numberOfPassengers, boolean roundTrip,
                                                 String returnDate, String returnTime, String dateOfJourney,
                                                  boolean exactTime){
        List<Search> trainSet;

        trainSet = getListOfTrainsWithNoStop(fromStation, toStation, departureTime, exactTime);
        trainSet = filterTrainsByTicketType(trainSet, ticketType);

        List<Transaction> transactionList;
        List<Search> returnTrainList;

        if(roundTrip){
            returnTrainList = getListOfTrainsWithNoStop(toStation, fromStation, returnTime, exactTime);
            returnTrainList = filterTrainsByTicketType(returnTrainList, ticketType);
            transactionList = createTransactionForRoundTripNoStopTrains(trainSet, returnTrainList, dateOfJourney, returnDate, numberOfPassengers);
        }else{
            transactionList = createTransactionForSingleTripNoStopTrain(trainSet, dateOfJourney, numberOfPassengers);
        }
        return transactionList;
    }

    public List<Search> getListOfTrainsWithNoStop(Station fromStation, Station toStation, String departureTime,
                                                  boolean exactTime){
        List<Search> trainSet;
        if(exactTime){
            trainSet = searchRepository.findAllByFromStationAndToStationAndDepartureTimeOrderByArrivalTime
                    (fromStation, toStation, departureTime);
        }else{
            departureTime = LocalTime.parse(departureTime).plusMinutes(DEPARTURE_TIME_MIN_WAITING_TIME).toString();
            String departureTimeBefore = LocalTime.parse(departureTime).plusMinutes(DEPARTURE_TIME_FIVE_HOUR_WAITING_TIME).toString();
            trainSet = searchRepository.findAllByFromStationAndToStationAndDepartureTimeAfterAndDepartureTimeBeforeOrderByArrivalTime
                    (fromStation, toStation, departureTime, departureTimeBefore);
        }
        return trainSet;
    }

    private List<Transaction> createTransactionForSingleTripNoStopTrain(List<Search> trainList, String dateOfJourney,
                                                                        int numberOfPassengers){
        List<Transaction> transactionList = new ArrayList<>();
        Ticket ticket;
        List<Ticket> tickets;
        Transaction transaction;

        for (Search aTrainList : trainList) {
            tickets = new ArrayList<>();
            Date date = Utilities.stringToDate(dateOfJourney);
            ticket = new Ticket(aTrainList, date, numberOfPassengers);

          //  System.out.println(ticketService.isTrainAvailable(ticket.getTrain(), dateOfJourney, numberOfPassengers));
            if (ticketService.isTrainAvailable(ticket.getTrain(), dateOfJourney, numberOfPassengers)) {
                tickets.add(ticket);
                String totalDuration = LocalTime.parse(ticket.getTrain().getArrivalTime()).minusSeconds
                        (LocalTime.parse(ticket.getTrain().getDepartureTime()).toSecondOfDay()).toString();

                transaction = new Transaction(tickets, ticket.getPrice(), totalDuration);
                transactionList.add(transaction);
            }
        }
        return transactionList;
    }

    private List<Transaction> createTransactionForRoundTripNoStopTrains(List<Search> trainList, List<Search> returnTrainList,
                                                                        String dateOfJourney, String returnDate, int numberOfPassengers){

        List<Transaction> transactionList = new ArrayList<>();
        Ticket ticket;
        Ticket returnTicket;
        List<Ticket> tickets;

        Transaction transaction;

        for(int i = 0; i<trainList.size(); i++){
            tickets = new ArrayList<>();

            Date date = Utilities.stringToDate(dateOfJourney);
            ticket = new Ticket(trainList.get(i), date, numberOfPassengers);
                if(returnTrainList.size() > i){
                    Date returnTripDate = Utilities.stringToDate(returnDate);
                    returnTicket = new Ticket(returnTrainList.get(i), returnTripDate, numberOfPassengers);
                    String totalDuration = LocalTime.parse(ticket.getTrain().getArrivalTime()).minusSeconds
                            (LocalTime.parse(ticket.getTrain().getDepartureTime()).toSecondOfDay()).toString();

                    if(ticketService.isTrainAvailable(ticket.getTrain(), dateOfJourney, numberOfPassengers)){
                        tickets.add(ticket);
                    }

                    if(ticketService.isTrainAvailable(returnTicket.getTrain(), returnDate, numberOfPassengers)){
                        tickets.add(returnTicket);
                    }

                    if(tickets.size() == 2){
                        long totalPrice = ticket.getPrice() + returnTicket.getPrice();
                        transaction = new Transaction(tickets, totalPrice, totalDuration);
                        transactionList.add(transaction);
                    }
            }
        }
        return transactionList;
    }

    private List<Transaction> createTransactionForSingleTripOneStopTrains(Map<Integer, List<Search>> connectingTrainTimeMap,
                                                                         int numberOfPassengers, String dateOfJourney){

        Map<Integer, List<Ticket>> ticketMap = getTicketMapForOneStopTrains(connectingTrainTimeMap, numberOfPassengers, dateOfJourney);
        List<Transaction> transactionList = new ArrayList<>();
        Transaction transaction;

        Set<Integer> keySet = ticketMap.keySet();
        for(Integer integer : keySet){

            LocalTime secondTrainArrivalTime = LocalTime.parse(ticketMap.get(integer).get(1).getTrain().getArrivalTime());
            LocalTime firstTrainDepartureTime = LocalTime.parse(ticketMap.get(integer).get(0).getTrain().getDepartureTime());

            long firstTrainPrice = ticketMap.get(integer).get(0).getPrice();
            long secondTrainPrice = ticketMap.get(integer).get(1).getPrice();

            String totalDuration = secondTrainArrivalTime.minusSeconds(firstTrainDepartureTime.toSecondOfDay()).toString();
            long totalPrice = firstTrainPrice + secondTrainPrice;
            transaction = new Transaction(ticketMap.get(integer),totalPrice, totalDuration);
            transactionList.add(transaction);
        }
        return transactionList;
    }


    private Map<Integer, List<Ticket>> getTicketMapForOneStopTrains(Map<Integer, List<Search>> connectingTrainTimeMap,
                                                            int numberOfPassengers, String dateOfJourney){
        Set<Integer> arrivalTimeTrainSet = connectingTrainTimeMap.keySet();
        ArrayList<Integer> arrivalTimeTrainList = new ArrayList<>(arrivalTimeTrainSet);
        Collections.sort(arrivalTimeTrainList);

        Ticket firstTrainTicket;
        Ticket secondTrainTicket;
        Map<Integer,List<Ticket>> ticketMap = new HashMap<>();
        List<Ticket> tickets;


        for(int i=0;i<5;i++){
            if(connectingTrainTimeMap.size() > i){
                tickets = new ArrayList<>();
                Search firstTrain = connectingTrainTimeMap.get(arrivalTimeTrainList.get(i)).get(0);
                Search secondTrain = connectingTrainTimeMap.get(arrivalTimeTrainList.get(i)).get(1);

                Date firstTrainDate;
                Date secondTrainDate;
                secondTrainDate = Utilities.stringToDate(dateOfJourney);
                firstTrainDate = Utilities.stringToDate(dateOfJourney);

                firstTrainTicket = new Ticket(firstTrain, firstTrainDate, numberOfPassengers);
                secondTrainTicket = new Ticket(secondTrain, secondTrainDate, numberOfPassengers);

                if(ticketService.isTrainAvailable(firstTrainTicket.getTrain(), dateOfJourney, numberOfPassengers) &&
                        ticketService.isTrainAvailable(secondTrainTicket.getTrain(), dateOfJourney, numberOfPassengers)){

                    tickets.add(firstTrainTicket);
                    tickets.add(secondTrainTicket);
                    ticketMap.put(i,tickets);
                }
            }
        }
        return ticketMap;
    }

    private List<Search> filterTrainsByTicketType(List<Search> trains, String ticketType){
        List<Search> filteredTrains = new ArrayList<>();

        if(ticketType.equalsIgnoreCase(PARAMETER_VALUE_ANY)){
            return trains;
        }
        else if(ticketType.equalsIgnoreCase(TRAIN_TYPE_EXPRESS)){
            for(Search search: trains){
                if(!search.getTrain().isExpress()){
                    filteredTrains.add(search);
                }
            }
        }else{
            for(Search search: trains){
                if(search.getTrain().isExpress()){
                    filteredTrains.add(search);
                }
            }
        }
        trains.removeAll(filteredTrains);
        return trains;
    }
}