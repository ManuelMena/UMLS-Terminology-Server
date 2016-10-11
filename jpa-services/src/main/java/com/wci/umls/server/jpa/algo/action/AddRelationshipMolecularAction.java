/*
 *    Copyright 2016 West Coast Informatics, LLC
 */
package com.wci.umls.server.jpa.algo.action;

import com.wci.umls.server.ValidationResult;
import com.wci.umls.server.helpers.LocalException;
import com.wci.umls.server.jpa.ValidationResultJpa;
import com.wci.umls.server.jpa.content.ConceptRelationshipJpa;
import com.wci.umls.server.model.content.AtomRelationship;
import com.wci.umls.server.model.content.ConceptRelationship;
import com.wci.umls.server.model.workflow.WorkflowStatus;

/**
 * A molecular action for adding a relationship.
 */
public class AddRelationshipMolecularAction extends AbstractMolecularAction {

  /** The relationship. */
  private ConceptRelationship relationship;

  /**
   * Instantiates an empty {@link AddRelationshipMolecularAction}.
   *
   * @throws Exception the exception
   */
  public AddRelationshipMolecularAction() throws Exception {
    super();
    // n/a
  }

  /**
   * Returns the relationship.
   *
   * @return the relationship
   */
  public ConceptRelationship getRelationship() {
    return relationship;
  }

  /**
   * Sets the relationship.
   *
   * @param relationship the relationship
   */
  public void setRelationship(ConceptRelationship relationship) {
    this.relationship = relationship;
  }

  /* see superclass */
  @Override
  public ValidationResult checkPreconditions() throws Exception {
    final ValidationResult validationResult = new ValidationResultJpa();

    // Perform action specific validation - n/a

    // Verify concept id1/2 are not the same
    if (getConcept().getId().equals(getConcept2().getId())) {
      throw new Exception(
          "Unexpected self-referential relationship, the fromId should match conceptId1");
    }

    // Metadata referential integrity checking
    if (getRelationshipType(relationship.getRelationshipType(),
        relationship.getTerminology(), relationship.getVersion()) == null) {
      rollback();
      throw new LocalException(
          "Cannot add relationship with invalid relationship type - "
              + relationship.getRelationshipType());
    }
    if (getAdditionalRelationshipType(
        relationship.getAdditionalRelationshipType(),
        relationship.getTerminology(), relationship.getVersion()) == null) {
      rollback();
      throw new LocalException(
          "Cannot add relationship with invalid additional relationship type - "
              + relationship.getAdditionalRelationshipType());
    }
    if (getTerminology(relationship.getTerminology(),
        relationship.getVersion()) == null) {
      rollback();
      throw new LocalException(
          "Cannot add relationship with invalid terminology - "
              + relationship.getTerminology() + ", version: "
              + relationship.getVersion());
    }

    // Duplicate check
    for (final ConceptRelationship a : getConcept().getRelationships()) {
      if (a.equals(relationship)) {
        rollback();
        throw new LocalException(
            "Duplicate relationship - " + relationship.getName());
      }
    }

    validationResult.merge(super.checkPreconditions());
    return validationResult;
  }

  /* see superclass */
  @Override
  public void compute() throws Exception {
    //
    // Perform the action (contentService will create atomic actions for CRUD
    // operations)
    //

    // construct inverse relationship
    final ConceptRelationshipJpa inverseRelationship =
        (ConceptRelationshipJpa) createInverseConceptRelationship(relationship);

    // XR (not related) relationships need to be set to not-released
    if (relationship.getRelationshipType().equals("XR")) {
      relationship.setPublishable(false);
    }
    if (inverseRelationship.getRelationshipType().equals("XR")) {
      inverseRelationship.setPublishable(false);
    }

    // Assign alternateTerminologyId
    // final IdentifierAssignmentHandler handler = contentService
    // .getIdentifierAssignmentHandler(concept.getTerminology());

    // final String altId = handler.getTerminologyId(relationship);
    // relationship.getAlternateTerminologyIds().put(concept.getTerminology(),
    // altId);

    // final String inverseAltId =
    // handler.getTerminologyId(inverseRelationship);
    // inverseRelationship.getAlternateTerminologyIds()
    // .put(concept.getTerminology(), inverseAltId);

    // Change status of the relationships
    if (getChangeStatusFlag()) {
      relationship.setWorkflowStatus(WorkflowStatus.NEEDS_REVIEW);
      inverseRelationship.setWorkflowStatus(WorkflowStatus.NEEDS_REVIEW);
    }

    // If any matching relationship, remove it and its inverse (new
    // relationships will replace them)
    for (final ConceptRelationship rel : getConcept().getRelationships()) {
      if (rel.getTo().getId() == relationship.getTo().getId()) {
        // Remove the relationship from the concepts
        getConcept().getRelationships().remove(rel);
        getConcept2().getRelationships().remove(findInverseRelationship(rel));

        // Update Concepts
        updateConcept(getConcept());
        updateConcept(getConcept2());

        // Remove the relationships
        removeRelationship(rel.getId(), rel.getClass());
        removeRelationship(findInverseRelationship(rel).getId(),
            rel.getClass());

        // Change status of the source and target concept
        if (getChangeStatusFlag()) {
          getConcept().setWorkflowStatus(WorkflowStatus.NEEDS_REVIEW);
          getConcept2().setWorkflowStatus(WorkflowStatus.NEEDS_REVIEW);
        }

        // Update Concepts
        updateConcept(getConcept());
        updateConcept(getConcept2());
      }
    }

    // Look through atoms for demotion relationships, and remove them.
    AtomRelationship demotion =
        findDemotionMatchingRelationship(relationship);

    if (demotion != null) {
      // Remove the demotions from the atoms
      demotion.getFrom().getRelationships().remove(demotion);
      demotion.getTo().getRelationships()
          .remove(findInverseRelationship(demotion));

      // Update Atoms
      updateAtom(demotion.getFrom());
      updateAtom(demotion.getTo());

      // Remove the demotions
      removeRelationship(demotion.getId(), demotion.getClass());
      removeRelationship(findInverseRelationship(demotion).getId(),
          demotion.getClass());

      // Change status of the source and target atom
      if (getChangeStatusFlag()) {
        demotion.getFrom().setWorkflowStatus(WorkflowStatus.NEEDS_REVIEW);
        demotion.getTo().setWorkflowStatus(WorkflowStatus.NEEDS_REVIEW);
      }

      // Update Atoms
      updateAtom(demotion.getFrom());
      updateAtom(demotion.getTo());
    }

    // Add the relationships
    relationship = (ConceptRelationshipJpa) addRelationship(relationship);
    final ConceptRelationshipJpa newInverseRelationship =
        (ConceptRelationshipJpa) addRelationship(inverseRelationship);

    // Add the relationship to concepts
    getConcept().getRelationships().add(relationship);
    getConcept2().getRelationships().add(newInverseRelationship);

    // update the concepts
    updateConcept(getConcept());
    updateConcept(getConcept2());

    // Change status of ONLY the source concept
    if (getChangeStatusFlag()) {
      getConcept().setWorkflowStatus(WorkflowStatus.NEEDS_REVIEW);
    }

    // update the concept
    updateConcept(getConcept());

    // log the REST calls
    addLogEntry(getLastModifiedBy(), getProject().getId(), getConcept().getId(),
        getActivityId(), getWorkId(), getName() + " to concept "
            + getConcept2().getId() + " " + relationship);
    addLogEntry(getLastModifiedBy(), getProject().getId(),
        getConcept2().getId(), getActivityId(), getWorkId(), getName()
            + " from concept " + getConcept().getId() + " " + relationship);
    
    addLogEntry(getLastModifiedBy(), getProject().getId(),
        getMolecularAction().getId(), getActivityId(), getWorkId(),
        "\nACTION  " + getName() + "\n  concept = " + getConcept().getId() + " " + getConcept().getName() +
        (getConcept2() != null ? "\n  concept2 = " + getConcept2().getId() + " " + getConcept2().getName() : "") +
        "\n  relationship id = " + getRelationship().getId() +
        "\n  terminology = " + getTerminology() +
        "\n  version = " + getVersion());
  }

}
