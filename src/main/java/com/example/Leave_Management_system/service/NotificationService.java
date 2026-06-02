package com.example.Leave_Management_system.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    @Async
    public void sendLeaveApprovedNotification(String employeeName,String email, int numberOfDays){
        log.info("[Notification Thread] {} - Sending approval notification to {}",
                Thread.currentThread().getName(), email);
        try {
            Thread.sleep(2000);//pretend sending email takes 2 seconds
        }catch (Exception e){
            Thread.currentThread().interrupt();
        }
        log.info("[Notification Thread] Email sent to {} — Your {} day leave is APPROVED!",
                email, numberOfDays);
    }


    @Async
    public void sendRejectedNotification(String employeeName, String email, String reason){
        log.info("[Notification Thread] {} - Sending rejection notification to {}",
                Thread.currentThread().getName(), email);
        try {
            Thread.sleep(2000);
        } catch (Exception e) {
            Thread.currentThread().interrupt();
        }
        log.info("[Notification Thread] Email sent to {} — Your leave was REJECTED. Reason: {}",
                email, reason);
    }


    @Async
    public void sendAppliedNotification(String employeeName, String managerEmail, int days){
        log.info("[Notification Thread] Notifying manager {} about new leave request from {}",
                managerEmail, employeeName);
        try {
            Thread.sleep(1000);
        } catch (Exception e) {
            Thread.currentThread().interrupt();
        }
        log.info("[Notification Thread] Manager notified about {} {} day leave request.",
                employeeName, days);
    }
}
