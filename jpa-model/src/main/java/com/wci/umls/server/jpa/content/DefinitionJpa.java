/**
 * Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.umls.server.jpa.content;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.envers.Audited;
import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Store;

import com.wci.umls.server.model.content.Definition;

/**
 * JPA-enabled implementation of {@link Definition}.
 */
@Entity
@Table(name = "definitions", uniqueConstraints = @UniqueConstraint(columnNames = {
    "terminologyId", "terminology", "version", "id"
}))
@Audited
@XmlRootElement(name = "definition")
public class DefinitionJpa extends AbstractComponentHasAttributes implements
    Definition {

  /** The value. */
  @Column(nullable = false, length = 4000)
  private String value;

  /** The alternate terminology ids. */
  @ElementCollection(fetch = FetchType.EAGER)
  @Column(nullable = true)
  private Map<String, String> alternateTerminologyIds;

  /**
   * Instantiates an empty {@link DefinitionJpa}.
   */
  public DefinitionJpa() {
    // do nothing
  }

  /**
   * Instantiates a {@link DefinitionJpa} from the specified parameters.
   *
   * @param definition the definition
   * @param deepCopy the deep copy
   */
  public DefinitionJpa(Definition definition, boolean deepCopy) {
    super(definition, deepCopy);
    value = definition.getValue();
    alternateTerminologyIds = definition.getAlternateTerminologyIds();
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.wci.umls.server.model.content.Definition#getValue()
   */
  @Override
  @Field(index = Index.YES, analyze = Analyze.YES, store = Store.NO)
  public String getValue() {
    return value;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * com.wci.umls.server.model.content.Definition#setValue(java.lang.String)
   */
  @Override
  public void setValue(String value) {
    this.value = value;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.wci.umls.server.helpers.HasAlternateTerminologyIds#
   * getAlternateTerminologyIds()
   */
  @Override
  public Map<String, String> getAlternateTerminologyIds() {
    if (alternateTerminologyIds == null) {
      alternateTerminologyIds = new HashMap<>();
    }
    return alternateTerminologyIds;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.wci.umls.server.helpers.HasAlternateTerminologyIds#
   * setAlternateTerminologyIds(java.util.Map)
   */
  @Override
  public void setAlternateTerminologyIds(
    Map<String, String> alternateTerminologyIds) {
    this.alternateTerminologyIds = alternateTerminologyIds;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.wci.umls.server.helpers.HasAlternateTerminologyIds#
   * putAlternateTerminologyId(java.lang.String, java.lang.String)
   */
  @Override
  public void putAlternateTerminologyId(String terminology, String terminologyId) {
    if (alternateTerminologyIds == null) {
      alternateTerminologyIds = new HashMap<>();
    }
    alternateTerminologyIds.put(terminology, terminologyId);
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.wci.umls.server.helpers.HasAlternateTerminologyIds#
   * removeAlternateTerminologyId(java.lang.String)
   */
  @Override
  public void removeAlternateTerminologyId(String terminology) {
    if (alternateTerminologyIds == null) {
      alternateTerminologyIds = new HashMap<>();
    }
    alternateTerminologyIds.remove(terminology);

  }

  /*
   * (non-Javadoc)
   * 
   * @see com.wci.umls.server.jpa.content.AbstractComponent#hashCode()
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((value == null) ? 0 : value.hashCode());
    result =
        prime
            * result
            + ((alternateTerminologyIds == null) ? 0 : alternateTerminologyIds
                .toString().hashCode());
    return result;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * com.wci.umls.server.jpa.content.AbstractComponent#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (!super.equals(obj))
      return false;
    if (getClass() != obj.getClass())
      return false;
    DefinitionJpa other = (DefinitionJpa) obj;
    if (value == null) {
      if (other.value != null)
        return false;
    } else if (!value.equals(other.value))
      return false;
    if (alternateTerminologyIds == null) {
      if (other.alternateTerminologyIds != null)
        return false;
    } else if (!alternateTerminologyIds.equals(other.alternateTerminologyIds))
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
    return "DefinitionJpa [value=" + value + ", alternateTerminologyIds="
        + alternateTerminologyIds + "]";
  }

}
