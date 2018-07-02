package com.intuit.cg.backendtechassessment.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
import java.io.Serializable;


@Embeddable
@Data
@NoArgsConstructor
public class BidIdentity implements Serializable {

    @NotNull
    private Long projectId;

    @NotNull
    private Long buyerId;

    public BidIdentity(Long projectId, Long buyerId) {
        this.projectId = projectId;
        this.buyerId = buyerId;
    }

    Long getProjectId() {
        return projectId;
    }

    Long getBuyerId() {
        return buyerId;
    }
}

