package com.intuit.cg.backendtechassessment.repository;

import com.intuit.cg.backendtechassessment.domain.Project;
import org.springframework.data.repository.CrudRepository;


public interface ProjectRepo extends CrudRepository<Project, Long> {

}
