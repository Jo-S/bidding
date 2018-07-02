package com.intuit.cg.backendtechassessment.repository;

import com.intuit.cg.backendtechassessment.domain.Bid;
import com.intuit.cg.backendtechassessment.domain.BidIdentity;
import org.springframework.data.repository.CrudRepository;


public interface BidRepo extends CrudRepository<Bid, BidIdentity> {

    Bid findFirstByBidIdentityProjectIdOrderByAmountAsc(Long projectId);
}
