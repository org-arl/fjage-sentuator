package org.arl.fjage.sentuator

import org.arl.fjage.*

/**
 * Request for status.
 */
@groovy.transform.CompileStatic
class StatusReq extends Message {

  StatusReq() {
    super(Performative.REQUEST)
  }

  @Override
  String toString() {
    return getClass().getSimpleName() + ':' + performative
  }

}
