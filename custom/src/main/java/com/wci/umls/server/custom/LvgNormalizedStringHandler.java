/*
 *    Copyright 2015 West Coast Informatics, LLC
 */
package com.wci.umls.server.custom;

import java.util.Properties;

import com.wci.umls.server.services.handlers.NormalizedStringHandler;

/**
 * Implementation of string normalization based on NLM's LVG.
 * Requires a local LVG installation and the "lvg.dir" property to
 * be defined.  
 * 
 * Presently, this also requires the lvg2014dist.jar to be locally installed
 * in an src/main/resources directory (which means it may not run properly 
 * on the server). What we *really* want is a maven reference to this
 * artifact.
 */
public class LvgNormalizedStringHandler implements NormalizedStringHandler {

  /** The lvg dir. */
  @SuppressWarnings("unused")
  private String LVG_DIR;

  /**  The api. */
  //private LuiNormApi api;

  /*
   * (non-Javadoc)
   * 
   * @see
   * com.wci.umls.server.helpers.Configurable#setProperties(java.util.Properties
   * )
   */
  @Override
  public void setProperties(Properties p) throws Exception {
    if (p.getProperty("lvg.dir") == null) {
      throw new Exception("Required property lvg.dir is missing");
    }
    LVG_DIR = p.getProperty("lvg.dir");
    //Hashtable<String, String> properties = new Hashtable<String, String>();
    //properties.put(gov.nih.nlm.nls.lvg.Lib.Configuration.LVG_DIR, LVG_DIR + "/");
    // Use default config
    //api = new LuiNormApi(LVG_DIR + "/data/config/lvg.properties",properties);
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.wci.umls.server.services.handlers.NormalizedStringHandler#
   * getNormalizedString(java.lang.String)
   */
  @Override
  public String getNormalizedString(String string) throws Exception {
    return null; //api.Mutate(string);
  }

}
