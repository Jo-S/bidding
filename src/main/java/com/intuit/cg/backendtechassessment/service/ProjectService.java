package com.intuit.cg.backendtechassessment.service;


import com.intuit.cg.backendtechassessment.domain.Bid;
import com.intuit.cg.backendtechassessment.domain.Project;
import com.intuit.cg.backendtechassessment.exception.ProjectClosedForBiddingException;
import com.intuit.cg.backendtechassessment.exception.ProjectNotFoundException;
import com.intuit.cg.backendtechassessment.repository.BidRepo;
import com.intuit.cg.backendtechassessment.repository.ProjectRepo;
import org.springframework.stereotype.Service;



@Service
public class ProjectService {

    private ProjectRepo projectRepo;
    private BidRepo bidRepo;

    private static final Integer AUTOBID_DECREMENT_AMOUNT = 100;

    public ProjectService(ProjectRepo projectRepo, BidRepo bidRepo) {
        this.projectRepo = projectRepo;
        this.bidRepo = bidRepo;
    }


    public Project createProject(Project project) {
        return projectRepo.save(project);
    }


    public Project getProject(Long projectId) {

        Project project = projectRepo.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException("id=" + projectId));

        Bid leadingBid = getLeadingBidForProject(projectId);
        project.setLeadingBid(leadingBid != null ? leadingBid.getAmount() : project.getMaxBudget() + 100);
        return project;
    }


    public Bid putBid(Bid bid) {

        Project project = getProject(bid.getProjectId());

        if (!project.isStillOpenForBidding())
            throw new ProjectClosedForBiddingException("id=" + project.getId());

        if (bid.getAmount() != null)
            return bidRepo.save(bid);

        return autoBid(project, bid);
    }


    private Bid autoBid(Project project, Bid bid) {

        Bid leadingBid = getLeadingBidForProject(project.getId());

        if (leadingBid == null)
            bid.setAmount(project.getMaxBudget() - AUTOBID_DECREMENT_AMOUNT);

        else if (leadingBid.getBuyerId().equals(bid.getBuyerId()))
            return leadingBid;

        else
            bid.setAmount(project.getLeadingBid() - AUTOBID_DECREMENT_AMOUNT);

        return bidRepo.save(bid);
    }

    private Bid getLeadingBidForProject(Long projectId) {
        return bidRepo.findFirstByBidIdentityProjectIdOrderByAmountAsc(projectId);
    }
}
