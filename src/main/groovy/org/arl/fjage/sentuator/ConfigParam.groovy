package org.arl.fjage.sentuator

import org.arl.fjage.param.Parameter

/**
 * Configuration parameter.
 */
@groovy.transform.CompileStatic
class ConfigParam implements Parameter, Serializable {

  private static final long serialVersionUID = 1L

  private String name

  /**
   * Constructs configuration parameter with specified name.
   */
  ConfigParam(String name) {
    this.name = name
  }

  /**
   * Gets the parameter name.
   */
  String name() {
    return name
  }

  @Override
  public int ordinal() {
    return -1
  }

  @Override
  String toString() {
    return name
  }

  @Override
  boolean equals(Object obj) {
    if (!(obj instanceof ConfigParam)) return false
    ConfigParam p = (ConfigParam)obj
    if (!name.equals(p.name)) return false
    return true
  }

  @Override
  int hashCode() {
    return name.hashCode()
  }

}
