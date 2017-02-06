/*
 *    Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.umls.server.jpa.algo.insert;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import com.wci.umls.server.AlgorithmParameter;
import com.wci.umls.server.ValidationResult;
import com.wci.umls.server.helpers.Branch;
import com.wci.umls.server.helpers.ConfigUtility;
import com.wci.umls.server.helpers.FieldedStringTokenizer;
import com.wci.umls.server.jpa.ValidationResultJpa;
import com.wci.umls.server.jpa.algo.AbstractInsertMaintReleaseAlgorithm;
import com.wci.umls.server.jpa.content.AtomJpa;
import com.wci.umls.server.jpa.content.CodeJpa;
import com.wci.umls.server.jpa.content.ConceptJpa;
import com.wci.umls.server.jpa.content.DescriptorJpa;
import com.wci.umls.server.jpa.content.LexicalClassJpa;
import com.wci.umls.server.jpa.content.StringClassJpa;
import com.wci.umls.server.model.content.Atom;
import com.wci.umls.server.model.content.Code;
import com.wci.umls.server.model.content.Concept;
import com.wci.umls.server.model.content.Descriptor;
import com.wci.umls.server.model.content.LexicalClass;
import com.wci.umls.server.model.content.StringClass;
import com.wci.umls.server.model.meta.TermType;
import com.wci.umls.server.model.meta.Terminology;
import com.wci.umls.server.model.workflow.WorkflowStatus;
import com.wci.umls.server.services.RootService;
import com.wci.umls.server.services.handlers.IdentifierAssignmentHandler;

/**
 * Algorithm responsible for loading "classes_atoms.src" files.
 */
public class AtomLoaderAlgorithm extends AbstractInsertMaintReleaseAlgorithm {

  /** The add count. */
  private int addCount = 0;

  /** The update count. */
  private int updateCount = 0;

  /**
   * Instantiates an empty {@link AtomLoaderAlgorithm}.
   *
   * @throws Exception the exception
   */
  public AtomLoaderAlgorithm() throws Exception {
    super();
    setActivityId(UUID.randomUUID().toString());
    setWorkId("ATOMLOADER");
    setLastModifiedBy("admin");
  }

  /* see superclass */
  @Override
  public ValidationResult checkPreconditions() throws Exception {

    ValidationResult validationResult = new ValidationResultJpa();

    if (getProject() == null) {
      throw new Exception("Atom Loading requires a project to be set");
    }

    // Check the input directories

    String srcFullPath =
        ConfigUtility.getConfigProperties().getProperty("source.data.dir")
            + File.separator + getProcess().getInputPath();

    setSrcDirFile(new File(srcFullPath));
    if (!getSrcDirFile().exists()) {
      throw new Exception("Specified input directory does not exist");
    }

    return validationResult;
  }

  /* see superclass */
  @Override
  public void compute() throws Exception {
    logInfo("Starting " + getName());

    // No molecular actions will be generated by this algorithm
    setMolecularActionFlag(false);

    // Set up the handler for identifier assignment
    final IdentifierAssignmentHandler handler =
        newIdentifierAssignmentHandler(getProject().getTerminology());
    handler.setTransactionPerOperation(false);
    handler.beginTransaction();

    try {

      //
      // Load the classes_atoms.src file
      //
      final List<String> lines = loadFileIntoStringList(getSrcDirFile(),
          "classes_atoms.src", null, null);

      logInfo("  Process classes_atoms.src");
      commitClearBegin();

      // Set the number of steps to the number of atoms to be processed
      setSteps(lines.size());

      final String fields[] = new String[15];

      // Each line of classes_atoms.src corresponds to one atom.
      // Check to make sure the atom doesn't already exist in the database
      // If it does, skip it.
      // If it does not, add it.
      for (final String line : lines) {

        // Check for a cancelled call once every 100 lines
        if (getStepsCompleted() % 100 == 0) {
          checkCancel();
        }

        FieldedStringTokenizer.split(line, "|", 15, fields);

        // Fields:
        // 0 src_atom_id (Atom.alternateTerminologlyId(), where Key =
        // ProjectTerminology + "-SRC")
        // 1 source (Atom.terminology, Atom.version)
        // 2 termgroup (Atom.termType (portion after the forward-slash))
        // 3 code (Atom.codeId)
        // 4 status (Atom.WorkflowStatus)
        // 5 tobereleased (Atom.publishable)
        // 6 released (Atom.published)
        // 7 atom_name (Atom.name)
        // 8 suppressible (Atom.suppresible, Atom.obsolete)
        // obsolete = true IFF "O".
        // suppresible = true IFF "O, Y, E";
        // 9 source_aui (Atom.terminologyId)
        // 10 source_cui (Atom.conceptId)
        // 11 source_dui (Atom.descriptorId)
        // 12 language (Atom.language)
        // 13 order_id
        // 14 last_release_cui (Atom.conceptTerminologyId(), where
        // Key=ProjectTerminology)

        // e.g.
        // 362166319|NCI_2016_05E|NCI_2016_05E/PT|C28777|R|Y|N|1,9-Nonanediol|N||
        // C28777||ENG|362166319|

        //
        // Atom based on input line.
        //
        final Atom newAtom = new AtomJpa();
        if (!ConfigUtility.isEmpty(fields[0])) {
          newAtom.getAlternateTerminologyIds()
              .put(getProject().getTerminology() + "-SRC", fields[0]);
        }
        final Terminology terminology = getCachedTerminology(fields[1]);
        if (terminology == null) {
          logWarnAndUpdate(line,
              "WARNING - terminology not found: " + fields[1] + ".");
          continue;
        } else {
          newAtom.setTerminology(terminology.getTerminology());
          newAtom.setVersion(terminology.getVersion());
        }
        newAtom.setTermType(fields[2].substring(fields[2].indexOf("/") + 1));
        newAtom.setCodeId(fields[3]);
        if (fields[4].equals("N")) {
          newAtom.setWorkflowStatus(WorkflowStatus.NEEDS_REVIEW);
        }
        if (fields[4].equals("R")) {
          newAtom.setWorkflowStatus(WorkflowStatus.READY_FOR_PUBLICATION);
        }
        newAtom
            .setPublishable((fields[5].equals("Y") || fields[5].equals("y")));
        newAtom.setPublished((fields[6].equals("Y")));
        newAtom.setName(fields[7]);
        newAtom.setSuppressible("OYE".contains(fields[8]));
        newAtom.setObsolete(fields[8].equals("O"));
        newAtom.setTerminologyId(fields[9]);
        newAtom.setConceptId(fields[10]);
        newAtom.setDescriptorId(fields[11]);
        newAtom.setLanguage(fields[12]);
        if (!ConfigUtility.isEmpty(fields[14])) {
          newAtom.getConceptTerminologyIds().put(getProject().getTerminology(),
              fields[14]);
        }

        // Add string and lexical classes to get assign their Ids to the Atom
        final StringClass strClass = new StringClassJpa();
        strClass.setLanguage(newAtom.getLanguage());
        strClass.setName(newAtom.getName());
        newAtom.setStringClassId(handler.getTerminologyId(strClass));

        final LexicalClass lexClass = new LexicalClassJpa();
        lexClass.setLanguage(newAtom.getLanguage());
        lexClass.setNormalizedName(getNormalizedString(newAtom.getName()));
        newAtom.setLexicalClassId(handler.getTerminologyId(lexClass));

        // Compute atom identity
        final String newAtomAui = handler.getTerminologyId(newAtom);

        // Check to see if atom with matching AUI already exists in the database
        final Atom oldAtom = (Atom) getComponent("AUI", newAtomAui, null, null);

        // If no atom with the same AUI exists, add this new Atom and a concept
        // to put it into.
        if (oldAtom == null) {
          newAtom.getAlternateTerminologyIds()
              .put(getProject().getTerminology(), newAtomAui);
          final Atom newAtom2 = addAtom(newAtom);

          // Create a new concept to store the atom
          final Concept newConcept = new ConceptJpa();
          newConcept.setTerminology(getProject().getTerminology());
          newConcept.setTerminologyId("");
          newConcept.setVersion(getProject().getVersion());
          newConcept.setObsolete(false);
          newConcept.setSuppressible(false);
          newConcept.setPublishable(true);
          newConcept.setPublished(false);
          newConcept.getAtoms().add(newAtom2);
          newConcept.setName(newAtom2.getName());
          newConcept.setWorkflowStatus(WorkflowStatus.NEEDS_REVIEW);
          final Concept newConcept2 = addConcept(newConcept);

          // Set the terminology Id
          newConcept.setTerminologyId(newConcept2.getId().toString());
          updateConcept(newConcept2);

          addCount++;
          putComponent(newAtom2, newAtomAui);

          // Reconcile code/concept/descriptor
          reconcileCodeConceptDescriptor(newAtom2);

        }
        // If a previous atom with same AUI exists, update that object.
        else {

          boolean oldAtomChanged = false;

          // Update "alternateTerminologyIds"
          final Map<String, String> altTermIds =
              oldAtom.getAlternateTerminologyIds();
          if (altTermIds.get(getProject().getTerminology() + "-SRC") == null
              || !altTermIds.get(getProject().getTerminology() + "-SRC")
                  .equals(fields[0])) {
            oldAtom.getAlternateTerminologyIds()
                .put(getProject().getTerminology() + "-SRC", fields[0]);
            oldAtomChanged = true;
          }

          // Update the version
          if (!oldAtom.getVersion().equals(newAtom.getVersion())) {
            oldAtom.setVersion(newAtom.getVersion());
            oldAtomChanged = true;
          }

          // If this loaded Atom is not exactly the same as the new Atom:
          if (!oldAtom.equals(newAtom)) {

            // Update obsolete and suppresible.
            // If the old version of the atom is suppresible, and its term type
            // is not, keep the old atom's suppresibility. Otherwise, use the
            // new Atom's suppresible value.
            final TermType atomTty = getCachedTermType(oldAtom.getTermType());
            if (oldAtom.isSuppressible() != newAtom.isSuppressible()
                && !(oldAtom.isSuppressible() && !atomTty.isSuppressible())) {
              oldAtom.setSuppressible(newAtom.isSuppressible());
              oldAtomChanged = true;
            }
            if (oldAtom.isObsolete() != newAtom.isObsolete()
                && !(oldAtom.isObsolete() && !atomTty.isObsolete())) {
              oldAtom.setObsolete(newAtom.isObsolete());
              oldAtomChanged = true;
            }
          }

          if (oldAtomChanged) {
            updateAtom(oldAtom);
            updateCount++;

            // Reconcile code/concept/descriptor
            reconcileCodeConceptDescriptor(oldAtom);
          }
        }

        // Update the progress
        updateProgress();
        handler.silentIntervalCommit(getStepsCompleted(), RootService.logCt,
            RootService.commitCt);

      }

      commitClearBegin();
      handler.commit();

      logInfo("  added count = " + addCount);
      logInfo("  updated count = " + updateCount);

      logInfo("Finished " + getName());

    } catch (Exception e) {
      logError("Unexpected problem - " + e.getMessage());
      handler.rollback();
      handler.close();
      throw e;
    }

  }

  /**
   * Reconcile code concept descriptor.
   *
   * @param atom the atom
   * @throws Exception the exception
   */
  private void reconcileCodeConceptDescriptor(Atom atom) throws Exception {
    // Check map to see if code already exists
    if (!atom.getCodeId().isEmpty()) {

      // Use getComponent because it caches stuff in the background
      final Code existingCode = (Code) getComponent("CODE_SOURCE",
          atom.getCodeId(), atom.getTerminology(), null);

      if (existingCode != null) {
        existingCode.getAtoms().add(atom);
        existingCode.setVersion(atom.getVersion());
        updateCode(existingCode);
      }

      // else create a new code
      else {
        final Code newCode = new CodeJpa();
        newCode.setTerminology(atom.getTerminology());
        newCode.setTerminologyId(atom.getCodeId());
        newCode.setVersion(atom.getVersion());
        newCode.setBranch(Branch.ROOT);
        newCode.setName(atom.getName());
        newCode.setObsolete(false);
        newCode.setPublished(false);
        newCode.setPublishable(true);
        newCode.setSuppressible(false);
        newCode.setWorkflowStatus(WorkflowStatus.READY_FOR_PUBLICATION);

        newCode.getAtoms().add(atom);
        addCode(newCode);
        putComponent(newCode, newCode.getTerminologyId());
      }
    }

    // Check map to see if concept already exists
    if (!atom.getConceptId().isEmpty()) {

      final Concept existingConcept = (Concept) getComponent("SOURCE_CUI",
          atom.getConceptId(), atom.getTerminology(), null);
      if (existingConcept != null) {
        existingConcept.getAtoms().add(atom);
        existingConcept.setVersion(atom.getVersion());
        updateConcept(existingConcept);
      }

      // else create a new concept
      else {
        final Concept newConcept = new ConceptJpa();
        newConcept.setTerminology(atom.getTerminology());
        newConcept.setTerminologyId(atom.getConceptId());
        newConcept.setVersion(atom.getVersion());
        newConcept.setBranch(Branch.ROOT);
        newConcept.setName(atom.getName());
        newConcept.setObsolete(false);
        newConcept.setPublished(false);
        newConcept.setPublishable(true);
        newConcept.setSuppressible(false);
        newConcept.setWorkflowStatus(WorkflowStatus.READY_FOR_PUBLICATION);

        newConcept.getAtoms().add(atom);
        addConcept(newConcept);
        putComponent(newConcept, newConcept.getTerminologyId());
      }
    }
    // Check map to see if descriptor already exists
    if (!atom.getDescriptorId().isEmpty()) {

      final Descriptor existingDescriptor =
          (Descriptor) getComponent("SOURCE_DUI", atom.getConceptId(),
              atom.getTerminology(), null);
      if (existingDescriptor != null) {
        existingDescriptor.getAtoms().add(atom);
        existingDescriptor.setVersion(atom.getVersion());
        updateDescriptor(existingDescriptor);
      }

      // else create a new descriptor
      else {
        final Descriptor newDescriptor = new DescriptorJpa();
        newDescriptor.setTerminology(atom.getTerminology());
        newDescriptor.setTerminologyId(atom.getDescriptorId());
        newDescriptor.setVersion(atom.getVersion());
        newDescriptor.setBranch(Branch.ROOT);
        newDescriptor.setName(atom.getName());
        newDescriptor.setObsolete(false);
        newDescriptor.setPublished(false);
        newDescriptor.setPublishable(true);
        newDescriptor.setSuppressible(false);
        newDescriptor.setWorkflowStatus(WorkflowStatus.READY_FOR_PUBLICATION);

        newDescriptor.getAtoms().add(atom);
        addDescriptor(newDescriptor);
        putComponent(newDescriptor, newDescriptor.getTerminologyId());
      }
    }
  }

  /* see superclass */
  @Override
  public void reset() throws Exception {
    // n/a - No reset
  }

  /* see superclass */
  @Override
  public void checkProperties(Properties p) throws Exception {
    // n/a
  }

  /* see superclass */
  @Override
  public void setProperties(Properties p) throws Exception {
    // n/a
  }

  /* see superclass */
  @Override
  public List<AlgorithmParameter> getParameters() throws Exception {
    final List<AlgorithmParameter> params = super.getParameters();
    return params;
  }

  /* see superclass */
  @Override
  public String getDescription() {
    return "Loads and processes a classes_atoms.src file.";
  }
}