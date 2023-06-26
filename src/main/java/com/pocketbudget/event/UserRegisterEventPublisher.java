package com.pocketbudget.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class UserRegisterEventPublisher {
    private final ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    public UserRegisterEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public void publishUserRegisteredEvent(String userEmail) {
        UserRegisterEvent userRegisteredEvent = new UserRegisterEvent(this, userEmail);
        this.applicationEventPublisher.publishEvent(userRegisteredEvent);
    }
}
