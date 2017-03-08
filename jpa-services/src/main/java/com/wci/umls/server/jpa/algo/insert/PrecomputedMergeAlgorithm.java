/*
 *    Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.umls.server.jpa.algo.insert;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;

import com.wci.umls.server.AlgorithmParameter;
import com.wci.umls.server.ValidationResult;
import com.wci.umls.server.helpers.ConfigUtility;
import com.wci.umls.server.helpers.FieldedStringTokenizer;
import com.wci.umls.server.helpers.KeyValuePair;
import com.wci.umls.server.helpers.QueryType;
import com.wci.umls.server.jpa.AlgorithmParameterJpa;
import com.wci.umls.server.jpa.ValidationResultJpa;
import com.wci.umls.server.jpa.algo.AbstractMergeAlgorithm;
import com.wci.umls.server.jpa.algo.action.UndoMolecularAction;
import com.wci.umls.server.model.actions.MolecularAction;
import com.wci.umls.server.model.actions.MolecularActionList;
import com.wci.umls.server.model.content.Atom;
import com.wci.umls.server.model.content.AtomClass;
import com.wci.umls.server.model.content.Component;
import com.wci.umls.server.services.handlers.ComputePreferredNameHandler;

/**
 * Implementation of an algorithm to import attributes.
 */
public class PrecomputedMergeAlgorithm extends AbstractMergeAlgorithm {

  /** The merge set. */
  private String mergeSet;

  /** The check names. */
  private List<String> checkNames;

  /** The filter query type. */
  private QueryType filterQueryType = null;

  /** The filter query. */
  private String filterQuery = null;

  /** The change status. */
  private Boolean changeStatus = null;

  /** The make demotions. */
  private Boolean makeDemotions = null;

  /**
   * Instantiates an empty {@link PrecomputedMergeAlgorithm}.
   * @throws Exception if anything goes wrong
   */
  public PrecomputedMergeAlgorithm() throws Exception {
    super();
    setActivityId(UUID.randomUUID().toString());
    setWorkId("PRECOMPUTEDMERGE");
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
      throw new Exception("Precomputed Merge requires a project to be set");
    }

    // Check the input directories

    final String srcFullPath =
        ConfigUtility.getConfigProperties().getProperty("source.data.dir")
            + File.separator + getProcess().getInputPath();

    setSrcDirFile(new File(srcFullPath));
    if (!getSrcDirFile().exists()) {
      throw new Exception("Specified input directory does not exist");
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
    logInfo("  integrity checks = " + checkNames);
    logInfo("  filter query type = " + filterQueryType);
    logInfo("  filter query = " + filterQuery);
    logInfo("  make demotions = " + makeDemotions);

    // Molecular actions WILL be generated by this algorithm
    setMolecularActionFlag(true);

    // Set up the search handler
    final ComputePreferredNameHandler prefNameHandler =
        getComputePreferredNameHandler(getProject().getTerminology());

    // Set up a stats map to be passed into the merge function later
    final Map<String, Integer> statsMap = new HashMap<>();
    statsMap.put("atomPairsLoadedFromMergefacts", 0);
    statsMap.put("atomPairsRemovedByFilters", 0);
    statsMap.put("atomPairsRemainingAfterFilters", 0);
    statsMap.put("successfulMerges", 0);
    statsMap.put("unsuccessfulMerges", 0);
    statsMap.put("successfulDemotions", 0);
    statsMap.put("unsuccessfulDemotions", 0);

    try {

      logInfo("  Processing mergefacts.src");
      commitClearBegin();

      //
      // Load the mergefacts.src file
      //
      List<String> lines =
          loadFileIntoStringList(getSrcDirFile(), "mergefacts.src", null, null);

      // Set the number of steps to the number of lines to be processed
      setSteps(lines.size());

      logInfo("Looking up atom id pairs for each " + mergeSet
          + " line in mergefacts.src");
      // Store all of the atom Id pairs in the file in a list
      List<Long[]> atomIdPairs = new ArrayList<>();

      String fields[] = new String[12];

      for (String line : lines) {

        // Check for a cancelled call once every 100 lines
        if (getStepsCompleted() % 100 == 0) {
          checkCancel();
        }

        FieldedStringTokenizer.split(line, "|", 12, fields);

        // Fields:
        // 0 id_1
        // 1 merge_level
        // 2 id_2
        // 3 source
        // 4 integrity_vector
        // 5 make_demotion
        // 6 change_status
        // 7 merge_set
        // 8 id_type_1
        // 9 id_qualifier_1
        // 10 id_type_2
        // 11 id_qualifier_2

        // e.g.
        // 362249700|SY|362281363|NCI_2016_05E||Y|N|NCI-SY|SRC_ATOM_ID||SRC_ATOM_ID||

        // If this lines mergeSet doesn't match the specified mergeSet, skip.
        if (!fields[7].equals(mergeSet)) {
          continue;
        }

        // Use the first line encountered to set changeStatus and makeDemotions
        // (they will be the same for the entire merge set)
        if (changeStatus == null) {
          changeStatus = fields[6].toUpperCase().equals("Y");
        }
        if (makeDemotions == null) {
          makeDemotions = fields[5].toUpperCase().equals("Y");
        }

        // Load the two atoms specified by the mergefacts line, or the preferred
        // name atoms if they are containing component
        Component component = getComponent(fields[8], fields[0],
            getCachedTerminologyName(fields[9]), null);
        if (component == null) {
          logWarn("WARNING - could not find Component for type: " + fields[8]
              + ", terminologyId: " + fields[0]
              + ". Could not process the following line:\n\t" + line);
          continue;
        }
        Atom atom = null;
        if (component instanceof Atom) {
          atom = (Atom) component;
        } else if (component instanceof AtomClass) {
          AtomClass atomClass = (AtomClass) component;
          List<Atom> atoms =
              prefNameHandler.sortAtoms(atomClass.getAtoms(), getPrecedenceList(
                  getProject().getTerminology(), getProject().getVersion()));
          atom = atoms.get(0);
        } else {
          logWarn("WARNING - " + component.getClass().getName()
              + " is an unhandled type. Could not process the following line:\n\t"
              + line);
          continue;
        }

        Component component2 = getComponent(fields[10], fields[2],
            getCachedTerminologyName(fields[11]), null);
        if (component2 == null) {
          logWarn("WARNING - could not find Component for type: " + fields[10]
              + ", terminologyId: " + fields[2]
              + ". Could not process the following line:\n\t" + line);
          continue;
        }
        Atom atom2 = null;
        if (component2 instanceof Atom) {
          atom2 = (Atom) component2;
        } else if (component2 instanceof AtomClass) {
          AtomClass atomClass = (AtomClass) component2;
          List<Atom> atoms =
              prefNameHandler.sortAtoms(atomClass.getAtoms(), getPrecedenceList(
                  getProject().getTerminology(), getProject().getVersion()));
          atom2 = atoms.get(0);
        } else {
          logWarn("WARNING - " + component2.getClass().getName()
              + " is an unhandled type. Could not process the following line:\n\t"
              + line);
          continue;
        }

        // Add the pair of atom ids to the list
        atomIdPairs.add(new Long[] {
            atom.getId(), atom2.getId()
        });

      }

      statsMap.put("atomPairsLoadedFromMergefacts", atomIdPairs.size());

      // Generate parameters to pass into query executions
      Map<String, String> params = new HashMap<>();
      params.put("terminology", this.getTerminology());
      params.put("version", this.getVersion());
      params.put("projectTerminology", getProject().getTerminology());
      params.put("projectVersion", getProject().getVersion());

      // Remove all atom pairs caught by the filters
      logInfo("Removing atom id pairs that are caught by the filter.");
      // If no filters specified, it will return all of the atom pairs.
      final List<Pair<Long, Long>> filteredAtomIdPairs = applyFilters(
          atomIdPairs, params, filterQueryType, filterQuery, false, statsMap);

      statsMap.put("atomPairsRemainingAfterFilters",
          filteredAtomIdPairs.size());

      // Set the steps count to the number of atomPairs merges will be
      // attempted for
      setSteps(filteredAtomIdPairs.size());

      // Attempt to perform the merges given the integrity checks
      for (Pair<Long, Long> atomIdPair : filteredAtomIdPairs) {
        checkCancel();

        merge(atomIdPair.getLeft(), atomIdPair.getRight(), checkNames,
            makeDemotions, changeStatus, getProject(), statsMap);

        // Update the progress
        updateProgress();
      }

      commitClearBegin();

      logInfo("  atom pairs returned by query count = "
          + statsMap.get("atomPairsReturnedByQuery"));
      logInfo("  atom pairs removed by filters count = "
          + statsMap.get("atomPairsRemovedByFilters"));
      logInfo("  atom pairs remaining after filters count = "
          + statsMap.get("atomPairsRemainingAfterFilters"));
      logInfo("  merges successfully performed count = "
          + statsMap.get("successfulMerges"));
      logInfo("  unsuccessful merges count = "
          + statsMap.get("unsuccessfulMerges"));
      if (makeDemotions != null && makeDemotions) {
        logInfo("  demotions successfully created count = "
            + statsMap.get("successfulDemotions"));
        logInfo("  attempted demotion unsuccessful count = "
            + statsMap.get("unsuccessfulDemotions"));
      }

      logInfo("Finished " + getName());

    } catch (

    Exception e) {
      logError("Unexpected problem - " + e.getMessage());
      throw e;
    }

  }

  /**
   * Reset.
   *
   * @throws Exception the exception
   */
  /* see superclass */
  @Override
  public void reset() throws Exception {
    logInfo("Starting RESET " + getName());

    // Collect any merges previously performed, and UNDO them
    final MolecularActionList molecularActions =
        findMolecularActions(null, getProject().getTerminology(),
            getProject().getVersion(), "activityId:" + getActivityId(), null);

    for (MolecularAction molecularAction : molecularActions.getObjects()) {
      // Create and set up an undo action
      final UndoMolecularAction undoAction = new UndoMolecularAction();

      // Configure and run the undo action
      undoAction.setProject(getProject());
      undoAction.setActivityId(molecularAction.getActivityId());
      undoAction.setConceptId(null);
      undoAction.setConceptId2(molecularAction.getComponentId2());
      undoAction.setLastModifiedBy(molecularAction.getLastModifiedBy());
      undoAction.setTransactionPerOperation(false);
      undoAction.setMolecularActionFlag(false);
      undoAction.setChangeStatusFlag(true);
      undoAction.setMolecularActionId(molecularAction.getId());
      undoAction.setForce(false);
      undoAction.performMolecularAction(undoAction, getLastModifiedBy(), false);
    }
    logInfo("Finished RESET " + getName());
  }

  /* see superclass */
  @Override
  public void checkProperties(Properties p) throws Exception {
    checkRequiredProperties(new String[] {
        "mergeSet"
    }, p);
  }

  /* see superclass */
  @Override
  public void setProperties(Properties p) throws Exception {

    if (p.getProperty("mergeSet") != null) {
      mergeSet = String.valueOf(p.getProperty("mergeSet"));
    }
    if (p.getProperty("checkNames") != null) {
      checkNames =
          Arrays.asList(String.valueOf(p.getProperty("checkNames")).split(";"));
    }
    if (p.getProperty("filterQueryType") != null) {
      filterQueryType = Enum.valueOf(QueryType.class,
          String.valueOf(p.getProperty("filterQueryType")));
    }
    if (p.getProperty("filterQuery") != null) {
      filterQuery = String.valueOf(p.getProperty("filterQuery"));
    }

  }

  /**
   * Returns the parameters.
   *
   * @return the parameters
   */
  /* see superclass */
  @Override
  public List<AlgorithmParameter> getParameters() throws Exception {
    final List<AlgorithmParameter> params = super.getParameters();

    // Run checkPreconditions to set the SrcDirFile, since it will be used by
    // the merge
    // set parameter
    try {
      checkPreconditions();
    } catch (Exception e) {
      // Do nothing
    }

    AlgorithmParameter param = new AlgorithmParameterJpa("Merge Set",
        "mergeSet", "The merge set to perform the merges on", "e.g. NCI-SY", 10,
        AlgorithmParameter.Type.ENUM, "");
    // Look for the mergefacts.src file and populate the enum based on the
    // merge_set column.
    List<String> mergeSets = getMergeSets(getSrcDirFile());

    // If the file isn't found, or the file contains no mergeSets, set the
    // parameter to a free-entry string
    if (mergeSets == null || mergeSets.size() == 0) {
      param.setType(AlgorithmParameter.Type.STRING);
    } else {
      param.setPossibleValues(mergeSets);
    }
    params.add(param);

    param = new AlgorithmParameterJpa("Integrity Checks", "checkNames",
        "The names of the integrity checks to run", "e.g. MGV_B", 10,
        AlgorithmParameter.Type.MULTI, "");

    List<String> validationChecks = new ArrayList<>();
    for (final KeyValuePair validationCheck : getValidationCheckNames()
        .getKeyValuePairs()) {
      // Add handler Name to ENUM list
      validationChecks.add(validationCheck.getKey());
    }

    Collections.sort(validationChecks);
    param.setPossibleValues(validationChecks);
    params.add(param);

    // filter query type
    param = new AlgorithmParameterJpa("Filter Query Type", "filterQueryType",
        "The language the filter query is written in", "e.g. JPQL", 200,
        AlgorithmParameter.Type.ENUM, "");
    param.setPossibleValues(EnumSet.allOf(QueryType.class).stream()
        .map(e -> e.toString()).collect(Collectors.toList()));
    params.add(param);

    // filter query
    param = new AlgorithmParameterJpa("Filter Query", "filterQuery",
        "A filter query to further restrict the objects to run the merge on",
        "e.g. query in format of the query type", 4000,
        AlgorithmParameter.Type.TEXT, "");
    params.add(param);

    return params;
  }

  /* see superclass */
  @Override
  public String getDescription() {
    return "Loads and performs merges based on mergefacts.src.";
  }
}