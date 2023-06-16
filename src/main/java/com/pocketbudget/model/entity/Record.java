package com.pocketbudget.model.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "records")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Record extends BaseEntity {
    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @Enumerated(value = EnumType.ORDINAL)
    private Category category;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String note;

    @ManyToOne(targetEntity = Account.class)
    private Account account;
}
