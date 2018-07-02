package com.intuit.cg.backendtechassessment.controller;


import com.intuit.cg.backendtechassessment.controller.requestmappings.RequestMappings;
import com.intuit.cg.backendtechassessment.domain.Bid;
import com.intuit.cg.backendtechassessment.domain.BidControllerResource;
import com.intuit.cg.backendtechassessment.domain.Project;
import com.intuit.cg.backendtechassessment.service.ProjectService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;


@RestController
@RequestMapping(value = RequestMappings.PROJECTS, produces = MediaType.APPLICATION_JSON_VALUE)
public class ProjectController {


    private ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }


    @PostMapping()
    ResponseEntity<Project> create(
            @RequestBody @Valid Project project) {

        Project createdProject = projectService.createProject(project);

        URI newResourceUri = ServletUriComponentsBuilder
                .fromCurrentRequestUri()
                .path("/{id}")
                .buildAndExpand(createdProject.getId())
                .toUri();

        return ResponseEntity.created(newResourceUri).body(createdProject);
    }


    @GetMapping(value = "/{id}")
    Project get(
            @PathVariable("id") Long id) {

        return projectService.getProject(id);
    }


    @PutMapping(value= "/{projectId}" + RequestMappings.BIDS)
    BidControllerResource putBid(
            @PathVariable("projectId") Long projectId,
            @RequestBody @Valid BidControllerResource resource) {

        Bid bid = new Bid(projectId, resource.getBuyerId(), resource.getAmount());
        Bid placedBid = projectService.putBid(bid);
        return placedBid.toBidControllerResource();
    }


}
