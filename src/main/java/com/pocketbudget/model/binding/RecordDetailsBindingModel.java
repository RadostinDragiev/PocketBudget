package com.pocketbudget.model.binding;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.pocketbudget.model.entity.enums.Action;
import com.pocketbudget.model.entity.enums.Category;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RecordDetailsBindingModel {
    private String uuid;
    private Action action;
    private BigDecimal amount;
    private Category category;
    private String notes;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdDateTime;
}
