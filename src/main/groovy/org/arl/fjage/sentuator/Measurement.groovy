package org.arl.fjage.sentuator

import org.arl.fjage.*

/**
 * Measurement made by a sensor.
 */
@groovy.transform.CompileStatic
class Measurement extends Message {

  /**
   * Epoch time of measurement, -1 if unavailable.
   */
  long time = -1

  protected String id = null
  protected String type = null

  Measurement() {
    super(Performative.INFORM)
  }

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
    this.properties.each { k, v ->
      if (!(k in ['class', 'sender', 'recipient', 'messageID', 'performative', 'inReplyTo', 'type', 'id', 'time'])) {
        if (!first) sb.append(' ')
        sb.append("${k}:${v}")
        first = false
      }
    }
    sb.append(']')
    return sb.toString()
  }

}
