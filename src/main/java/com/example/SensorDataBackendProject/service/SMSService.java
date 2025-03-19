package com.example.SensorDataBackendProject.service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

@Service
public class SMSService {

    @Value("${twilio.account.sid}")
    private String TWILIO_ACCOUNT_SID;


    public void sendSMS(String phoneNumber, String message) {
        Twilio.init(TWILIO_ACCOUNT_SID, System.getenv("TWILIO_AUTH_TOKEN"));
        Message.creator(new PhoneNumber("+48" + phoneNumber),
                new PhoneNumber("+18588425641"), message).create();
    }
}



