/*
 *    Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.umls.server.jpa.algo;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import com.wci.umls.server.Project;
import com.wci.umls.server.ValidationResult;
import com.wci.umls.server.helpers.Branch;
import com.wci.umls.server.helpers.FieldedStringTokenizer;
import com.wci.umls.server.jpa.algo.action.AddDemotionMolecularAction;
import com.wci.umls.server.jpa.algo.action.MergeMolecularAction;
import com.wci.umls.server.jpa.content.ConceptJpa;
import com.wci.umls.server.model.content.Atom;
import com.wci.umls.server.model.content.Concept;

/**
 * Abstract support for merge algorithms.
 */
public abstract class AbstractMergeAlgorithm
    extends AbstractSourceInsertionAlgorithm {

  /**
   * Instantiates an empty {@link AbstractMergeAlgorithm}.
   *
   * @throws Exception the exception
   */
  public AbstractMergeAlgorithm() throws Exception {
    // n/a
  }

  /**
   * Merge.
   *
   * @param atomId the atom id
   * @param atomId2 the atom id 2
   * @param validationChecks the integrity check names
   * @param makeDemotion the make demotion
   * @param changeStatus the change status
   * @param project the project
   * @param statsMap the stats map
   * @throws Exception the exception
   */
  public void merge(Long atomId, Long atomId2, List<String> validationChecks,
    boolean makeDemotion, boolean changeStatus, Project project,
    Map<String, Integer> statsMap) throws Exception {

    // Get the two concepts associated with the two atoms
    List<ConceptJpa> concepts =
        searchHandler.getQueryResults(getProject().getTerminology(),
            getProject().getVersion(), Branch.ROOT, "atoms.id:" + atomId, null,
            ConceptJpa.class, null, new int[1], getEntityManager());
    if (concepts.size() != 1) {
      throw new Exception("Unexpected number of concepts: " + concepts.size()
          + ", for atom: " + atomId);
    }
    final Concept concept = concepts.get(0);

    concepts = searchHandler.getQueryResults(getProject().getTerminology(),
        getProject().getVersion(), Branch.ROOT, "atoms.id:" + atomId2, null,
        ConceptJpa.class, null, new int[1], getEntityManager());
    if (concepts.size() != 1) {
      throw new Exception("Unexpected number of concepts: " + concepts.size()
          + ", for atom: " + atomId2);
    }
    final Concept concept2 = concepts.get(0);

    // Identify the from and to concepts, and from/to Atoms
    // FromConcept will be the smaller concept (least number of atoms)
    Concept fromConcept = null;
    Concept toConcept = null;
    Atom fromAtom = null;
    Atom toAtom = null;

    if (concept.getAtoms().size() < concept2.getAtoms().size()) {
      fromConcept = concept;
      fromAtom = getAtom(atomId);
      toConcept = concept2;
      toAtom = getAtom(atomId2);
    } else {
      fromConcept = concept2;
      fromAtom = getAtom(atomId2);
      toConcept = concept;
      toAtom = getAtom(atomId);
    }

    // If Atoms are in the same concept, DON'T perform merge, and log that the
    // atoms are already merged.
    if (fromConcept.getId().equals(toConcept.getId())) {
      addLogEntry(getLastModifiedBy(), getProject().getId(),
          fromConcept.getId(), getActivityId(), getWorkId(),
          "Failure merging atom " + atomId + " with atom " + atomId
              + " - atoms are both already in the same concept "
              + toConcept.getId());

      statsMap.put("unsuccessfulMerges",
          statsMap.get("unsuccessfulMerges") + 1);
      return;
    }

    // Otherwise, create and set up a merge action
    final MergeMolecularAction action = new MergeMolecularAction();

    try {

      // Configure the action
      action.setProject(getProject());
      action.setActivityId(getActivityId());
      action.setConceptId(fromConcept.getId());
      action.setConceptId2(toConcept.getId());
      action.setLastModifiedBy(getLastModifiedBy());
      action.setLastModified(fromConcept.getLastModified().getTime());
      action.setOverrideWarnings(false);
      action.setTransactionPerOperation(false);
      action.setMolecularActionFlag(true);
      action.setChangeStatusFlag(changeStatus);
      action.setValidationChecks(validationChecks);

      // Perform the action
      final ValidationResult validationResult =
          action.performMolecularAction(action, getLastModifiedBy(), false);

      // If the action failed, log the failure, and make a demotion if
      // makeDemotion=true.
      if (!validationResult.isValid()) {
        addLogEntry(getLastModifiedBy(), getProject().getId(),
            fromConcept.getId(), getActivityId(), getWorkId(),
            "FAIL " + action.getName() + " concept " + fromConcept.getId()
                + " into concept " + toConcept.getId() + ": "
                + validationResult);
        addLogEntry(getLastModifiedBy(), getProject().getId(),
            toConcept.getId(), getActivityId(), getWorkId(),
            "FAIL " + action.getName() + " concept " + toConcept.getId()
                + " from concept " + fromConcept.getId() + ": "
                + validationResult);

        if (makeDemotion) {
          final AddDemotionMolecularAction action2 =
              new AddDemotionMolecularAction();
          action2.setTransactionPerOperation(false);
          action2.setProject(getProject());
          action2.setTerminology(getProject().getTerminology());
          action2.setVersion(getProject().getVersion());
          action2.setWorkId(getWorkId());
          action2.setActivityId(getActivityId());
          action2.setAtom(fromAtom);
          action2.setAtom2(toAtom);
          action2.setChangeStatusFlag(changeStatus);
          action2.setConceptId(fromConcept.getId());
          action2.setConceptId2(toConcept.getId());
          action2.setLastModifiedBy(getLastModifiedBy());
          ValidationResult demotionValidationResult = action2
              .performMolecularAction(action2, getLastModifiedBy(), false);

          // If there is already a demotion between these two atoms, it will
          // return a validation error
          if (!demotionValidationResult.isValid()) {
            addLogEntry(getLastModifiedBy(), getProject().getId(),
                fromConcept.getId(), getActivityId(), getWorkId(),
                "FAIL " + action2.getName() + " to concept "
                    + fromConcept.getId() + ": " + demotionValidationResult);
            addLogEntry(getLastModifiedBy(), getProject().getId(),
                toConcept.getId(), getActivityId(), getWorkId(),
                "FAIL " + action2.getName() + " from concept "
                    + fromConcept.getId() + ": " + demotionValidationResult);

            addLogEntry(getLastModifiedBy(), getProject().getId(),
                fromConcept.getId(), getActivityId(), getWorkId(),
                "" + demotionValidationResult);

            statsMap.put("unsuccessfulDemotions",
                statsMap.get("unsuccessfulDemotions") + 1);
          }
          // Otherwise, the demotion was successfully added
          else {
            statsMap.put("successfulDemotions",
                statsMap.get("successfulDemotions") + 1);
          }
          action2.close();
        }

        statsMap.put("unsuccessfulMerges",
            statsMap.get("unsuccessfulMerges") + 1);
        return;
      }
      // Otherwise, it was successful.
      else {
        statsMap.put("successfulMerges", statsMap.get("successfulMerges") + 1);
        return;
      }

    } catch (Exception e) {
      try {
        action.rollback();
        e.printStackTrace();
      } catch (Exception e2) {
        // do nothing
      }
      statsMap.put("unsuccessfulMerges",
          statsMap.get("unsuccessfulMerges") + 1);
      return;
    } finally {
      // NEED to commit here to make sure that any changes made to the database
      // by MergeMolecularAction or AddDemotionMolecularAction are viewable by
      // this algorithm
      commitClearBegin();
      action.close();
    }

  }

  /**
   * Returns the merge sets.
   *
   * @param srcDirFile the src dir file
   * @return the merge sets
   */
  public List<String> getMergeSets(File srcDirFile) {

    final List<String> mergeSets = new ArrayList<>();
    final Set<String> mergeSetsUnique = new HashSet<>();
    List<String> lines = new ArrayList<>();
    //
    // Load the mergefacts.src file
    //
    try {
      lines = loadFileIntoStringList(srcDirFile, "mergefacts.src", null, null);
    }
    // If file not found, return null
    catch (Exception e) {
      return null;
    }

    final int fieldCount = StringUtils.countMatches(lines.get(0), "|") + 1;
    String fields[] = new String[fieldCount];

    // For this method's purpose, the only field we care about is merge_set, at
    // index 7
    for (String line : lines) {
      FieldedStringTokenizer.split(line, "|", fieldCount, fields);
      final String mergeSet = fields[7];
      mergeSetsUnique.add(mergeSet);
    }

    // Add all of the unique mergeSets referenced in the file to the stringList,
    // and return
    mergeSets.addAll(mergeSetsUnique);

    return mergeSets;
  }

  /**
   * Returns the merge level or an atomId pair.
   *
   * @param atomIdPair the atom id pair
   * @return the merge level
   */
  public Long calculateMergeLevel(Pair<Long, Long> atomIdPair) {
    // MergeLevel =
    // 1 => atom1.code=atom2.code && atom1.sui=atom2.sui && atom1.tty=atom2.tty
    // 2 => atom1.code=atom2.code && atom1.lui=atom2.lui && atom1.tty=atom2.tty
    // 3 => atom1.code=atom2.code && atom1.sui=atom2.sui
    // 4 => atom1.code=atom2.code && atom1.lui=atom2.lui
    // 5 => atom1.code=atom2.code
    // 9 => no equivalence, or equivalence not able to be determined

    Long mergeLevel = null;
    Atom atom1 = null;
    Atom atom2 = null;
    try {
      atom1 = getAtom(atomIdPair.getLeft());
      atom2 = getAtom(atomIdPair.getRight());
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    if (atom1.getCodeId().equals(atom2.getCodeId())
        && atom1.getStringClassId().equals(atom2.getStringClassId())
        && atom1.getTermType().equals(atom2.getTermType())) {
      mergeLevel = 1L;
    } else if (atom1.getCodeId().equals(atom2.getCodeId())
        && atom1.getLexicalClassId().equals(atom2.getLexicalClassId())
        && atom1.getTermType().equals(atom2.getTermType())) {
      mergeLevel = 2L;
    } else if (atom1.getCodeId().equals(atom2.getCodeId())
        && atom1.getStringClassId().equals(atom2.getStringClassId())) {
      mergeLevel = 3L;
    } else if (atom1.getCodeId().equals(atom2.getCodeId())
        && atom1.getLexicalClassId().equals(atom2.getLexicalClassId())) {
      mergeLevel = 4L;
    } else if (atom1.getCodeId().equals(atom2.getCodeId())) {
      mergeLevel = 5L;
    } else {
      mergeLevel = 9L;
    }

    return mergeLevel;
  }

  /**
   * Sort pairs by merge level and id.
   *
   * @param filteredAtomIdPairs the filtered atom id pairs
   */
  public void sortPairsByMergeLevelAndId(
    List<Pair<Long, Long>> filteredAtomIdPairs) {

    // Order atomIdPairs
    // sort by MergeLevel, atomId1, atomId2
    Collections.sort(filteredAtomIdPairs, new Comparator<Pair<Long, Long>>() {

      @Override
      public int compare(final Pair<Long, Long> atomIdPair1,
        final Pair<Long, Long> atomIdPair2) {
        int c = 0;
        c = calculateMergeLevel(atomIdPair1)
            .compareTo(calculateMergeLevel(atomIdPair2));
        if (c == 0)
          c = atomIdPair1.getLeft().compareTo(atomIdPair2.getLeft());
        if (c == 0)
          c = atomIdPair1.getRight().compareTo(atomIdPair2.getRight());

        return c;
      }
    });
  }

}