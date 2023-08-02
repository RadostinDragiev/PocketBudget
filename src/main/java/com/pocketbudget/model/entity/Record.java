package com.pocketbudget.model.entity;

import com.pocketbudget.model.entity.enums.Action;
import com.pocketbudget.model.entity.enums.Category;
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
public class Record extends BaseEntity implements Cloneable, Comparable<Record> {
    @Enumerated(EnumType.STRING)
    private Action action;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @Enumerated(value = EnumType.ORDINAL)
    private Category category;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @ManyToOne(targetEntity = Account.class)
    private Account account;

    @OneToOne(cascade = CascadeType.ALL)
    private Record relatedRecord;

    @Override
    public Record clone() {
        try {
            return (Record) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    @Override
    public int compareTo(Record r) {
        boolean isActionEqual = this.getAction().equals(r.getAction());
        boolean isAmountEqual = this.getAmount().compareTo(r.getAmount()) == 0;
        boolean isCategoryEqual = this.getCategory().equals(r.getCategory());
        boolean isNotesEqual = this.getNotes().equals(r.getNotes());
        if (isActionEqual && isAmountEqual && isCategoryEqual && isNotesEqual) {
            return 0;
        }
        return -1;
    }
}
