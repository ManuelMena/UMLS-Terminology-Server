/**
 * Copyright 2016 West Coast Informatics, LLC
 */
package com.wci.umls.server.rest.impl;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;

import com.wci.umls.server.UserRole;
import com.wci.umls.server.ValidationResult;
import com.wci.umls.server.helpers.KeyValuePairList;
import com.wci.umls.server.jpa.content.AtomJpa;
import com.wci.umls.server.jpa.content.CodeJpa;
import com.wci.umls.server.jpa.content.ConceptJpa;
import com.wci.umls.server.jpa.content.DescriptorJpa;
import com.wci.umls.server.jpa.services.SecurityServiceJpa;
import com.wci.umls.server.jpa.services.ValidationServiceJpa;
import com.wci.umls.server.jpa.services.rest.ValidationServiceRest;
import com.wci.umls.server.services.SecurityService;
import com.wci.umls.server.services.ValidationService;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

/**
 * REST implementation for {@link ValidationServiceRest}.
 */
@Path("/validation")
@Api(value = "/validation", description = "Operations providing terminology validation")
@Consumes({
    MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML
})
@Produces({
    MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML
})
public class ValidationServiceRestImpl extends RootServiceRestImpl implements
    ValidationServiceRest {

  /** The security service. */
  private SecurityService securityService;

  /**
   * Instantiates an empty {@link ValidationServiceRestImpl}.
   *
   * @throws Exception the exception
   */
  public ValidationServiceRestImpl() throws Exception {
    securityService = new SecurityServiceJpa();
  }

  /* see superclass */
  @Override
  @PUT
  @Path("/dui")
  @ApiOperation(value = "Validate Descriptor", notes = "Validates a descriptor", response = ValidationResult.class)
  public ValidationResult validateDescriptor(
    @ApiParam(value = "Descriptor", required = true) DescriptorJpa descriptor,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {
    Logger.getLogger(getClass()).info(
        "RESTful call PUT (Project): /dui " + descriptor);

    ValidationService validationService = new ValidationServiceJpa();
    try {
      authorizeApp(securityService, authToken, "validate descriptor",
          UserRole.VIEWER);

      return validationService.validateDescriptor(descriptor);
    } catch (Exception e) {
      handleException(e, "trying to validate descriptor");
      return null;
    } finally {
      validationService.close();
      securityService.close();
    }

  }

  /* see superclass */
  @Override
  @PUT
  @Path("/aui")
  @ApiOperation(value = "Validate Atom", notes = "Validates a atom", response = ValidationResult.class)
  public ValidationResult validateAtom(
    @ApiParam(value = "Atom", required = true) AtomJpa atom,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {
    Logger.getLogger(getClass()).info(
        "RESTful call PUT (Project): /aui " + atom);

    ValidationService validationService = new ValidationServiceJpa();
    try {
      authorizeApp(securityService, authToken, "validate atom", UserRole.VIEWER);

      return validationService.validateAtom(atom);
    } catch (Exception e) {
      handleException(e, "trying to validate atom");
      return null;
    } finally {
      validationService.close();
      securityService.close();
    }

  }

  /* see superclass */
  @Override
  @PUT
  @Path("/code")
  @ApiOperation(value = "Validate Code", notes = "Validates a code", response = ValidationResult.class)
  public ValidationResult validateCode(
    @ApiParam(value = "Code", required = true) CodeJpa code,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {
    Logger.getLogger(getClass()).info(
        "RESTful call PUT (Project): /code " + code);

    ValidationService validationService = new ValidationServiceJpa();
    try {
      authorizeApp(securityService, authToken, "validate code", UserRole.VIEWER);

      return validationService.validateCode(code);
    } catch (Exception e) {
      handleException(e, "trying to validate code");
      return null;
    } finally {
      validationService.close();
      securityService.close();
    }

  }

  /* see superclass */
  @Override
  @PUT
  @Path("/concept")
  @ApiOperation(value = "Validate Concept", notes = "Validates a concept", response = ValidationResult.class)
  public ValidationResult validateConcept(
    @ApiParam(value = "Concept", required = true) ConceptJpa concept,
    @ApiParam(value = "Authorization token, e.g. 'guest'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {
    Logger.getLogger(getClass()).info(
        "RESTful call PUT (Project): /concept " + concept);

    ValidationService validationService = new ValidationServiceJpa();
    try {
      authorizeApp(securityService, authToken, "validate concept",
          UserRole.VIEWER);

      return validationService.validateConcept(concept);
    } catch (Exception e) {
      handleException(e, "trying to validate concept");
      return null;
    } finally {
      validationService.close();
      securityService.close();
    }

  }

  /* see superclass */
  @Override
  @GET
  @Path("/checks")
  @ApiOperation(value = "Gets all validation checks", notes = "Gets all validation checks", response = KeyValuePairList.class)
  public KeyValuePairList getValidationChecks(
    @ApiParam(value = "Authorization token, e.g. 'author1'", required = true) @HeaderParam("Authorization") String authToken)
    throws Exception {
    Logger.getLogger(getClass()).info(
        "RESTful call POST (Validation): /checks ");

    final ValidationService validationService = new ValidationServiceJpa();
    try {
      authorizeApp(securityService, authToken, "get validation checks",
          UserRole.VIEWER);

      final KeyValuePairList list = validationService.getValidationCheckNames();
      return list;
    } catch (Exception e) {
      handleException(e, "trying to validate all concept");
      return null;
    } finally {
      validationService.close();
      securityService.close();
    }
  }
}
