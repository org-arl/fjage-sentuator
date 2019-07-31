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

  Measurement() {
    super(Performative.INFORM)
  }

  @Override
  String toString() {
    StringBuffer sb = new StringBuffer()
    sb.append(getClass().getSimpleName() + ':' + performative + '[')
    boolean first = true
    this.properties.each { k, v ->
      if (!(k in ['class', 'sender', 'recipient', 'messageID', 'performative', 'inReplyTo'])) {
        if (!first) sb.append(' ')
        sb.append("${k}:${v}")
        first = false
      }
    }
    sb.append(']')
    return sb.toString()
  }

}
