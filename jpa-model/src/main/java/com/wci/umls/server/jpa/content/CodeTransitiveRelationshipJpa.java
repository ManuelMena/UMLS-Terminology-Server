/**
 * Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.umls.server.jpa.content;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.hibernate.envers.Audited;

import com.wci.umls.server.model.content.Code;
import com.wci.umls.server.model.content.CodeTransitiveRelationship;

/**
 * JPA-enabled implementation of {@link CodeTransitiveRelationship}.
 */
@Entity
@Table(name = "code_transitive_rels", uniqueConstraints = @UniqueConstraint(columnNames = {
    "terminologyId", "terminology", "terminologyVersion", "id"
}))
@Audited
@XmlRootElement(name = "codeTransitiveRel")
public class CodeTransitiveRelationshipJpa extends
    AbstractTransitiveRelationship<Code> implements CodeTransitiveRelationship {

  /** The super type. */
  @ManyToOne(targetEntity = CodeJpa.class, optional = false)
  @JoinColumn(nullable = false)
  private Code superType;

  /** The sub type. */
  @ManyToOne(targetEntity = CodeJpa.class, optional = false)
  @JoinColumn(nullable = false)
  private Code subType;

  /**
   * Instantiates an empty {@link CodeTransitiveRelationshipJpa}.
   */
  public CodeTransitiveRelationshipJpa() {
    // do nothing
  }

  /**
   * Instantiates a {@link CodeTransitiveRelationshipJpa} from the specified
   * parameters.
   *
   * @param relationship the relationship
   * @param deepCopy the deep copy
   */
  public CodeTransitiveRelationshipJpa(CodeTransitiveRelationship relationship,
      boolean deepCopy) {
    super(relationship, deepCopy);
    superType = relationship.getSuperType();
    subType = relationship.getSubType();
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * com.wci.umls.server.model.content.TransitiveRelationship#getSuperType()
   */
  @XmlTransient
  @Override
  public Code getSuperType() {
    return superType;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * com.wci.umls.server.model.content.TransitiveRelationship#setSuperType(com
   * .wci.umls.server.model.content.AtomClass)
   */
  @Override
  public void setSuperType(Code ancestor) {
    this.superType = ancestor;
  }

  /**
   * Returns the super type id. For JAXB.
   *
   * @return the super type id
   */
  public Long getSuperTypeId() {
    return superType == null ? null : superType.getId();
  }

  /**
   * Sets the super type id.
   *
   * @param id the super type id
   */
  public void setSuperTypeId(Long id) {
    if (superType == null) {
      superType = new CodeJpa();
    }
    superType.setId(id);
  }

  /**
   * Returns the super type terminology id. For JAXB.
   *
   * @return the super type terminology id
   */
  public String getSuperTypeTerminologyId() {
    return superType == null ? "" : superType.getTerminologyId();
  }

  /**
   * Sets the super type terminology id.
   *
   * @param id the super type terminology id
   */
  /**
   * @param terminologyId
   */
  public void setSuperTypeTerminologyId(String terminologyId) {
    if (superType == null) {
      superType = new CodeJpa();
    }
    superType.setTerminologyId(terminologyId);
  }

  /**
   * Returns the super type term. For JAXB.
   *
   * @return the super type term
   */
  public String getSuperTypeName() {
    return superType == null ? null : superType.getName();
  }

  /**
   * Sets the super type term.
   *
   * @param term the super type term
   */
  public void setSuperTypeName(String term) {
    if (superType == null) {
      superType = new CodeJpa();
    }
    superType.setName(term);
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.wci.umls.server.model.content.TransitiveRelationship#getSubType()
   */
  @XmlTransient
  @Override
  public Code getSubType() {
    return subType;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * com.wci.umls.server.model.content.TransitiveRelationship#setSubType(com
   * .wci.umls.server.model.content.AtomClass)
   */
  @Override
  public void setSubType(Code descendant) {
    this.subType = descendant;
  }

  /**
   * CUSTOM hashcode method for sub/superType.getTerminologyId
   *
   * @return true, if successful
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result =
        prime
            * result
            + ((superType == null || superType.getTerminologyId() == null) ? 0
                : superType.getTerminologyId().hashCode());
    result =
        prime
            * result
            + ((subType == null || subType.getTerminologyId() == null) ? 0
                : subType.getTerminologyId().hashCode());
    return result;
  }

  /**
   * Returns the sub type id. For JAXB.
   *
   * @return the sub type id
   */
  public Long getSubTypeId() {
    return subType == null ? null : subType.getId();
  }

  /**
   * Sets the sub type id.
   *
   * @param id the sub type id
   */
  public void setSubTypeId(Long id) {
    if (subType == null) {
      subType = new CodeJpa();
    }
    subType.setId(id);
  }

  /**
   * Returns the sub type terminology id. For JAXB.
   *
   * @return the sub type terminology id
   */
  public String getSubTypeTerminologyId() {
    return subType == null ? "" : subType.getTerminologyId();
  }

  /**
   * Sets the sub type terminology id.
   *
   * @param terminologyId the sub type terminology id
   */
  public void setSubTypeTerminologyId(String terminologyId) {
    if (subType == null) {
      subType = new CodeJpa();
    }
    subType.setTerminologyId(terminologyId);
  }

  /**
   * Returns the sub type term. For JAXB.
   *
   * @return the sub type term
   */
  public String getSubTypeName() {
    return subType == null ? null : subType.getName();
  }

  /**
   * Sets the sub type term.
   *
   * @param term the sub type term
   */
  public void setSubTypeName(String term) {
    if (subType == null) {
      subType = new CodeJpa();
    }
    subType.setName(term);
  }

  /**
   * CUSTOM equals method for to/from.getTerminologyId
   *
   * @param obj the obj
   * @return true, if successful
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (!super.equals(obj))
      return false;
    if (getClass() != obj.getClass())
      return false;
    CodeTransitiveRelationshipJpa other = (CodeTransitiveRelationshipJpa) obj;
    if (subType == null) {
      if (other.subType != null)
        return false;
    } else if (subType.getTerminologyId() == null) {
      if (other.subType != null && other.subType.getTerminologyId() != null)
        return false;
    } else if (!subType.getTerminologyId().equals(
        other.subType.getTerminologyId()))
      return false;
    if (superType == null) {
      if (other.superType != null)
        return false;
    } else if (superType.getTerminologyId() == null) {
      if (other.superType != null && other.superType.getTerminologyId() != null)
        return false;
    } else if (!superType.getTerminologyId().equals(
        other.superType.getTerminologyId()))
      return false;
    return true;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.wci.umls.server.jpa.content.AbstractComponent#toString()
   */
  @Override
  public String toString() {
    return "CodeTransitiveRelationshipJpa [superType=" + superType.getTerminologyId()
        + ", subType=" + subType.getTerminologyId() + "]";
  }

}
