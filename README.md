# backend-tech-assessment

As per the requirements, it supports the four operations of:

1) Create project

2) Get project by id, which includes lowest bid amount.

3) Add a new bid. 
   One project can have many bids, buyers can bid on many project.
   One buyer can only have one bid per project, bid can be updated.
   Bidding can only be done before the deadline is reached.

4) Auto bid. Will bid below current lowest bid, unless you are already the lowest bidder, in which case, that bid stays as is.
   This is an open-ended excercise, there are a great number of ways to interpret "auto bid", and there is no
   business logic to adhere to, so I just picked one and went with it.
   



For time-management purposes, I kept it simple:

No manual configuration outside of annotations and Pom.

REST hierarchy
There are usually two ways go about it: you 100% let your resource URLs reflect the hierarchy relationship of the data,
or you don't (flatter design), sometimes the former way gets cumbersome.

Given these four operations and the rules from 3), adding/updating a bid does not produce a new resource URL, 
so this should go under projects.

Currently sellerId exists as a field in Project request/response body, which works with a flatter approach.
If we want the full drill-down hierarchy URL, then sellerId should be a path-variable, eg: /sellers/{sellerId}/projects/...

I did not implement buyer/seller as their own entities, takes time without adding value to this assessment.
In a real application you would want the DB to trigger a foreign key constraint violation when trying to add a project
for a non-existent seller etc.

I put in a @ControllerAdvice exception handler, which tends to be helpful in an application with many controllers/services.
In this small application it's overkill, @ExceptionHandler inside the one controller would do but ... 

For unit tests I chose to use Spock, to save time.
Easy and quick to write readable data-driven non-verbose tests.
(I think Spock has surpassed the usefulness of JUnit).



Includes
--------
- Maven - [pom.xml](pom.xml)
- Application properties - [application.yml](src/main/resources/application.yml)
- Runnable Spring Boot Application - [BackendTechAssessmentApplication](src/main/java/com/intuit/cg/backendtechassessment/BackendTechAssessmentApplication.java)
- REST endpoints - [RequestMappings.java](src/main/java/com/intuit/cg/backendtechassessment/controller/requestmappings/RequestMappings.java)

Requirements
------------
See Backend Technical Assessment document for detailed requirements.