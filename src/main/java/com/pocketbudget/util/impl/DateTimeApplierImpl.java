package com.pocketbudget.util.impl;

import com.pocketbudget.model.entity.BaseEntity;
import com.pocketbudget.util.DateTimeApplier;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DateTimeApplierImpl implements DateTimeApplier {
    @Override
    public void applyDateTIme(BaseEntity entity) {
        LocalDateTime now = LocalDateTime.now();
        entity.setCreatedDateTime(now);
    }
}
