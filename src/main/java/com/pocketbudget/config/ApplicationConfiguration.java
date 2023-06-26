package com.pocketbudget.config;

import com.pocketbudget.util.DateTimeApplier;
import com.pocketbudget.util.EmailManager;
import com.pocketbudget.util.impl.DateTimeApplierImpl;
import com.pocketbudget.util.impl.EmailManagerImpl;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class ApplicationConfiguration {
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DateTimeApplier dateTimeApplier() {
        return new DateTimeApplierImpl();
    }

    @Bean
    public EmailManager emailManager() {
        return new EmailManagerImpl();
    }
}
