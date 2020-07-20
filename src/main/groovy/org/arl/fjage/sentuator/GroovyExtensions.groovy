package org.arl.fjage.sentuator

import org.arl.fjage.*

/**
 * Syntax extensions for easier usage in shells and Groovy agents.
 * <p>
 * <pre>
 * aid.measure()
 * aid.measure(type)
 * aid.actuate(value)
 * aid.actuate(type, value)
 * aid.status
 * </pre>
 */
class GroovyExtensions {

  /**
   * Private constructor to disable class instantiation.
   */
  private GroovyExtensions() { }

  /**
   * Enable syntax extensions for use by Groovy agents. This method should be called once
   * during the initialization of the application.
   */
  static void enable() {

    AgentID.metaClass.measure << { ->
      if (delegate.owner == null) throw new FjageException('measure() only supported on owned agents')
      return request(new MeasurementReq(), 1000)
    }

    AgentID.metaClass.measure << { String type ->
      if (delegate.owner == null) throw new FjageException('measure() only supported on owned agents')
      return request(new MeasurementReq(type), 1000)
    }

    AgentID.metaClass.actuate << { value ->
      if (delegate.owner == null) throw new FjageException('actuate() only supported on owned agents')
      return request(new ActuationReq(value), 1000)
    }

    AgentID.metaClass.actuate << { String type, value ->
      if (delegate.owner == null) throw new FjageException('actuate() only supported on owned agents')
      return request(new ActuationReq(type, value), 1000)
    }

    AgentID.metaClass.getStatus = { ->
      if (delegate.owner == null) throw new FjageException('status only supported on owned agents')
      def rsp = request(new StatusReq(), 1000)
      if (rsp instanceof Status) return rsp.status + (rsp.message?': '+rsp.message:'')
      throw new FjageException('status not supported by agent')
    }

  }

}
