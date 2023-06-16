package com.pocketbudget.model.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

@MappedSuperclass
@Getter
@Setter
public abstract class BaseEntity extends BaseUUIDEntity {
    @Column(name = "created_date_time", nullable = false)
    private LocalDateTime createdDateTime;
}
