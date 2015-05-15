/**
 * Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.umls.server.jpa.content;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.envers.Audited;
import org.hibernate.search.annotations.Indexed;

import com.wci.umls.server.model.content.Definition;
import com.wci.umls.server.model.content.Descriptor;
import com.wci.umls.server.model.content.DescriptorRelationship;

/**
 * JPA-enabled implementation of {@link Descriptor}.
 */
@Entity
@Table(name = "descriptors", uniqueConstraints = @UniqueConstraint(columnNames = {
    "terminologyId", "terminology", "terminologyVersion", "id"
}))
@Audited
@Indexed
@XmlRootElement(name = "descriptor")
public class DescriptorJpa extends AbstractAtomClass implements Descriptor {

  /** The definitions. */
  @OneToMany(orphanRemoval = true, targetEntity = DefinitionJpa.class)
  private List<Definition> definitions = new ArrayList<>(1);

  /** The relationships. */
  @OneToMany(mappedBy = "from", orphanRemoval = true, targetEntity = DescriptorRelationshipJpa.class)
  private List<DescriptorRelationship> relationships = new ArrayList<>(1);

  /**
   * Instantiates an empty {@link DescriptorJpa}.
   */
  public DescriptorJpa() {
    // do nothing
  }

  /**
   * Instantiates a {@link DescriptorJpa} from the specified parameters.
   *
   * @param descriptor the Descriptor
   * @param deepCopy the deep copy
   */
  public DescriptorJpa(Descriptor descriptor, boolean deepCopy) {
    super(descriptor, deepCopy);

    if (deepCopy) {
      for (Definition definition : descriptor.getDefinitions()) {
        addDefinition(new DefinitionJpa(definition, deepCopy));
      }
      for (DescriptorRelationship relationship : descriptor.getRelationships()) {
        addRelationship(new DescriptorRelationshipJpa(relationship, deepCopy));
      }
    }
  }

  /**
   * Returns the definitions.
   *
   * @return the definitions
   */
  @XmlElement(type = DefinitionJpa.class, name = "definition")
  @Override
  public List<Definition> getDefinitions() {
    if (definitions == null) {
      definitions = new ArrayList<>(1);
    }
    return definitions;
  }

  /**
   * Sets the definitions.
   *
   * @param definitions the definitions
   */
  @Override
  public void setDefinitions(List<Definition> definitions) {
    this.definitions = definitions;
  }

  /**
   * Adds the definition.
   *
   * @param definition the definition
   */
  @Override
  public void addDefinition(Definition definition) {
    if (definitions == null) {
      definitions = new ArrayList<>(1);
    }
    definitions.add(definition);

  }

  /**
   * Removes the definition.
   *
   * @param definition the definition
   */
  @Override
  public void removeDefinition(Definition definition) {
    if (definitions == null) {
      definitions = new ArrayList<>(1);
    }
    definitions.remove(definition);

  }

  /**
   * Returns the relationships.
   *
   * @return the relationships
   */
  @XmlElement(type = DescriptorRelationshipJpa.class, name = "relationship")
  @Override
  public List<DescriptorRelationship> getRelationships() {
    if (relationships == null) {
      relationships = new ArrayList<>(1);
    }
    return relationships;
  }

  /**
   * Sets the relationships.
   *
   * @param relationships the relationships
   */
  @Override
  public void setRelationships(List<DescriptorRelationship> relationships) {
    this.relationships = relationships;

  }

  /**
   * Adds the relationship.
   *
   * @param relationship the relationship
   */
  @Override
  public void addRelationship(DescriptorRelationship relationship) {
    if (relationships == null) {
      relationships = new ArrayList<>(1);
    }
    relationships.add(relationship);
  }

  /**
   * Removes the relationship.
   *
   * @param relationship the relationship
   */
  @Override
  public void removeRelationship(DescriptorRelationship relationship) {
    if (relationships == null) {
      relationships = new ArrayList<>(1);
    }
    relationships.remove(relationship);
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.wci.umls.server.jpa.content.AbstractAtomClass#toString()
   */
  @Override
  public String toString() {
    return "DescriptorJpa [isObsolete()=" + isObsolete()
        + ", getTerminologyVersion()=" + getTerminologyVersion()
        + ", getTerminology()=" + getTerminology() + ", getTerminologyId()="
        + getTerminologyId() + "]";
  }

}
