/**
 * Copyright 2016 West Coast Informatics, LLC
 */
package com.wci.umls.server.jpa.helpers;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.wci.umls.server.User;
import com.wci.umls.server.helpers.AbstractResultList;
import com.wci.umls.server.helpers.UserList;
import com.wci.umls.server.jpa.UserJpa;

/**
 * JAXB enabled implementation of {@link UserList}.
 */
@XmlRootElement(name = "userList")
public class UserListJpa extends AbstractResultList<User> implements UserList {

  /* see superclass */
  @Override
  @XmlElement(type = UserJpa.class, name = "users")
  public List<User> getObjects() {
    return super.getObjectsTransient();
  }

}
