package com.cmpe275.service;

import com.cmpe275.domain.Passenger;
import com.cmpe275.domain.Ticket;
import com.cmpe275.domain.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by vedant on 12/19/17.
 */
@Service
public class NotificationService {

    private JavaMailSender javaMailSender;

    @Autowired
    public NotificationService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void sendNotification(Passenger passenger) throws MailException {

        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setTo(passenger.getEmail());
        // mail.setFrom("vedant123@gmail.com");
        mail.setSubject("Test email from Railway System");
        mail.setText("This is a test email from the Railway Management System");

        javaMailSender.send(mail);

    }

    public void sendBookingNotification(Passenger passenger, Transaction transaction) {

        SimpleMailMessage mail = new SimpleMailMessage();

        mail.setFrom("railwaybookingsystem@gmail.com");
        mail.setTo(passenger.getEmail());
        mail.setSubject("Railway Ticket Booking Confirmation | Transaction Id: "+transaction.getId());

        List<Ticket> ticket = transaction.getTickets();

        String text ="Dear "+ passenger.getFirstName()+",\n\n\n" +
                "Your Itinerary:";

        String[] date;

        for(int i=0; i<transaction.getTickets().size(); i++) {
            date = ticket.get(i).getDateOfJourney().split(" ",5);
            text = text + (
                    "\n\nDate of Journey: " + date[0] +
                            " " + date[1] +
                            " " + date[2] +
                            " " + date[4].split(" ")[1] +
                            "\nFrom Station: " + ticket.get(i).getTrain().getFromStation().getName() +
                            " | Departure Time: " + ticket.get(i).getTrain().getDepartureTime() +
                            "\nTo Station: " + ticket.get(i).getTrain().getToStation().getName() +
                            " | Arrival Time: " + ticket.get(i).getTrain().getArrivalTime()
            );
        }

        //might need to be updated
        long price = 0;
        for(int i=0; i<transaction.getTickets().size(); i++) {
            price = ((ticket.get(i).getPrice()) * transaction.getListOfPassengers().size()) + 1;
        }

        text = text + "\n\nTotal Price: " + price;

        text = text + "\n\nPassenger(s): " + transaction.getListOfPassengers();

        mail.setText(text);

        javaMailSender.send(mail);
    }

    public void sendCancellationNotification(Passenger passenger) {
        SimpleMailMessage mail = new SimpleMailMessage();

        mail.setFrom("railwaybookingsystem@gmail.com");
        mail.setTo(passenger.getEmail());
        mail.setSubject("Railway Ticket Booking Cancellation Notice");

        mail.setText("Unfortunately, your train has been cancelled and re-booking of this train cannot be done. However, your other bookings will remain unchanged. " +
                "If you want to cancel them please cancel the complete itinerary from the Railway Booking System." +
                "Thank you for your cooperation. Sorry for the inconvenience.");

        javaMailSender.send(mail);
    }

}
