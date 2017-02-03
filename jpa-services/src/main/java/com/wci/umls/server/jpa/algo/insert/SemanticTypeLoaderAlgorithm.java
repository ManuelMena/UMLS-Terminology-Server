/*
 *    Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.umls.server.jpa.algo.insert;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import javax.persistence.Query;

import com.wci.umls.server.AlgorithmParameter;
import com.wci.umls.server.ValidationResult;
import com.wci.umls.server.helpers.Branch;
import com.wci.umls.server.helpers.ConfigUtility;
import com.wci.umls.server.helpers.FieldedStringTokenizer;
import com.wci.umls.server.jpa.ValidationResultJpa;
import com.wci.umls.server.jpa.algo.AbstractInsertMaintReleaseAlgorithm;
import com.wci.umls.server.jpa.content.SemanticTypeComponentJpa;
import com.wci.umls.server.model.content.Atom;
import com.wci.umls.server.model.content.AtomClass;
import com.wci.umls.server.model.content.Component;
import com.wci.umls.server.model.content.Concept;
import com.wci.umls.server.model.content.SemanticTypeComponent;
import com.wci.umls.server.services.handlers.ComputePreferredNameHandler;

/**
 * Implementation of an algorithm to import semantic types.
 */
public class SemanticTypeLoaderAlgorithm
    extends AbstractInsertMaintReleaseAlgorithm {

  /**
   * Instantiates an empty {@link SemanticTypeLoaderAlgorithm}.
   * @throws Exception if anything goes wrong
   */
  public SemanticTypeLoaderAlgorithm() throws Exception {
    super();
    setActivityId(UUID.randomUUID().toString());
    setWorkId("SEMANTICTYPELOADER");
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
      throw new Exception("Semantic Type Loading requires a project to be set");
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

  /**
   * Compute.
   *
   * @throws Exception the exception
   */
  /* see superclass */
  @Override
  public void compute() throws Exception {
    logInfo("Starting SEMANTICTYPELOADING");
    commitClearBegin();

    // No molecular actions will be generated by this algorithm
    setMolecularActionFlag(false);

    // Set up the search handler
    final ComputePreferredNameHandler prefNameHandler =
        getComputePreferredNameHandler(getProject().getTerminology());

    // Count number of added and updated Semantic Types, for logging
    int addCount = 0;
    int updateCount = 0;

    try {

      //
      // Cache all atom->concept
      //
      final Query query = manager
          .createQuery("select a.id, c.id from ConceptJpa c join c.atoms a "
              + "where c.terminology = :terminology "
              + "  and c.version = :version and c.publishable = true ");
      query.setParameter("terminology", getProject().getTerminology());
      query.setParameter("version", getProject().getVersion());
      final Map<Long, Long> atomConceptMap = new HashMap<>();
      @SuppressWarnings("unchecked")
      final List<Object[]> ids = query.getResultList();
      for (final Object[] result : ids) {
        Long atomId = Long.valueOf(result[0].toString());
        Long conceptId = Long.valueOf(result[1].toString());
        atomConceptMap.put(atomId, conceptId);
      }

      //
      // Load the attributes.src file, only keeping SEMANTIC_TYPE lines.
      //
      final List<String> lines =
          loadFileIntoStringList(getSrcDirFile(), "attributes.src",
              "(([a-zA-Z0-9]+?)\\|){3}(SEMANTIC_TYPE\\|){1}(.*)", null);

      // Set the number of steps to the number of atoms to be processed
      setSteps(lines.size());

      final String fields[] = new String[14];

      for (final String line : lines) {

        FieldedStringTokenizer.split(line, "|", 14, fields);

        // Fields:
        // 0 source_attribute_id
        // 1 sg_id
        // 2 attribute_level
        // 3 attribute_name
        // 4 attribute_value
        // 5 source
        // 6 status
        // 7 tobereleased
        // 8 released
        // 9 suppressible
        // 10 sg_type_1
        // 11 sg_qualifier_1
        // 12 source_atui
        // 13 hashcode

        // e.g.
        // 49|C47666|S|Chemical_Formula|C19H32N2O5.C4H11N|NCI_2016_05E|R|Y|N|N|SOURCE_CUI|NCI_2016_05E||875b4a03f8dedd9de05d6e9e4a440401|

        // Load the referenced atom, or preferred atom of atomClass object
        final Component component = getComponent(fields[10], fields[1],
            getCachedTerminologyName(fields[11]), null);
        if (component == null) {
          logWarnAndUpdate(line,
              "Warning - could not find Component for type: " + fields[10]
                  + ", terminologyId: " + fields[1] + ", and terminology:"
                  + fields[11]);
          continue;
        }
        Atom atom = null;
        if (component instanceof Atom) {
          atom = (Atom) component;
        } else if (component instanceof AtomClass) {
          final AtomClass atomClass = (AtomClass) component;
          final List<Atom> atoms =
              prefNameHandler.sortAtoms(atomClass.getAtoms(), getPrecedenceList(
                  getProject().getTerminology(), getProject().getVersion()));
          atom = atoms.get(0);
        } else {
          logWarnAndUpdate(line, "Warning - " + component.getClass().getName()
              + " is an unhandled type.");
          continue;
        }

        // Get the concept associated with the loaded atom
        final Concept concept = getConcept(atomConceptMap.get(atom.getId()));

        // If concept has a semantic type already matching this value, move on
        // otherwise add a new semantic type.
        boolean componentContainsSty = false;
        for (final SemanticTypeComponent sty : concept.getSemanticTypes()) {
          if (sty.getSemanticType().equals(fields[4])) {
            componentContainsSty = true;
            break;
          }
        }

        if (!componentContainsSty) {
          final SemanticTypeComponent newSty = new SemanticTypeComponentJpa();
          newSty.setBranch(Branch.ROOT);
          newSty.setName(fields[4]);
          newSty.setPublishable(fields[7].toUpperCase().equals("Y"));
          newSty.setPublished(fields[8].toUpperCase().equals("Y"));
          newSty.setSemanticType(fields[4]);
          newSty.setSuppressible(false);
          newSty.setObsolete(false);
          newSty.setTerminology(getProject().getTerminology());
          newSty.setVersion(getProject().getVersion());
          newSty.setTerminologyId("");
          newSty.setWorkflowStatus(lookupWorkflowStatus(fields[6]));

          final SemanticTypeComponent newSty2 =
              addSemanticTypeComponent(newSty, concept);
          concept.getSemanticTypes().add(newSty2);
          updateConcept(concept);

          addCount++;
        }

        // Update the progress
        updateProgress();

      }

      commitClearBegin();

      logInfo(
          "[SemanticTypeLoader] Added " + addCount + " new Semantic Types.");
      logInfo("[SemanticTypeLoader] Updated " + updateCount
          + " existing Semantic Types.");

      logInfo("Finished SEMANTICTYPELOADING");

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

  @Override
  public String getDescription() {
    return "Loads and processes an attributes.src file to load semantic type components.";
  }

}