package com.intuit.cg.backendtechassessment

import com.fasterxml.jackson.databind.ObjectMapper
import com.intuit.cg.backendtechassessment.controller.requestmappings.RequestMappings
import com.intuit.cg.backendtechassessment.domain.Bid
import com.intuit.cg.backendtechassessment.domain.BidControllerResource
import com.intuit.cg.backendtechassessment.domain.BidIdentity
import com.intuit.cg.backendtechassessment.domain.Project
import com.intuit.cg.backendtechassessment.repository.BidRepo
import com.intuit.cg.backendtechassessment.repository.ProjectRepo
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Stepwise
import spock.lang.Unroll

import java.time.LocalDate


@SpringBootTest(classes = BackendTechAssessmentApplication)
@ContextConfiguration
@Stepwise
class SimpleTest extends Specification {

    @Autowired
    WebApplicationContext webApplicationContext

    @Autowired
    ProjectRepo projectRepo

    @Autowired
    BidRepo bidRepo

    @Autowired
    ObjectMapper objectMapper

    @Shared
    Long projectId1

    @Shared
    Long projectId3_closedForBidding

    MockMvc mockMvc

    final static String WEB_SERVICE_BASE_URL = "http://localhost:8080${RequestMappings.PROJECTS}"

    final static Long LOWEST_BIDDER_ID = 22L
    final static Integer LOWEST_BID = 10_000


    void setup() {

        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .defaultRequest(MockMvcRequestBuilders.get(WEB_SERVICE_BASE_URL)
                .headers(new HttpHeaders())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .build()
    }

    @Unroll
    def "CREATE PROJECT -> project #ordinal"() {

        when: "a request is made to add a project"
        Project toBeCreatedProject = project(biddingDeadLine)
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(WEB_SERVICE_BASE_URL).content(objectMapper.writeValueAsString(toBeCreatedProject))
        MockHttpServletResponse response = mockMvc.perform(request).andReturn().response

        Project projectReturned = objectMapper.readValue(response.contentAsString, Project)
        switch (ordinal) {
            case 1: projectId1 = projectReturned.id
                break
            case 3: projectId3_closedForBidding = projectReturned.id
                break
        }

        then: "the project is persisted"
        projectRepo.findAll().size() == ordinal
        Project persistedProject = projectRepo.findById(projectReturned.id).get()
        persistedProject.maxBudget == toBeCreatedProject.maxBudget
        persistedProject.biddingDeadLine == toBeCreatedProject.biddingDeadLine

        and: "an appropriate response is returned"
        response.status == HttpStatus.CREATED.value()
        projectReturned.biddingDeadLine == toBeCreatedProject.biddingDeadLine
        projectReturned.maxBudget == toBeCreatedProject.maxBudget
        !projectReturned.leadingBid
        projectReturned.stillOpenForBidding == openForBidding


        where:
        ordinal | biddingDeadLine                | openForBidding
        1       | LocalDate.now().plusWeeks(1L)  | true
        2       | LocalDate.now().plusWeeks(1L)  | true
        3       | LocalDate.now().minusWeeks(1L) | false
    }

    @Unroll
    def "UPDATE BID: manually -> bid #ordinal"() {

        when: "the request is made"
        String url = "$WEB_SERVICE_BASE_URL/$projectId1${RequestMappings.BIDS}"
        BidControllerResource toBeCreatedBid = bidControllerResource(buyerId, amount)
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.put(url).content(objectMapper.writeValueAsString(toBeCreatedBid))
        MockHttpServletResponse response = mockMvc.perform(request).andReturn().response

        then: "the bid is created/updated"
        bidRepo.findAll().size() == ordinal
        Bid persistedBid = bidRepo.findById(new BidIdentity(projectId1, buyerId)).get()
        persistedBid.amount == toBeCreatedBid.amount
        persistedBid.buyerId == toBeCreatedBid.buyerId

        and: "a proper response is returned"
        response.status == HttpStatus.OK.value()
        BidControllerResource bidReturned = objectMapper.readValue(response.contentAsString, BidControllerResource)
        bidReturned.buyerId == toBeCreatedBid.buyerId
        bidReturned.amount == toBeCreatedBid.amount

        where:
        ordinal | buyerId          | amount
        1       | 11L              | 20_000
        2       | LOWEST_BIDDER_ID | LOWEST_BID
        3       | 33L              | 30_000
    }

    def "READ PROJECT: with correct lowest bid"() {

        when: "the request is made"
        String url = "$WEB_SERVICE_BASE_URL/$projectId1"
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get(url)
        MockHttpServletResponse response = mockMvc.perform(request).andReturn().response


        then: "the project is successfully retrieved"
        response.status == HttpStatus.OK.value()
        objectMapper.readValue(response.contentAsString, Project).leadingBid == LOWEST_BID
    }

    @Unroll
    def "UPDATE BID: Auto -> #description"() {

        when: "the request is made"
        String url = "$WEB_SERVICE_BASE_URL/$projectId1${RequestMappings.BIDS}"
        BidControllerResource toBeCreatedBid = bidControllerResource(buyerId)
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.put(url).content(objectMapper.writeValueAsString(toBeCreatedBid))
        MockHttpServletResponse response = mockMvc.perform(request).andReturn().response

        then: "the bid is created/updated"
        Bid persistedBid = bidRepo.findById(new BidIdentity(projectId1, buyerId)).get()
        persistedBid.buyerId == buyerId
        persistedBid.amount == expectedBid

        and: "a proper response is returned"
        response.status == HttpStatus.OK.value()
        BidControllerResource bidReturned = objectMapper.readValue(response.contentAsString, BidControllerResource)
        bidReturned.buyerId == toBeCreatedBid.buyerId
        bidReturned.amount == expectedBid

        where:
        buyerId          | description                     || expectedBid
        LOWEST_BIDDER_ID | 'bidder already had lowest bid' || LOWEST_BID
        33L              | 'new lowest bid'                || LOWEST_BID - 100
    }

    def "BAD READ: non-existing project"() {

        given: "a bogus project id"
        Long bogusProjectId = 99L

        when: "the request is made"
        String url = "$WEB_SERVICE_BASE_URL/$bogusProjectId"
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get(url)
        MockHttpServletResponse response = mockMvc.perform(request).andReturn().response

        then: "a 'not found' response is returned"
        response.status == HttpStatus.NOT_FOUND.value()
    }

    def "BAD UPDATE BID: project closed for bidding"() {

        when: "the request is made"
        String url = "$WEB_SERVICE_BASE_URL/$projectId3_closedForBidding${RequestMappings.BIDS}"
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.put(url).content(objectMapper.writeValueAsString(bidControllerResource(99L)))
        MockHttpServletResponse response = mockMvc.perform(request).andReturn().response

        then: "an appropriate response is returned"
        response.status == HttpStatus.GONE.value()
    }

    def "BAD - CREATE PROJECT: required field not set"() {
        when: "a request is made to add a project"
        LocalDate invalidBiddingDeadLine = null
        Project toBeCreatedProject = project(invalidBiddingDeadLine)

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(WEB_SERVICE_BASE_URL).content(objectMapper.writeValueAsString(toBeCreatedProject))
        MockHttpServletResponse response = mockMvc.perform(request).andReturn().response

        then: "an appropriate response is returned"
        response.status == HttpStatus.UNPROCESSABLE_ENTITY.value()
    }


    Project project(LocalDate biddingDeadLine) {
        new Project(
                maxBudget: 40_000,
                biddingDeadLine: biddingDeadLine
        )
    }

    BidControllerResource bidControllerResource(Long buyerId, Integer amount = null) {
        new BidControllerResource(buyerId, amount)
    }

}
