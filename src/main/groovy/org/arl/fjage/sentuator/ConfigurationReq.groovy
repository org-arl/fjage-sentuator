package org.arl.fjage.sentuator

import org.arl.fjage.*

/**
 * Request for configuration.
 */
@groovy.transform.CompileStatic
class ConfigurationReq extends Message {

  Map<String,Object> settings = new HashMap<>()
  List<String> queries = new ArrayList<>()

  ConfigurationReq() {
    super(Performative.REQUEST)
  }

  /**
   * Request to set the value of a configuration parameter.
   */
  ConfigurationReq set(String key, Object value) {
    settings.put(key, value)
    return this
  }

  /**
   * Request the value of a configuration parameter.
   */
  ConfigurationReq get(String key) {
    queries.add(key)
    return this
  }

  @Override
  String toString() {
    String s = getClass().getSimpleName() + ':' + performative + settings + queries
    return s.replace(', ',' ')
  }

}
