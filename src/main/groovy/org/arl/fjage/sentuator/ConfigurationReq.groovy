package org.arl.fjage.sentuator

import org.arl.fjage.*
import org.arl.fjage.param.*

/**
 * Request for configuration.
 */
@groovy.transform.CompileStatic
class ConfigurationReq extends ParameterReq {

  ConfigurationReq() {
    super()
  }

  /**
   * Request to set the value of a configuration parameter.
   */
  ConfigurationReq set(String key, Object value) {
    super.set(new ConfigParam(key), value)
    return this
  }

  /**
   * Request the value of a configuration parameter.
   */
  ConfigurationReq get(String key) {
    super.get(new ConfigParam(key))
    return this
  }

}
