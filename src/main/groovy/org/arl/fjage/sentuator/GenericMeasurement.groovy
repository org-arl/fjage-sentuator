package org.arl.fjage.sentuator

import groovy.transform.CompileStatic

/**
 * Generic key-value measurement made by a sensor.
 */
@CompileStatic
class GenericMeasurement extends Measurement implements Map<String,Quantity>{

  @Delegate
  protected Map<String,Quantity> values = [:]
  protected String id = null
  protected String type = null

  /**
   * Set sensor ID.
   */
  void setSensorID(String id) {
    this.id = id
  }

  /**
   * Get sensor ID.
   */
  String getSensorID() {
    return id
  }

  /**
   * Set sensor type.
   */
  void setSensorType(String type) {
    this.type = type
  }

  /**
   * Get sensor type.
   */
  String getSensorType() {
    return type
  }

  /**
   * Add a measured quantity.
   */
  void set(String key, Quantity value) {
    values.put(key, value)
  }

  /**
   * Get a measured quantity.
   */
  Quantity get(String key) {
    return values.get(key)
  }

  @Override
  String toString() {
    StringBuffer sb = new StringBuffer()
    sb.append(getClass().getSimpleName() + '[')
    boolean first = true
    if (type != null) {
      sb.append("type:${type}")
      first = false
    }
    if (id != null) {
      if (!first) sb.append(' ')
      sb.append("id:${id}")
      first = false
    }
    values.each { k, v ->
      if (!first) sb.append(' ')
      sb.append("${k}:${v}")
      first = false
    }
    sb.append(']')
    return sb.toString()
  }
}
