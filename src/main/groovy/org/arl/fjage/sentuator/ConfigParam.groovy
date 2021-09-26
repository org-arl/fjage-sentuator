package org.arl.fjage.sentuator

import org.arl.fjage.param.NamedParameter

/**
 * Configuration parameter.
 */
@groovy.transform.CompileStatic
class ConfigParam extends NamedParameter {

  private static final long serialVersionUID = 1L

  /**
   * Constructs configuration parameter with specified name.
   */
  ConfigParam(String name) {
    super(name)
  }

}
