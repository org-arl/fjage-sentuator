package org.arl.fjage.sentuator

import org.arl.fjage.*

/**
 * Request for a sensor to make a measurement.
 */
@groovy.transform.CompileStatic
class MeasurementReq extends Message {

  /**
   * Measurement type.
   */
  String type

  MeasurementReq() {
    super(Performative.REQUEST)
    type = null
  }

  MeasurementReq(String type) {
    super(Performative.REQUEST)
    this.type = type
  }

  @Override
  String toString() {
    return getClass().getSimpleName() + ':' + performative + (type?'['+type+']':'')
  }

}
