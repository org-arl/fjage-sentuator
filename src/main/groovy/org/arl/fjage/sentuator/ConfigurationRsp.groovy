package org.arl.fjage.sentuator

import org.arl.fjage.*

/**
 * Response message containing a configuration parameter set.
 */
@groovy.transform.CompileStatic
class ConfigurationRsp extends Message {

  @groovy.transform.PackageScope
  Map<String,Object> cfg = new HashMap<>()

  ConfigurationRsp(ConfigurationReq req) {
    super(req, Performative.INFORM)
  }

  /**
   * Get the value of a configuration parameter.
   */
  Object get(String key) {
    return cfg.get(key)
  }

  /**
   * Get the list of keys in the configuration parameter set.
   */
  Set<String> keys() {
    return cfg.keySet()
  }

  @Override
  String toString() {
    String s = getClass().getSimpleName() + ':' + performative + cfg
    return s.replace(', ',' ')
  }

}
