/*
 *    Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.umls.server.jpa.algo.insert;

import java.io.File;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

import com.wci.umls.server.AlgorithmParameter;
import com.wci.umls.server.ValidationResult;
import com.wci.umls.server.helpers.ConfigUtility;
import com.wci.umls.server.jpa.ValidationResultJpa;
import com.wci.umls.server.jpa.algo.AbstractAlgorithm;
import com.wci.umls.server.services.RootService;
import com.wci.umls.server.services.handlers.IdentifierAssignmentHandler;

/**
 * Implementation of an algorithm to import mergefacts.
 */
public class MergeLoaderAlgorithm extends AbstractAlgorithm {

  /** The full directory where the src files are. */
  private File srcDirFile = null;

  /** The previous progress. */
  private int previousProgress;

  /** The steps. */
  private int steps;

  /** The steps completed. */
  private int stepsCompleted;

  /**
   * Instantiates an empty {@link MergeLoaderAlgorithm}.
   * @throws Exception if anything goes wrong
   */
  public MergeLoaderAlgorithm() throws Exception {
    super();
    setActivityId(UUID.randomUUID().toString());
    setWorkId("MERGELOADER");
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
      throw new Exception("Merge Loading requires a project to be set");
    }

    // Check the input directories

    String srcFullPath =
        ConfigUtility.getConfigProperties().getProperty("source.data.dir")
            + File.separator + getProcess().getInputPath();

    srcDirFile = new File(srcFullPath);
    if (!srcDirFile.exists()) {
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
    logInfo("Starting MERGELOADING");

    // No molecular actions will be generated by this algorithm
    setMolecularActionFlag(false);

    // Set up the handler for identifier assignment
    final IdentifierAssignmentHandler handler =
        newIdentifierAssignmentHandler(getProject().getTerminology());
    handler.setTransactionPerOperation(false);
    handler.beginTransaction();

    // Count number of added and updated Merges, for logging
    int addCount = 0;
    int updateCount = 0;

    try {

      previousProgress = 0;
      stepsCompleted = 0;

      logInfo("[MergeLoader] Checking for new/updated Merges");

      // Update the progress
      updateProgress();

      logAndCommit("[Merge Loader] Merges processed ", stepsCompleted,
          RootService.logCt, RootService.commitCt);

      logInfo("[MergeLoader] Added " + addCount + " new Merges.");
      logInfo("[MergeLoader] Updated " + updateCount + " existing Merges.");

      logInfo("  project = " + getProject().getId());
      logInfo("  workId = " + getWorkId());
      logInfo("  activityId = " + getActivityId());
      logInfo("  user  = " + getLastModifiedBy());
      logInfo("Finished MERGELOADING");

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

  /**
   * Update progress.
   *
   * @throws Exception the exception
   */
  public void updateProgress() throws Exception {
    stepsCompleted++;
    int currentProgress = (int) ((100.0 * stepsCompleted / steps));
    if (currentProgress > previousProgress) {
      fireProgressEvent(currentProgress,
          "MERGELOADING progress: " + currentProgress + "%");
      previousProgress = currentProgress;
    }
  }

  /**
   * Sets the properties.
   *
   * @param p the properties
   * @throws Exception the exception
   */
  /* see superclass */
  @Override
  public void setProperties(Properties p) throws Exception {
    checkRequiredProperties(new String[] {
        // TODO - handle problem with config.properties needing properties
    }, p);

  }

  /**
   * Returns the parameters.
   *
   * @return the parameters
   */
  /* see superclass */
  @Override
  public List<AlgorithmParameter> getParameters() {
    final List<AlgorithmParameter> params = super.getParameters();

    return params;
  }

}