/*
 *    Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.umls.server.jpa.algo.maint;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import com.wci.umls.server.AlgorithmParameter;
import com.wci.umls.server.ValidationResult;
import com.wci.umls.server.jpa.AlgorithmParameterJpa;
import com.wci.umls.server.jpa.ValidationResultJpa;
import com.wci.umls.server.jpa.algo.AbstractInsertMaintReleaseAlgorithm;
import com.wci.umls.server.model.content.Atom;
import com.wci.umls.server.model.content.Definition;

/**
 * Implementation of an algorithm to execute an action based on a user-defined
 * query.
 */
public class AdHocAlgorithm extends AbstractInsertMaintReleaseAlgorithm {

  /** The actionName. */
  private String actionName;

  /**
   * Instantiates an empty {@link AdHocAlgorithm}.
   * @throws Exception if anything goes wrong
   */
  public AdHocAlgorithm() throws Exception {
    super();
    setActivityId(UUID.randomUUID().toString());
    setWorkId("QUERYACTION");
    setLastModifiedBy("admin");
  }

  /**
   * Check preconditions.
   *
   * @return the validation result
   * @throws Exception the exception
   */
  /* see superclass */
  @Override
  public ValidationResult checkPreconditions() throws Exception {

    ValidationResult validationResult = new ValidationResultJpa();

    if (getProject() == null) {
      throw new Exception("Ad Hoc algorithms requires a project to be set");
    }

    return validationResult;
  }

  /**
   * Compute.
   *
   * @throws Exception the exception
   */
  /* see superclass */
  @Override
  public void compute() throws Exception {
    logInfo("Starting " + getName());
    logInfo("  actionName = " + actionName);

    // No Molecular actions will be generated by this algorithm
    setMolecularActionFlag(false);

    commitClearBegin();

    // Add all ad hoc actions to if-statement chain.
    if (actionName.equals("Fix Orphan Definitions")) {
      fixOrphanDefinitions();
    } else {
      throw new Exception("Valid Action Name not specified.");
    }

    commitClearBegin();

    logInfo("  project = " + getProject().getId());
    logInfo("  workId = " + getWorkId());
    logInfo("  activityId = " + getActivityId());
    logInfo("  user  = " + getLastModifiedBy());
    logInfo("Finished " + getName());

  }

  private void fixOrphanDefinitions() throws Exception {
    // 11/14/2017 - Bug in SplitMolecularAction found where definitions weren't
    // being copied over with atoms when split out, resulting in orphaned
    // definitions.
    // Load these definitions and re-add them to the appropriate atom.

    int successful = 0;

    final Map<Long, Long> definitionIdAtomIdMap = new HashMap<>();
    definitionIdAtomIdMap.put(37014L, 338961L);
    definitionIdAtomIdMap.put(275324L, 6783080L);
    definitionIdAtomIdMap.put(275326L, 6783082L);
    definitionIdAtomIdMap.put(275327L, 6783083L);
    definitionIdAtomIdMap.put(275328L, 6783084L);
    definitionIdAtomIdMap.put(275329L, 6783085L);
    definitionIdAtomIdMap.put(275330L, 6783086L);
    definitionIdAtomIdMap.put(275333L, 6783089L);
    definitionIdAtomIdMap.put(275562L, 6783318L);
    definitionIdAtomIdMap.put(275825L, 6783578L);
    definitionIdAtomIdMap.put(327827L, 6815915L);
    definitionIdAtomIdMap.put(327871L, 6815960L);
    definitionIdAtomIdMap.put(327938L, 6816027L);
    definitionIdAtomIdMap.put(327972L, 6816061L);
    definitionIdAtomIdMap.put(327975L, 6816064L);
    definitionIdAtomIdMap.put(328045L, 6816138L);
    definitionIdAtomIdMap.put(328111L, 6816205L);
    definitionIdAtomIdMap.put(362093L, 6854283L);
    definitionIdAtomIdMap.put(362094L, 6851063L);
    definitionIdAtomIdMap.put(362125L, 6854335L);
    definitionIdAtomIdMap.put(362126L, 6851079L);
    definitionIdAtomIdMap.put(362199L, 6854454L);
    definitionIdAtomIdMap.put(362200L, 6851116L);

    for (Map.Entry<Long, Long> entry : definitionIdAtomIdMap.entrySet()) {
      final Long definitionId = entry.getKey();
      final Long atomId = entry.getValue();

      final Atom atom = getAtom(atomId);
      if (atom == null) {
        logWarn("Could not find atom with id=" + atomId);
        continue;
      }

      final Definition definition = getDefinition(definitionId);
      if (definition == null) {
        logWarn("Could not find definition with id=" + definitionId);
        continue;
      }

      if (atom.getDefinitions().contains(definition)) {
        logWarn(
            "atom=" + atomId + " already contains definition=" + definitionId);
        continue;
      }

      atom.getDefinitions().add(definition);
      updateAtom(atom);
      successful++;
    }

    logInfo("[FixOrphanDefinitions] " + successful
        + " orphan definitions successfully reattached.");

  }

  /* see superclass */
  @Override
  public void reset() throws Exception {
    logInfo("Starting RESET " + getName());
    // n/a - No reset
    logInfo("Finished RESET " + getName());
  }

  /* see superclass */
  @Override
  public void checkProperties(Properties p) throws Exception {
    checkRequiredProperties(new String[] {
        "actionName"
    }, p);
  }

  /* see superclass */
  @Override
  public void setProperties(Properties p) throws Exception {

    if (p.getProperty("actionName") != null) {
      actionName = String.valueOf(p.getProperty("actionName"));
    }

  }

  /**
   * Returns the parameters.
   *
   * @return the parameters
   * @throws Exception the exception
   */
  /* see superclass */
  @Override
  public List<AlgorithmParameter> getParameters() throws Exception {
    final List<AlgorithmParameter> params = super.getParameters();
    AlgorithmParameter param = new AlgorithmParameterJpa("Action Name",
        "actionName", "Name of Ad Hoc Action to be performed",
        "e.g. Fix Orphan Definitions", 200, AlgorithmParameter.Type.ENUM, "");
    param.setPossibleValues(Arrays.asList("Fix Orphan Definitions"));
    params.add(param);

    return params;
  }

  /* see superclass */
  @Override
  public String getDescription() {
    return "Perform Ad Hoc Action, normally for data fixes.";
  }

}