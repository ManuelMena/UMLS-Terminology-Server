/*
 *    Copyright 2015 West Coast Informatics, LLC
 */
/*
 * 
 */
package com.wci.umls.server.jpa.services.rest;

import com.wci.umls.server.jpa.content.ConceptJpa;
import com.wci.umls.server.jpa.worfklow.WorklistJpa;
import com.wci.umls.server.model.content.Concept;
import com.wci.umls.server.model.workflow.Worklist;

/**
 * Represents a service for supporting CRUD operations needed by integration
 * tests.
 */
public interface IntegrationTestServiceRest {

  /**
   * Adds the concept.
   *
   * @param concept the concept
   * @param authToken the auth token
   * @return the concept
   * @throws Exception the exception
   */
  public Concept addConcept(ConceptJpa concept, String authToken)
    throws Exception;

  /**
   * Removes the concept.
   *
   * @param conceptId the concept id
   * @param authToken the auth token
   * @throws Exception the exception
   */
  public void removeConcept(Long conceptId, String authToken) throws Exception;
  
  /**
   * Adds the worklist.
   *
   * @param worklist the worklist
   * @param authToken the auth token
   * @return the worklist
   * @throws Exception the exception
   */
  public Worklist addWorklist(WorklistJpa worklist, String authToken) throws Exception;
  
  /**
   * Removes the worklist.
   *
   * @param worklistId the worklist id
   * @param authToken the auth token
   * @throws Exception the exception
   */
  public void removeWorklist(Long worklistId, String authToken) throws Exception;
  
  /**
   * Returns the worklist.
   *
   * @param worklistId the worklist id
   * @param authToken the auth token
   * @return the worklist
   * @throws Exception the exception
   */
  public Worklist getWorklist(Long worklistId, String authToken) throws Exception;
  


}
