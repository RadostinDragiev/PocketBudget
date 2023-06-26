package com.pocketbudget.event;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

@Getter
@Setter
public class UserRegisterEvent extends ApplicationEvent {
    private String userEmail;

    public UserRegisterEvent(Object source, String userEmail) {
        super(source);
        this.userEmail = userEmail;
    }
}
