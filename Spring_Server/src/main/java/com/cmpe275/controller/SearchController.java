package com.cmpe275.controller;

import com.cmpe275.domain.Transaction;
import com.cmpe275.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;
import static com.cmpe275.constant.Constants.*;

/**
 * @author arunabh.shrivastava
 */
@RestController
@RequestMapping(value = "/api/search")
@CrossOrigin(origins = "http://34.201.250.194:3000")
public class SearchController {

    private final
    SearchService searchService;


    @Autowired
    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @PostMapping
    @CrossOrigin(origins = "http://34.201.250.194:3000")
    public ResponseEntity<?> search(@RequestParam(value = "noOfPassengers",required = false, defaultValue = "1") Integer noOfPassengers,
                                    @RequestParam(value = "departureTime") String departureTime,
                                    @RequestParam(value = "dateOfJourney") String dateOfJourney,
                                    @RequestParam(value = "fromStation") Long fromStation,
                                    @RequestParam(value = "toStation") Long toStation,
                                    @RequestParam(value = "ticketType", required = false, defaultValue = "any") String ticketType,
                                    @RequestParam(value = "connection", required = false, defaultValue = "any") String connections,
                                    @RequestParam(value = "roundTrip", required = false, defaultValue = "false") boolean roundTrip,
                                    @RequestParam(value = "returnDate", required = false) String returnDate,
                                    @RequestParam(value = "returnTime", required = false) String returnTime,
                                    @RequestParam(value = "exactTime", required = false, defaultValue = "false") boolean exactTime,
                                    HttpSession request)
    {
        if(departureTime == null || toStation == null || fromStation == null || dateOfJourney == null){
            return new ResponseEntity<>(INVALID_SEARCH_REQUEST, HttpStatus.BAD_REQUEST);
        }

        System.out.println(request.getAttribute("user"));


        List<Transaction> transactions = searchService.getAvailableTrains(noOfPassengers,departureTime,fromStation,toStation,ticketType,
                connections,roundTrip, returnDate, returnTime, dateOfJourney, exactTime);
       return new ResponseEntity<>(transactions, HttpStatus.OK);
    }
}
