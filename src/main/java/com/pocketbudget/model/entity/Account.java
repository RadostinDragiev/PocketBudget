package com.pocketbudget.model.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "accounts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Account extends BaseEntity {
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "balance", nullable = false)
    private BigDecimal balance;

    @Column(name = "currency" , nullable = false)
    private String currency;

    @ManyToOne(targetEntity = User.class)
    private User user;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL)
    private List<Record> records;
}
