package com.intuit.cg.backendtechassessment.domain;


import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Entity
@Data
public class Project {

    @Id
    @GeneratedValue
    private Long id;

    @NotNull
    private Integer maxBudget;

    @NotNull
    private LocalDate biddingDeadLine;

    @Transient
    private Integer leadingBid;


    public Boolean isStillOpenForBidding() {
        return biddingDeadLine != null ? !LocalDate.now().isAfter(biddingDeadLine) : Boolean.FALSE;
    }

    public Long getId() {
        return id;
    }

    public Integer getMaxBudget() {
        return maxBudget;
    }

    public LocalDate getBiddingDeadLine() {
        return biddingDeadLine;
    }

    public Integer getLeadingBid() {
        return leadingBid;
    }

    public void setLeadingBid(Integer leadingBid) {
        this.leadingBid = leadingBid;
    }
}
