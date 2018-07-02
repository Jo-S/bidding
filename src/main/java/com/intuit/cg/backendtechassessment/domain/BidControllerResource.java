package com.intuit.cg.backendtechassessment.domain;


import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;


@NoArgsConstructor
public class BidControllerResource {

    @NotNull
    private Long buyerId;

    private Integer amount;

    public BidControllerResource(Long buyerId, Integer amount) {
        this.buyerId = buyerId;
        this.amount = amount;
    }


    public Long getBuyerId() {
        return buyerId;
    }

    public Integer getAmount() {
        return amount;
    }
}
