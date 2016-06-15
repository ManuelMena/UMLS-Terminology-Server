/*
 *    Copyright 2016 West Coast Informatics, LLC
 */
package com.wci.umls.server.jpa.worfklow;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.FieldBridge;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.Store;
import org.hibernate.search.bridge.builtin.BooleanBridge;
import org.hibernate.search.bridge.builtin.EnumBridge;
import org.hibernate.search.bridge.builtin.LongBridge;

import com.wci.umls.server.Project;
import com.wci.umls.server.jpa.ProjectJpa;
import com.wci.umls.server.model.workflow.WorkflowBinDefinition;
import com.wci.umls.server.model.workflow.WorkflowBinType;
import com.wci.umls.server.model.workflow.WorkflowConfig;

/**
 * JPA and JAXB enabled implementation of a {@link WorkflowConfig}.
 */
@Entity
@Table(name = "workflow_configs")
@Indexed
@XmlRootElement(name = "workflowConfig")
public class WorkflowConfigJpa implements WorkflowConfig {

  /** The id. */
  @TableGenerator(name = "EntityIdGenWorkflow", table = "table_generator_wf", pkColumnValue = "Entity")
  @Id
  @GeneratedValue(strategy = GenerationType.TABLE, generator = "EntityIdGenWorkflow")
  private Long id;

  /** The last modified. */
  @Column(nullable = false)
  @Temporal(TemporalType.TIMESTAMP)
  private Date lastModified = new Date();

  /** The last modified. */
  @Column(nullable = false)
  private String lastModifiedBy;

  /** The timestamp. */
  @Column(nullable = false)
  @Temporal(TemporalType.TIMESTAMP)
  private Date timestamp = null;

  /** The type. */
  @Column(nullable = false)
  private WorkflowBinType type;

  /** The mutually exclusive. */
  @Column(nullable = false)
  private boolean mutuallyExclusive;

  /** The last partition time. */
  /** The last partition time. */
  @Column(nullable = false, unique = false)
  private Long lastPartitionTime;

  /** The workflow bin definitions. */
  @OneToMany(mappedBy = "workflowConfig", targetEntity = WorkflowBinDefinitionJpa.class)
  private List<WorkflowBinDefinition> workflowBinDefinitions =
      new ArrayList<>();

  /** The project. */
  @OneToOne(targetEntity = ProjectJpa.class, optional = false)
  private Project project;

  /**
   * Instantiates a new workflow bin definitions jpa.
   */
  public WorkflowConfigJpa() {
    // do nothing
  }

  /**
   * Instantiates a new project workflow config jpa.
   *
   * @param config the project workflow configuration
   */
  public WorkflowConfigJpa(WorkflowConfig config) {
    super();
    id = config.getId();
    lastModified = config.getLastModified();
    lastModifiedBy = config.getLastModifiedBy();
    lastPartitionTime = config.getLastPartitionTime();
    project = config.getProject();
    timestamp = config.getTimestamp();
    mutuallyExclusive = config.isMutuallyExclusive();
    type = config.getType();
    workflowBinDefinitions =
        new ArrayList<>(config.getWorkflowBinDefinitions());
  }

  /* see superclass */
  @Override
  public Date getTimestamp() {
    return timestamp;
  }

  /* see superclass */
  @Override
  public void setTimestamp(Date timestamp) {
    this.timestamp = timestamp;
  }

  /* see superclass */
  @Override
  public Date getLastModified() {
    return lastModified;
  }

  /* see superclass */
  @Override
  public void setLastModified(Date lastModified) {
    this.lastModified = lastModified;
  }

  /* see superclass */
  @Override
  @Field(index = Index.YES, analyze = Analyze.NO, store = Store.NO)
  public String getLastModifiedBy() {
    return lastModifiedBy;
  }

  /* see superclass */
  @Override
  public void setLastModifiedBy(String lastModifiedBy) {
    this.lastModifiedBy = lastModifiedBy;
  }

  /* see superclass */
  @Override
  public Long getId() {
    return id;
  }

  /* see superclass */
  @Override
  public void setId(Long id) {
    this.id = id;
  }

  /* see superclass */
  @Override
  @XmlElement(type = WorkflowBinDefinitionJpa.class)
  public List<WorkflowBinDefinition> getWorkflowBinDefinitions() {
    if (workflowBinDefinitions == null) {
      workflowBinDefinitions = new ArrayList<>();
    }
    return workflowBinDefinitions;
  }

  /* see superclass */
  @Override
  public void setWorkflowBinDefinitions(List<WorkflowBinDefinition> definitions) {
    this.workflowBinDefinitions = definitions;
  }

  /* see superclass */
  @Override
  @FieldBridge(impl = EnumBridge.class)
  @Field(index = Index.YES, analyze = Analyze.NO, store = Store.NO)
  public WorkflowBinType getType() {
    return type;
  }

  /* see superclass */
  @Override
  public void setType(WorkflowBinType type) {
    this.type = type;
  }

  /* see superclass */
  @FieldBridge(impl = BooleanBridge.class)
  @Field(index = Index.YES, analyze = Analyze.NO, store = Store.NO)
  @Override
  public boolean isMutuallyExclusive() {
    return mutuallyExclusive;
  }

  /* see superclass */
  @Override
  public void setMutuallyExclusive(boolean mutuallyExclusive) {
    this.mutuallyExclusive = mutuallyExclusive;
  }

  /* see superclass */
  @Override
  public Long getLastPartitionTime() {
    return lastPartitionTime;
  }

  /* see superclass */
  @Override
  public void setLastPartitionTime(Long lastPartitionTime) {
    this.lastPartitionTime = lastPartitionTime;
  }

  /* see superclass */
  @Override
  @XmlTransient
  public Project getProject() {
    return project;
  }

  /* see superclass */
  @Override
  public void setProject(Project project) {
    this.project = project;
  }

  /**
   * Returns the project id.
   *
   * @return the project id
   */
  @FieldBridge(impl = LongBridge.class)
  @Field(index = Index.YES, analyze = Analyze.NO, store = Store.NO)
  public Long getProjectId() {
    return project == null ? 0L : project.getId();
  }

  /**
   * Sets the project id.
   *
   * @param projectId the project id
   */
  public void setProjectId(Long projectId) {
    if (project == null) {
      project = new ProjectJpa();
    }
    project.setId(projectId);
  }

  /* see superclass */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + (mutuallyExclusive ? 1231 : 1237);
    result = prime * result + ((type == null) ? 0 : type.hashCode());
    result =
        prime * result + ((project == null) ? 0 : getProjectId().hashCode());
    result =
        prime
            * result
            + ((workflowBinDefinitions == null) ? 0 : workflowBinDefinitions
                .hashCode());
    return result;
  }

  /* see superclass */
  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    WorkflowConfigJpa other = (WorkflowConfigJpa) obj;
    if (mutuallyExclusive != other.mutuallyExclusive)
      return false;
    if (project == null) {
      if (other.project != null)
        return false;
    } else if (!project.equals(other.project))
      return false;
    if (type != other.type)
      return false;
    if (workflowBinDefinitions == null) {
      if (other.workflowBinDefinitions != null)
        return false;
    } else if (!workflowBinDefinitions.equals(other.workflowBinDefinitions))
      return false;
    return true;
  }

  /* see superclass */
  @Override
  public String toString() {
    return "ProjectWorkflowConfigJpa [id=" + id + ", lastModified="
        + lastModified + ", lastModifiedBy=" + lastModifiedBy + ", timestamp="
        + timestamp + ", type=" + type + ", mutuallyExclusive="
        + mutuallyExclusive + ", lastPartitionTime=" + lastPartitionTime
        + ", workflowBinDefinitions=" + workflowBinDefinitions
        + ", getProjectId()=" + getProjectId() + "]";
  }

}