package org.arl.fjage.sentuator

import groovy.transform.CompileStatic

/**
 * Generic key-value measurement made by a sensor.
 */
@CompileStatic
class GenericMeasurement extends Measurement {

  protected Map<String,Quantity> values = [:]

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

  /**
   * Get keys for all measured quantities.
   */
  Set<String> keySet() {
    return values.keySet()
  }

  /**
   * Groovy support for msg.key = value syntax.
   */
  void missingProperty(String key, Object value) {
    try {
      set(key, (Quantity)value)
    } catch (Exception ex) {
      throw new MissingPropertyException(key, GenericMeasurement)
    }
  }

  /**
   * Groovy support for msg.key syntax.
   */
  void missingProperty(String key) {
    try {
      get(key)
    } catch (Exception ex) {
      throw new MissingPropertyException(key, GenericMeasurement)
    }
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
