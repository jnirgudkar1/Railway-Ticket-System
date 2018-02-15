package com.cmpe275.controller;

import com.cmpe275.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author arunabh.shrivastava
 *
 */
@RestController
public class AdminController {

    private final
    AdminService adminService;

    @Autowired
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }


    @RequestMapping(value = "/admin/reset")
    @CrossOrigin(origins = "http://34.201.250.194:3000")
    public ResponseEntity<?> reset(){

        adminService.reset();

        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    @RequestMapping(value = "/admin/updateTrainCapacity")
    @CrossOrigin(origins = "http://34.201.250.194:3000")
    public ResponseEntity<?> updateTrainCapacity(@RequestParam(value = "capacity") Long capacity){

        if(adminService.updateTrainCapacity(capacity)) {
            return new ResponseEntity<>("", HttpStatus.OK);
        }else{
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/admin/getTrainId")
    @CrossOrigin(origins = "http://34.201.250.194:3000")
    public ResponseEntity<?> getTrainId(@RequestParam(value = "trainName") String trainName,
                                        @RequestParam(value = "dateOfJourney") String dateOfJourney) {

        adminService.getTrainId(trainName, dateOfJourney);

        return new ResponseEntity<>(null,HttpStatus.OK);
    }


//    @RequestMapping(value = "/admin/cancelTrain")
//    @CrossOrigin(origins = "http://34.201.250.194:3000")
//    public ResponseEntity<?> cancelTrain(@RequestParam(value = "trainId") Long trainId,
//                                         @RequestParam(value = "dateOfJourney") String date){
//        adminService.cancelTrain(trainId, date);
//        return new ResponseEntity<Object>("Train cancelled.",HttpStatus.OK);
//    }

    @RequestMapping(value = "/admin/report/trainReservation")
    @CrossOrigin(origins = "http://34.201.250.194:3000")
    public ResponseEntity<?> trainReservationRate(@RequestParam(value = "date")String date){
        Map<String, Integer> perTrainReservationRate = adminService.calculateTrainReservationRate(date);
        return new ResponseEntity<Object>(perTrainReservationRate, HttpStatus.OK);
    }

    @RequestMapping(value = "/admin/report/systemReservation")
    @CrossOrigin(origins = "http://34.201.250.194:3000")
    public ResponseEntity<?> systemReservationRate(@RequestParam(value = "date") String date){
        Map<String, Integer> perTrainReservationRate = adminService.calculateSystemReservationRate(date);
        return new ResponseEntity<Object>(perTrainReservationRate, HttpStatus.OK);
    }

    @RequestMapping(value = "/admin/report/ticketReservation")
    @CrossOrigin(origins = "http://34.201.250.194:3000")
    public ResponseEntity<?> ticketReservationRate(@RequestParam(value = "date") String date){
        Map<String, Integer> perTrainReservationRate = adminService.calculateTicketReservationRate(date);
        return new ResponseEntity<Object>(perTrainReservationRate, HttpStatus.OK);
    }
}
