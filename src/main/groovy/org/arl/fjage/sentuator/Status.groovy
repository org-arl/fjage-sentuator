package org.arl.fjage.sentuator

import org.arl.fjage.*

/**
 * Sentuator status.
 */
@groovy.transform.CompileStatic
class Status extends Message {

  final static String INIT      = "INIT"
  final static String OK        = "OK"
  final static String DISABLED  = "DISABLED"
  final static String WARNING   = "WARNING"
  final static String ERROR     = "ERROR"

  String status
  String message
  long time = -1

  Status(String status) {
    super(Performative.INFORM)
    this.status = status
    this.message = null
  }

  Status(String status, String message) {
    super(Performative.INFORM)
    this.status = status
    this.message = message
  }

  @Override
  String toString() {
    return getClass().getSimpleName() + ':' + performative + '[time:' + time + ' ' + status + (message?': '+message:'') + ']'
  }

}
