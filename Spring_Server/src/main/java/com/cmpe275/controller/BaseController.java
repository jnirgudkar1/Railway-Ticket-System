package com.cmpe275.controller;

import com.cmpe275.domain.Passenger;
import com.cmpe275.repository.PassengerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.util.Set;

/**
 * @author arunabh.shrivastava
 *
 */

@Controller(value = "/auth")
public class BaseController {

    @GetMapping(value = "/login")
    @CrossOrigin(origins = "http://34.201.250.194:3000")
    public String getHomePage(){
        return "Home";
    }

    @Autowired
    PassengerRepository passengerRepository;


    @Autowired
    private HttpSession httpSession;



    @RequestMapping(value = "/admin/login")
    @CrossOrigin(origins = "http://34.201.250.194:3000")
    public ResponseEntity<?> adminLogin(@RequestParam(value = "username") String username,
                                        @RequestParam(value = "password") String password) {
        if(username.equals("admin")&&password.equals("admin")) {
            return new ResponseEntity<>(null, HttpStatus.OK);
        }
        return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    }


    @CrossOrigin(origins = "http://34.201.250.194:3000")
    @PostMapping(value = "/registerNewUser")
    public ResponseEntity<Object> registerUser(@RequestParam(value = "firstName") String firstName,
                                               @RequestParam(value = "lastName") String lastName,
                                               @RequestParam(value = "email") String email,
                                               @RequestParam(value = "password") String password) {

        if(firstName == null || lastName == null)
            return new ResponseEntity<>("Please input all fields", HttpStatus.BAD_REQUEST);
        else {
            Set<Passenger> p = passengerRepository.findPassengerByEmail(firstName);
            if (p.size() == 0)
            {
                Passenger passenger = new Passenger(firstName,lastName,email,password);
                passengerRepository.save(passenger);
                    return new ResponseEntity<>(passenger, HttpStatus.OK);
            }
        }

        return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    }



    @CrossOrigin(origins = "http://34.201.250.194:3000")
    @GetMapping(value = "/verifyLogin")
    public ResponseEntity<Object> verifyLogin(@RequestParam(value = "email") String email,
                                              @RequestParam(value = "password") String password,
                                              HttpSession request) {

        if(email == null || password == null) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
        else {
            Passenger passengers = passengerRepository.findPassengerByEmailAndPassword(email,password);
            if(passengers != null) {
                //request.getSession().setAttribute("user",passengers);
                //console.log(req.getHeaders());
                httpSession.setAttribute("user",passengers);

                request.setAttribute("session", passengers);

                System.out.println(httpSession.getAttribute("user"));


                return new ResponseEntity<>(passengers,HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    }

    @CrossOrigin(origins = "http://34.201.250.194:3000")
    @GetMapping(value = "/logout")
    public ResponseEntity<Object> logout(HttpServletRequest request) {
        if(request.getSession().getAttribute("user") != null || request.getSession().getAttribute("user") != "") {
            request.getSession().setAttribute("user",null);
            return new ResponseEntity<>("Logout successful",HttpStatus.OK);
        }
        else return new ResponseEntity<>(null,HttpStatus.BAD_REQUEST);
    }


    @CrossOrigin(origins = "http://34.201.250.194:3000")
    @RequestMapping(value = "/username", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Object> currentUserName(Principal principal, HttpServletRequest request) {
        Passenger p = (Passenger) request.getSession().getAttribute("user");
        return new ResponseEntity<>(p,HttpStatus.OK);
    }
}
