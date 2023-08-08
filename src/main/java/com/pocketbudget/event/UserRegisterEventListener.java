package com.pocketbudget.event;

import com.pocketbudget.util.EmailManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class UserRegisterEventListener implements ApplicationListener<UserRegisterEvent> {
    private final EmailManager emailManager;

    @Autowired
    public UserRegisterEventListener(EmailManager emailManager) {
        this.emailManager = emailManager;
    }

    @Override
    public void onApplicationEvent(UserRegisterEvent customEvent) {
        this.emailManager.sendSSLEmail(customEvent.getUserEmail(), "Welcome to our app", new File("src/main/resources/html/welcome-message.html"));
    }
}
