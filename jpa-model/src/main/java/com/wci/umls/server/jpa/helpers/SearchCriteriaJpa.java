/**
 * Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.umls.server.jpa.helpers;

import javax.xml.bind.annotation.XmlRootElement;

import com.wci.umls.server.helpers.SearchCriteria;

/**
 * JAXB enabled implementation of a {@link SearchCriteria}.
 */
@XmlRootElement(name = "searchCriteria")
public class SearchCriteriaJpa implements SearchCriteria {

  /** The active only. */
  private boolean activeOnly;

  /** The destination id. */
  private String relationshipToId;

  /** The relationship descendants. */
  private boolean relationshipDescendantsFlag;

  /** The relationship type id. */
  private String relationshipType;

  /** The source id. */
  private String relationshipFromId;

  /** The descendants. */
  private boolean findDescendants;

  /** The defined only. */
  private boolean definedOnly;

  /** The inactive only. */
  private boolean inactiveOnly;

  /** The primitive only. */
  private boolean primitiveOnly;

  /** The self. */
  private boolean findSelf;

  /**
   * Instantiates an empty {@link SearchCriteriaJpa}.
   */
  public SearchCriteriaJpa() {
    // do nothing
  }

  /**
   * Instantiates a {@link SearchCriteriaJpa} from the specified parameters.
   *
   * @param searchCriteria the search criteria
   */
  public SearchCriteriaJpa(SearchCriteria searchCriteria) {
    activeOnly = searchCriteria.getActiveOnly();
    relationshipToId = searchCriteria.getRelationshipToId();
    relationshipDescendantsFlag =
        searchCriteria.getRelationshipDescendantsFlag();
    relationshipType = searchCriteria.getRelationshipType();
    relationshipFromId = searchCriteria.getRelationshipFromId();
    definedOnly = searchCriteria.getDefinedOnly();
    findDescendants = searchCriteria.getFindDescendants();
    inactiveOnly = searchCriteria.getInactiveOnly();
    primitiveOnly = searchCriteria.getPrimitiveOnly();
    findSelf = searchCriteria.getFindSelf();
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.wci.umls.server.helpers.SearchCriteria#getActiveOnly()
   */
  @Override
  public boolean getActiveOnly() {
    return activeOnly;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.wci.umls.server.helpers.SearchCriteria#setActiveOnly(boolean)
   */
  @Override
  public void setActiveOnly(boolean activeOnly) {
    this.activeOnly = activeOnly;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.wci.umls.server.helpers.SearchCriteria#getInactiveOnly()
   */
  @Override
  public boolean getInactiveOnly() {
    return inactiveOnly;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.wci.umls.server.helpers.SearchCriteria#setInactiveOnly(boolean)
   */
  @Override
  public void setInactiveOnly(boolean inactiveOnly) {
    this.inactiveOnly = inactiveOnly;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.wci.umls.server.helpers.SearchCriteria#getFindDescendants()
   */
  @Override
  public boolean getFindDescendants() {
    return findDescendants;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.wci.umls.server.helpers.SearchCriteria#setFindDescendants(boolean)
   */
  @Override
  public void setFindDescendants(boolean descendants) {
    this.findDescendants = descendants;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.wci.umls.server.helpers.SearchCriteria#getFindSelf()
   */
  @Override
  public boolean getFindSelf() {
    return findSelf;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.wci.umls.server.helpers.SearchCriteria#setFindSelf(boolean)
   */
  @Override
  public void setFindSelf(boolean self) {
    this.findSelf = self;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.wci.umls.server.helpers.SearchCriteria#getPrimitiveOnly()
   */
  @Override
  public boolean getPrimitiveOnly() {
    return primitiveOnly;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.wci.umls.server.helpers.SearchCriteria#setPrimitiveOnly(boolean)
   */
  @Override
  public void setPrimitiveOnly(boolean primitiveOnly) {
    this.primitiveOnly = primitiveOnly;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.wci.umls.server.helpers.SearchCriteria#getDefinedOnly()
   */
  @Override
  public boolean getDefinedOnly() {
    return definedOnly;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.wci.umls.server.helpers.SearchCriteria#setDefinedOnly(boolean)
   */
  @Override
  public void setDefinedOnly(boolean definedOnly) {
    this.definedOnly = definedOnly;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.wci.umls.server.helpers.SearchCriteria#getRelationshipFromId()
   */
  @Override
  public String getRelationshipFromId() {
    return relationshipFromId;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.wci.umls.server.helpers.SearchCriteria#getRelationshipType()
   */
  @Override
  public String getRelationshipType() {
    return relationshipType;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.wci.umls.server.helpers.SearchCriteria#getRelationshipToId()
   */
  @Override
  public String getRelationshipToId() {
    return relationshipToId;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * com.wci.umls.server.helpers.SearchCriteria#getRelationshipDescendantsFlag()
   */
  @Override
  public boolean getRelationshipDescendantsFlag() {
    return relationshipDescendantsFlag;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * com.wci.umls.server.helpers.SearchCriteria#setFindFromByRelationshipTypeAndTo
   * (java.lang.String, java.lang.String, boolean)
   */
  @Override
  public void setFindFromByRelationshipTypeAndTo(String type,
    String destinationId, boolean descendants) {
    this.relationshipType = type;
    this.relationshipToId = destinationId;
    this.relationshipDescendantsFlag = descendants;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * com.wci.umls.server.helpers.SearchCriteria#setFindToByRelationshipFromAndType
   * (java.lang.String, java.lang.String, boolean)
   */
  @Override
  public void setFindToByRelationshipFromAndType(String type, String sourceId,
    boolean descendants) {
    this.relationshipType = type;
    this.relationshipFromId = sourceId;
    this.relationshipDescendantsFlag = descendants;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + (activeOnly ? 1231 : 1237);
    result = prime * result + (definedOnly ? 1231 : 1237);
    result = prime * result + (findDescendants ? 1231 : 1237);
    result = prime * result + (findSelf ? 1231 : 1237);
    result = prime * result + (inactiveOnly ? 1231 : 1237);
    result = prime * result + (primitiveOnly ? 1231 : 1237);
    result = prime * result + (relationshipDescendantsFlag ? 1231 : 1237);
    result =
        prime
            * result
            + ((relationshipFromId == null) ? 0 : relationshipFromId.hashCode());
    result =
        prime * result
            + ((relationshipToId == null) ? 0 : relationshipToId.hashCode());
    result =
        prime * result
            + ((relationshipType == null) ? 0 : relationshipType.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    SearchCriteriaJpa other = (SearchCriteriaJpa) obj;
    if (activeOnly != other.activeOnly)
      return false;
    if (definedOnly != other.definedOnly)
      return false;
    if (findDescendants != other.findDescendants)
      return false;
    if (findSelf != other.findSelf)
      return false;
    if (inactiveOnly != other.inactiveOnly)
      return false;
    if (primitiveOnly != other.primitiveOnly)
      return false;
    if (relationshipDescendantsFlag != other.relationshipDescendantsFlag)
      return false;
    if (relationshipFromId == null) {
      if (other.relationshipFromId != null)
        return false;
    } else if (!relationshipFromId.equals(other.relationshipFromId))
      return false;
    if (relationshipToId == null) {
      if (other.relationshipToId != null)
        return false;
    } else if (!relationshipToId.equals(other.relationshipToId))
      return false;
    if (relationshipType == null) {
      if (other.relationshipType != null)
        return false;
    } else if (!relationshipType.equals(other.relationshipType))
      return false;
    return true;
  }

  /**
   * Sets the find by destination id.
   *
   * @param findByDestinationId the find by destination id
   */
  public void setFindByDestinationId(String findByDestinationId) {
    this.relationshipToId = findByDestinationId;
  }

  /**
   * Sets the find by relationship descendants.
   *
   * @param findByRelationshipDescendants the find by relationship descendants
   */
  public void setFindByRelationshipDescendants(
    boolean findByRelationshipDescendants) {
    this.relationshipDescendantsFlag = findByRelationshipDescendants;
  }

  /**
   * Sets the find by relationship type id.
   *
   * @param findByRelationshipTypeId the find by relationship type id
   */
  public void setFindByRelationshipTypeId(String findByRelationshipTypeId) {
    this.relationshipType = findByRelationshipTypeId;
  }

  /**
   * Sets the find by source id.
   *
   * @param findBySourceId the find by source id
   */
  public void setFindBySourceId(String findBySourceId) {
    this.relationshipFromId = findBySourceId;
  }

  @Override
  public String toString() {
    return "SearchCriteriaJpa [activeOnly=" + activeOnly
        + ", relationshipToId=" + relationshipToId
        + ", relationshipDescendantsFlag=" + relationshipDescendantsFlag
        + ", relationshipType=" + relationshipType + ", relationshipFromId="
        + relationshipFromId + ", findDescendants=" + findDescendants
        + ", definedOnly=" + definedOnly + ", inactiveOnly=" + inactiveOnly
        + ", primitiveOnly=" + primitiveOnly + ", findSelf=" + findSelf + "]";
  }

}
