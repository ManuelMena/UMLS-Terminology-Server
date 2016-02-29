/*
 *    Copyright 2016 West Coast Informatics, LLC
 */
package com.wci.umls.server.jpa.algo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.apache.log4j.Logger;

import com.wci.umls.server.algo.Algorithm;
import com.wci.umls.server.jpa.content.AtomRelationshipJpa;
import com.wci.umls.server.jpa.content.AtomSubsetJpa;
import com.wci.umls.server.jpa.content.AtomSubsetMemberJpa;
import com.wci.umls.server.jpa.content.CodeRelationshipJpa;
import com.wci.umls.server.jpa.content.CodeTransitiveRelationshipJpa;
import com.wci.umls.server.jpa.content.CodeTreePositionJpa;
import com.wci.umls.server.jpa.content.ConceptRelationshipJpa;
import com.wci.umls.server.jpa.content.ConceptSubsetJpa;
import com.wci.umls.server.jpa.content.ConceptSubsetMemberJpa;
import com.wci.umls.server.jpa.content.ConceptTransitiveRelationshipJpa;
import com.wci.umls.server.jpa.content.ConceptTreePositionJpa;
import com.wci.umls.server.jpa.content.DescriptorRelationshipJpa;
import com.wci.umls.server.jpa.content.DescriptorTransitiveRelationshipJpa;
import com.wci.umls.server.jpa.content.DescriptorTreePositionJpa;
import com.wci.umls.server.jpa.services.HistoryServiceJpa;
import com.wci.umls.server.model.content.Atom;
import com.wci.umls.server.model.content.Concept;
import com.wci.umls.server.model.content.Definition;
import com.wci.umls.server.model.meta.AdditionalRelationshipType;
import com.wci.umls.server.model.meta.AttributeName;
import com.wci.umls.server.model.meta.GeneralMetadataEntry;
import com.wci.umls.server.model.meta.IdType;
import com.wci.umls.server.model.meta.Language;
import com.wci.umls.server.model.meta.PropertyChain;
import com.wci.umls.server.model.meta.RelationshipType;
import com.wci.umls.server.model.meta.RootTerminology;
import com.wci.umls.server.model.meta.SemanticType;
import com.wci.umls.server.model.meta.TermType;
import com.wci.umls.server.model.meta.Terminology;
import com.wci.umls.server.services.ContentService;
import com.wci.umls.server.services.RootService;
import com.wci.umls.server.services.helpers.ProgressEvent;
import com.wci.umls.server.services.helpers.ProgressListener;

/**
 * Implementation of an algorithm to compute transitive closure using the
 * {@link ContentService}.
 */
public class RemoveTerminologyAlgorithm extends HistoryServiceJpa
    implements Algorithm {

  /** Listeners. */
  private List<ProgressListener> listeners = new ArrayList<>();

  /** The request cancel flag. */
  boolean requestCancel = false;

  /** The terminology. */
  private String terminology;

  /** The terminology version. */
  private String version;

  /** The id type. */
  private IdType idType;

  /** The cycle tolerant. */
  private boolean cycleTolerant;

  /**
   * Standalone means that it is not also represented as part of a
   * metathesaurus. Default is true.
   */
  private boolean standalone = true;

  /**
   * Instantiates an empty {@link RemoveTerminologyAlgorithm}.
   * @throws Exception if anything goes wrong
   */
  public RemoveTerminologyAlgorithm() throws Exception {
    super();
  }

  /**
   * Sets the terminology.
   *
   * @param terminology the terminology
   */
  public void setTerminology(String terminology) {
    this.terminology = terminology;
  }

  /**
   * Sets the terminology version.
   *
   * @param version the terminology version
   */
  public void setVersion(String version) {
    this.version = version;
  }

  /**
   * Returns the id type.
   *
   * @return the id type
   */
  public IdType getIdType() {
    return idType;
  }

  /**
   * Sets the id type.
   *
   * @param idType the id type
   */
  public void setIdType(IdType idType) {
    if (idType != IdType.CONCEPT && idType != IdType.DESCRIPTOR
        && idType != IdType.CODE) {
      throw new IllegalArgumentException(
          "Only CONCEPT, DESCRIPTOR, and CODE types are allowed.");
    }
    this.idType = idType;
  }

  /**
   * Indicates whether or not cycle tolerant is the case.
   *
   * @return <code>true</code> if so, <code>false</code> otherwise
   */
  public boolean isCycleTolerant() {
    return cycleTolerant;
  }

  /**
   * Sets the cycle tolerant.
   *
   * @param cycleTolerant the cycle tolerant
   */
  public void setCycleTolerant(boolean cycleTolerant) {
    this.cycleTolerant = cycleTolerant;
  }

  /**
   * Sets the standalone flag. For deleting a UMLS terminology, this would be
   * set to false so that the atoms would be removed also from UMLS concepts.
   *
   * @param standalone the standalone
   */
  public void setStandalone(boolean standalone) {
    this.standalone = standalone;
  }

  /* see superclass */
  @Override
  public void compute() throws Exception {
    removeTerminology(terminology, version, idType);
  }

  /* see superclass */
  @Override
  public void reset() throws Exception {
    // n/a
  }

  /**
   * Remove a terminology of a given version.
   *
   * @param terminology the terminology
   * @param version the terminology version
   * @param idType the id type
   * @throws Exception the exception
   */
  @SuppressWarnings("unchecked")
  private void removeTerminology(String terminology, String version,
    IdType idType) throws Exception {

    // NOTE: do not change the order of calls, they are tuned
    // to properly handle foreign key dependencies.

    // Check assumptions/prerequisites
    Logger.getLogger(getClass())
        .info("Start removing terminology - " + terminology + " " + version);
    fireProgressEvent(0, "Starting...");

    // Disable transaction per operation
    setTransactionPerOperation(false);

    beginTransaction();

    // remove terminology
    Logger.getLogger(getClass()).info("  Remove terminology");
    Terminology t = getTerminology(terminology, version);
    if (t != null) {
      Logger.getLogger(getClass()).info(
          "  remove terminology = " + t.getTerminology() + ", " + version);
      removeTerminology(t.getId());
      commitClearBegin();
      commitClearBegin();
    }

    // remove root terminology if all versions removed
    Logger.getLogger(getClass()).info("  Remove root terminology");
    for (RootTerminology root : getRootTerminologies().getObjects()) {
      if (root.getTerminology().equals(terminology)) {
        Logger.getLogger(getClass())
            .info("  remove root terminology = " + root.getTerminology());
        removeRootTerminology(root.getId());
        break;
      }
    }
    commitClearBegin();

    // remove property chain
    // Need to use query for metadata to override what the handler may be doing
    // this specifically means to remove things with this terminology/version
    // tuple
    Logger.getLogger(getClass()).info("  Remove property chains");
    Query query = manager.createQuery(
        "SELECT a FROM PropertyChainJpa a WHERE terminology = :terminology "
            + " AND version = :version");
    query.setParameter("terminology", terminology);
    query.setParameter("version", version);
    for (PropertyChain chain : (List<PropertyChain>) query.getResultList()) {
      Logger.getLogger(getClass()).info("  remove property chain = " + chain);
      removePropertyChain(chain.getId());

    }
    commitClearBegin();

    // remove attribute names
    Logger.getLogger(getClass()).info("  Remove attribute names");
    query = manager.createQuery(
        "SELECT a FROM AttributeNameJpa a WHERE terminology = :terminology "
            + " AND version = :version");
    query.setParameter("terminology", terminology);
    query.setParameter("version", version);
    for (AttributeName name : (List<AttributeName>) query.getResultList()) {
      Logger.getLogger(getClass()).info("  remove attribute name = " + name);
      removeAttributeName(name.getId());
    }
    commitClearBegin();

    // remove additional relationship type
    Logger.getLogger(getClass()).info("  Remove additional relationship types");
    query = manager.createQuery(
        "SELECT a FROM AdditionalRelationshipTypeJpa a WHERE terminology = :terminology "
            + " AND version = :version");
    query.setParameter("terminology", terminology);
    query.setParameter("version", version);
    for (AdditionalRelationshipType rela : (List<AdditionalRelationshipType>) query
        .getResultList()) {
      Logger.getLogger(getClass()).info("  set inverse to null = " + rela);
      rela.setInverse(null);
      updateAdditionalRelationshipType(rela);
    }
    commitClearBegin();
    query = manager.createQuery(
        "SELECT a FROM AdditionalRelationshipTypeJpa a WHERE terminology = :terminology "
            + " AND version = :version");
    query.setParameter("terminology", terminology);
    query.setParameter("version", version);
    for (AdditionalRelationshipType rela : (List<AdditionalRelationshipType>) query
        .getResultList()) {
      Logger.getLogger(getClass())
          .info("  remove additional relationship type = " + rela);
      removeAdditionalRelationshipType(rela.getId());
    }
    commitClearBegin();

    // remove general metadata entry
    Logger.getLogger(getClass()).info("  Remove general metadata");
    query = manager.createQuery(
        "SELECT a FROM GeneralMetadataEntryJpa a WHERE terminology = :terminology "
            + " AND version = :version");
    query.setParameter("terminology", terminology);
    query.setParameter("version", version);
    for (GeneralMetadataEntry entry : (List<GeneralMetadataEntry>) query
        .getResultList()) {
      Logger.getLogger(getClass())
          .info("  remove general metadata entry = " + entry);
      removeGeneralMetadataEntry(entry.getId());
    }
    commitClearBegin();

    // remove semantic types
    Logger.getLogger(getClass()).info("  Remove semantic types");
    query = manager.createQuery(
        "SELECT a FROM SemanticTypeJpa a WHERE terminology = :terminology "
            + " AND version = :version");
    query.setParameter("terminology", terminology);
    query.setParameter("version", version);
    for (SemanticType sty : (List<SemanticType>) query.getResultList()) {
      Logger.getLogger(getClass()).info("  remove semantic types = " + sty);
      removeSemanticType(sty.getId());
    }
    commitClearBegin();

    // remove term types
    Logger.getLogger(getClass()).info("  Remove term types");
    query = manager.createQuery(
        "SELECT a FROM TermTypeJpa a WHERE terminology = :terminology "
            + " AND version = :version");
    query.setParameter("terminology", terminology);
    query.setParameter("version", version);
    for (TermType tty : (List<TermType>) query.getResultList()) {
      Logger.getLogger(getClass()).info("  remove term types = " + tty);
      removeTermType(tty.getId());
    }
    commitClearBegin();

    // remove relationship type
    Logger.getLogger(getClass()).info("  Remove relationship types");
    query = manager.createQuery(
        "SELECT a FROM RelationshipTypeJpa a WHERE terminology = :terminology "
            + " AND version = :version");
    query.setParameter("terminology", terminology);
    query.setParameter("version", version);
    for (RelationshipType rel : (List<RelationshipType>) query
        .getResultList()) {
      Logger.getLogger(getClass()).info("  set inverse to null = " + rel);
      rel.setInverse(null);
      updateRelationshipType(rel);
    }
    commitClearBegin();
    query = manager.createQuery(
        "SELECT a FROM RelationshipTypeJpa a WHERE terminology = :terminology "
            + " AND version = :version");
    query.setParameter("terminology", terminology);
    query.setParameter("version", version);
    for (RelationshipType rel : (List<RelationshipType>) query
        .getResultList()) {
      Logger.getLogger(getClass()).info("  remove relationship type = " + rel);
      removeRelationshipType(rel.getId());
    }
    commitClearBegin();

    // remove languages
    Logger.getLogger(getClass()).info("  Remove languages");
    query = manager.createQuery(
        "SELECT a FROM LanguageJpa a WHERE terminology = :terminology "
            + " AND version = :version");
    query.setParameter("terminology", terminology);
    query.setParameter("version", version);
    for (Language lat : (List<Language>) query.getResultList()) {
      Logger.getLogger(getClass()).info("  remove languages = " + lat);
      removeLanguage(lat.getId());
    }
    commitClearBegin();

    // remove concept subset members
    Logger.getLogger(getClass()).info("  Remove concept subset members");
    query = manager.createQuery(
        "SELECT a.id FROM ConceptSubsetMemberJpa a WHERE terminology = :terminology "
            + " AND version = :version");
    query.setParameter("terminology", terminology);
    query.setParameter("version", version);
    int ct = 0;
    for (Long id : (List<Long>) query.getResultList()) {
      removeSubsetMember(id, ConceptSubsetMemberJpa.class);
      logAndCommit(++ct, RootService.logCt, RootService.commitCt);
    }
    commitClearBegin();

    // remove concept subsets
    Logger.getLogger(getClass()).info("  Remove concept subsets");
    query = manager.createQuery(
        "SELECT a.id FROM ConceptSubsetJpa a WHERE terminology = :terminology "
            + " AND version = :version");
    query.setParameter("terminology", terminology);
    query.setParameter("version", version);
    ct = 0;
    for (Long id : (List<Long>) query.getResultList()) {
      removeSubset(id, ConceptSubsetJpa.class);
      logAndCommit(++ct, RootService.logCt, RootService.commitCt);
    }
    commitClearBegin();

    // remove atom subset members
    Logger.getLogger(getClass()).info("  Remove atom subset members");
    query = manager.createQuery(
        "SELECT a.id FROM AtomSubsetMemberJpa a WHERE terminology = :terminology "
            + " AND version = :version");
    query.setParameter("terminology", terminology);
    query.setParameter("version", version);
    ct = 0;
    for (Long id : (List<Long>) query.getResultList()) {
      removeSubsetMember(id, AtomSubsetMemberJpa.class);
      logAndCommit(++ct, RootService.logCt, RootService.commitCt);
    }
    commitClearBegin();

    // remove atom subsets
    Logger.getLogger(getClass()).info("  Remove atom subsets");
    query = manager.createQuery(
        "SELECT a.id FROM AtomSubsetJpa a WHERE terminology = :terminology "
            + " AND version = :version");
    query.setParameter("terminology", terminology);
    query.setParameter("version", version);
    ct = 0;
    for (Long id : (List<Long>) query.getResultList()) {
      removeSubset(id, AtomSubsetJpa.class);
      logAndCommit(++ct, RootService.logCt, RootService.commitCt);
    }
    commitClearBegin();

    // remove concept relationships
    Logger.getLogger(getClass()).info("  Remove concept relationships");
    query = manager.createQuery(
        "SELECT a.id FROM ConceptRelationshipJpa a WHERE terminology = :terminology "
            + " AND version = :version");
    query.setParameter("terminology", terminology);
    query.setParameter("version", version);
    ct = 0;
    for (Long id : (List<Long>) query.getResultList()) {
      removeRelationship(id, ConceptRelationshipJpa.class);
      logAndCommit(++ct, RootService.logCt, RootService.commitCt);
    }
    commitClearBegin();

    // remove definitions from the concepts; definitions cannot be removed yet,
    // because they are used by atoms
    Logger.getLogger(getClass()).info("  Remove definitions");
    query = manager.createQuery(
        "SELECT a.id FROM ConceptJpa a WHERE terminology = :terminology "
            + " AND version = :version");
    query.setParameter("terminology", terminology);
    query.setParameter("version", version);
    ct = 0;
    for (Long id : (List<Long>) query.getResultList()) {
      Concept c = getConcept(id);
      c.setDefinitions(new ArrayList<Definition>());
      updateConcept(c);
      logAndCommit(++ct, RootService.logCt, RootService.commitCt);
    }
    commitClearBegin();

    // remove the concept transitive relationships
    Logger.getLogger(getClass())
        .info("  Remove concept transitive relationships");
    query = manager.createQuery(
        "SELECT a.id FROM ConceptTransitiveRelationshipJpa a WHERE terminology = :terminology "
            + " AND version = :version");
    query.setParameter("terminology", terminology);
    query.setParameter("version", version);
    ct = 0;
    for (Long id : (List<Long>) query.getResultList()) {
      removeTransitiveRelationship(id, ConceptTransitiveRelationshipJpa.class);
      logAndCommit(++ct, RootService.logCt, RootService.commitCt);
    }
    commitClearBegin();

    // remove the concept tree positions
    Logger.getLogger(getClass()).info("  Remove concept tree positions");
    query = manager.createQuery(
        "SELECT a.id FROM ConceptTreePositionJpa a WHERE terminology = :terminology "
            + " AND version = :version");
    query.setParameter("terminology", terminology);
    query.setParameter("version", version);
    ct = 0;
    for (Long id : (List<Long>) query.getResultList()) {
      removeTreePosition(id, ConceptTreePositionJpa.class);
      logAndCommit(++ct, RootService.logCt, RootService.commitCt);
    }
    commitClearBegin();

    // remove the concepts
    Logger.getLogger(getClass()).info("  Remove concepts");
    query = manager.createQuery(
        "SELECT a.id FROM ConceptJpa a WHERE terminology = :terminology "
            + " AND version = :version");
    query.setParameter("terminology", terminology);
    query.setParameter("version", version);
    ct = 0;
    for (Long id : (List<Long>) query.getResultList()) {
      removeConcept(id);
      logAndCommit(++ct, RootService.logCt, RootService.commitCt);
    }
    commitClearBegin();

    if (!standalone) {
      // go through all remaining concepts and remove atoms with matching
      // terminology and version
      // concepts may have UMLS terminology and matching atoms wouldn't
      // otherwise
      // be removed
      // not using this code bc of invalid field names
      /*
       * SearchResultList results = findConceptsForQuery(null, null,
       * Branch.ROOT, "atoms.terminology:" + terminology + " atoms.version:" +
       * version, new PfscParameterJpa()); for (SearchResult result :
       * results.getObjects()) { Concept concept = getConcept(result.getId());
       */
      query = manager.createQuery("SELECT a.id FROM ConceptJpa a");
      ct = 0;
      for (Long id : (List<Long>) query.getResultList()) {
        Concept concept = getConcept(id);
        List<Atom> keepAtoms = new ArrayList<Atom>();
        for (Atom atom : concept.getAtoms()) {
          if (!atom.getTerminology().equals(terminology)
              || !atom.getVersion().equals(version)) {
            keepAtoms.add(atom);
          }
        }
        concept.setAtoms(keepAtoms);
        updateConcept(concept);
        logAndCommit(++ct, RootService.logCt, RootService.commitCt);
      }
      commitClearBegin();
    }

    // remove definitions from the atoms
    query = manager.createQuery(
        "SELECT a.id FROM AtomJpa a WHERE terminology = :terminology "
            + " AND version = :version");
    query.setParameter("terminology", terminology);
    query.setParameter("version", version);
    ct = 0;
    for (Long id : (List<Long>) query.getResultList()) {
      Atom a = getAtom(id);
      a.setDefinitions(new ArrayList<Definition>());
      updateAtom(a);
      logAndCommit(++ct, RootService.logCt, RootService.commitCt);
    }
    commitClearBegin();

    // remove atom relationships
    Logger.getLogger(getClass()).info("  Remove atom relationships");
    query = manager.createQuery(
        "SELECT a.id FROM AtomRelationshipJpa a WHERE terminology = :terminology "
            + " AND version = :version");
    query.setParameter("terminology", terminology);
    query.setParameter("version", version);
    ct = 0;
    for (Long id : (List<Long>) query.getResultList()) {
      removeRelationship(id, AtomRelationshipJpa.class);
      logAndCommit(++ct, RootService.logCt, RootService.commitCt);
    }
    commitClearBegin();

    // remove descriptor relationships
    Logger.getLogger(getClass()).info("  Remove descriptor relationships");
    query = manager.createQuery(
        "SELECT a.id FROM DescriptorRelationshipJpa a WHERE terminology = :terminology "
            + " AND version = :version");
    query.setParameter("terminology", terminology);
    query.setParameter("version", version);
    ct = 0;
    for (Long id : (List<Long>) query.getResultList()) {
      removeRelationship(id, DescriptorRelationshipJpa.class);
      logAndCommit(++ct, RootService.logCt, RootService.commitCt);
    }
    commitClearBegin();

    // remove the descriptor transitive relationships
    Logger.getLogger(getClass())
        .info("  Remove descriptor transitive relationships");
    query = manager.createQuery(
        "SELECT a.id FROM DescriptorTransitiveRelationshipJpa a WHERE terminology = :terminology "
            + " AND version = :version");
    query.setParameter("terminology", terminology);
    query.setParameter("version", version);
    ct = 0;
    for (Long id : (List<Long>) query.getResultList()) {
      removeTransitiveRelationship(id,
          DescriptorTransitiveRelationshipJpa.class);
      logAndCommit(++ct, RootService.logCt, RootService.commitCt);
    }
    commitClearBegin();

    // remove the descriptor tree positions
    Logger.getLogger(getClass()).info("  Remove descriptor tree positions");
    query = manager.createQuery(
        "SELECT a.id FROM DescriptorTreePositionJpa a WHERE terminology = :terminology "
            + " AND version = :version");
    query.setParameter("terminology", terminology);
    query.setParameter("version", version);
    ct = 0;
    for (Long id : (List<Long>) query.getResultList()) {
      removeTreePosition(id, DescriptorTreePositionJpa.class);
      logAndCommit(++ct, RootService.logCt, RootService.commitCt);
    }
    commitClearBegin();

    // remove descriptors
    Logger.getLogger(getClass()).info("  Remove descriptors");
    query = manager.createQuery(
        "SELECT a.id FROM DescriptorJpa a WHERE terminology = :terminology "
            + " AND version = :version");
    query.setParameter("terminology", terminology);
    query.setParameter("version", version);
    ct = 0;
    for (Long id : (List<Long>) query.getResultList()) {
      removeDescriptor(id);
      logAndCommit(++ct, RootService.logCt, RootService.commitCt);
    }
    commitClearBegin();

    // remove code relationships
    Logger.getLogger(getClass()).info("  Remove code relationships");
    query = manager.createQuery(
        "SELECT a.id FROM CodeRelationshipJpa a WHERE terminology = :terminology "
            + " AND version = :version");
    query.setParameter("terminology", terminology);
    query.setParameter("version", version);
    ct = 0;
    for (Long id : (List<Long>) query.getResultList()) {
      removeRelationship(id, CodeRelationshipJpa.class);
      logAndCommit(++ct, RootService.logCt, RootService.commitCt);
    }
    commitClearBegin();

    // remove the code transitive relationships
    Logger.getLogger(getClass()).info("  Remove code transitive relationships");
    query = manager.createQuery(
        "SELECT a.id FROM CodeTransitiveRelationshipJpa a WHERE terminology = :terminology "
            + " AND version = :version");
    query.setParameter("terminology", terminology);
    query.setParameter("version", version);
    ct = 0;
    for (Long id : (List<Long>) query.getResultList()) {
      removeTransitiveRelationship(id, CodeTransitiveRelationshipJpa.class);
      logAndCommit(++ct, RootService.logCt, RootService.commitCt);
    }
    commitClearBegin();

    // remove the code tree positions
    Logger.getLogger(getClass()).info("  Remove code tree positions");
    query = manager.createQuery(
        "SELECT a.id FROM CodeTreePositionJpa a WHERE terminology = :terminology "
            + " AND version = :version");
    query.setParameter("terminology", terminology);
    query.setParameter("version", version);
    ct = 0;
    for (Long id : (List<Long>) query.getResultList()) {
      removeTreePosition(id, CodeTreePositionJpa.class);
      logAndCommit(++ct, RootService.logCt, RootService.commitCt);
    }
    commitClearBegin();

    // remove codes
    Logger.getLogger(getClass()).info("  Remove codes");
    query = manager.createQuery(
        "SELECT a.id FROM CodeJpa a WHERE terminology = :terminology "
            + " AND version = :version");
    query.setParameter("terminology", terminology);
    query.setParameter("version", version);
    ct = 0;
    for (Long id : (List<Long>) query.getResultList()) {
      removeCode(id);
      logAndCommit(++ct, RootService.logCt, RootService.commitCt);
    }
    commitClearBegin();

    // remove atoms - don't do this until after removing codes
    Logger.getLogger(getClass()).info("  Remove atoms");
    query = manager.createQuery(
        "SELECT a.id FROM AtomJpa a WHERE terminology = :terminology "
            + " AND version = :version");
    query.setParameter("terminology", terminology);
    query.setParameter("version", version);
    ct = 0;
    for (Long id : (List<Long>) query.getResultList()) {
      removeAtom(id);
      logAndCommit(++ct, RootService.logCt, RootService.commitCt);
    }
    commitClearBegin();

    // remove semantic type components, definitions and attributes last
    Logger.getLogger(getClass()).info("  Remove semantic type components");
    query = manager.createQuery(
        "SELECT a.id FROM SemanticTypeComponentJpa a WHERE terminology = :terminology "
            + " AND version = :version");
    query.setParameter("terminology", terminology);
    query.setParameter("version", version);
    ct = 0;
    for (Long id : (List<Long>) query.getResultList()) {
      removeSemanticTypeComponent(id);
      logAndCommit(++ct, RootService.logCt, RootService.commitCt);
    }
    commitClearBegin();

    // remove the definitions
    Logger.getLogger(getClass()).info("  Remove definitions ");
    query = manager.createQuery(
        "SELECT a.id FROM DefinitionJpa a WHERE terminology = :terminology "
            + " AND version = :version");
    query.setParameter("terminology", terminology);
    query.setParameter("version", version);
    ct = 0;
    for (Long id : (List<Long>) query.getResultList()) {
      removeDefinition(id);
      logAndCommit(++ct, RootService.logCt, RootService.commitCt);
    }
    commitClearBegin();

    // remove the attributes
    Logger.getLogger(getClass()).info("  Remove attributes");
    query = manager.createQuery(
        "SELECT a.id FROM AttributeJpa a WHERE terminology = :terminology "
            + " AND version = :version");
    query.setParameter("terminology", terminology);
    query.setParameter("version", version);
    ct = 0;
    for (Long id : (List<Long>) query.getResultList()) {
      removeAttribute(id);
      logAndCommit(++ct, RootService.logCt, RootService.commitCt);
    }
    commitClearBegin();

    commit();
    clear();

    Logger.getLogger(getClass())
        .info("Finished computing transitive closure ... " + new Date());
    // set the transaction strategy based on status starting this routine
    // setTransactionPerOperation(currentTransactionStrategy);
    fireProgressEvent(100, "Finished...");
  }

  /**
   * Fires a {@link ProgressEvent}.
   * @param pct percent done
   * @param note progress note
   */
  public void fireProgressEvent(int pct, String note) {
    ProgressEvent pe = new ProgressEvent(this, pct, pct, note);
    for (int i = 0; i < listeners.size(); i++) {
      listeners.get(i).updateProgress(pe);
    }
    Logger.getLogger(getClass()).info("    " + pct + "% " + note);
  }

  /* see superclass */
  @Override
  public void addProgressListener(ProgressListener l) {
    listeners.add(l);
  }

  /* see superclass */
  @Override
  public void removeProgressListener(ProgressListener l) {
    listeners.remove(l);
  }

  /* see superclass */
  @Override
  public void cancel() {
    requestCancel = true;
  }

}
