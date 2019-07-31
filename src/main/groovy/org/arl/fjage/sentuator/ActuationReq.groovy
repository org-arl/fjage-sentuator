package org.arl.fjage.sentuator

import org.arl.fjage.*

/**
 * Request for actuation.
 */
@groovy.transform.CompileStatic
class ActuationReq extends Message {

  String type
  Object value

  ActuationReq(Object value) {
    super(Performative.REQUEST)
    this.type = null
    this.value = value
  }

  ActuationReq(String type, Object value) {
    super(Performative.REQUEST)
    this.type = type
    this.value = value
  }

  @Override
  String toString() {
    return getClass().getSimpleName() + ':' + performative + '[' + (type?type+':':'') + value + ']'
  }

}
