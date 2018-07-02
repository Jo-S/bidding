package com.intuit.cg.backendtechassessment.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.validation.Valid;


@Entity
@Data
@NoArgsConstructor
public class Bid {

    @EmbeddedId
    @Valid
    private BidIdentity bidIdentity;

    private Integer amount;

    public Bid(Long projectId, Long buyerId, Integer amount) {
        this.bidIdentity = new BidIdentity(projectId, buyerId);
        this.amount = amount;
    }


    public BidControllerResource toBidControllerResource() {
        return new BidControllerResource(bidIdentity.getBuyerId(), amount);
    }


    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public Long getProjectId() {
        return bidIdentity.getProjectId();
    }
    public Long getBuyerId() {
        return bidIdentity.getBuyerId();
    }
}
