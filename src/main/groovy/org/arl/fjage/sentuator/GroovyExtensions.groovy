package org.arl.fjage.sentuator

import org.arl.fjage.*

/**
 * Syntax extensions for easier usage in shells and Groovy agents.
 * <p>
 * <pre>
 * aid.get()
 * aid.get(type)
 * aid.set(value)
 * aid.set(type, value)
 * aid.status
 * aid.config
 * aid.config.xxx
 * aid.config.xxx = yyy
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

    AgentID.metaClass.get << { ->
      if (delegate.owner == null) throw new FjageException('get() only supported on owned agents')
      return request(new MeasurementReq(), 1000)
    }

    AgentID.metaClass.get << { String type ->
      if (delegate.owner == null) throw new FjageException('get() only supported on owned agents')
      return request(new MeasurementReq(type), 1000)
    }

    AgentID.metaClass.set << { value ->
      if (delegate.owner == null) throw new FjageException('set() only supported on owned agents')
      return request(new ActuationReq(value), 1000)
    }

    AgentID.metaClass.set << { String type, value ->
      if (delegate.owner == null) throw new FjageException('set() only supported on owned agents')
      return request(new ActuationReq(type, value), 1000)
    }

    AgentID.metaClass.getStatus = { ->
      if (delegate.owner == null) throw new FjageException('status only supported on owned agents')
      def rsp = request(new StatusReq(), 1000)
      if (rsp instanceof Status) return rsp.status + (rsp.message?': '+rsp.message:'')
      throw new FjageException('status not supported by agent')
    }

    AgentID.metaClass.getConfig = { ->
      if (delegate.owner == null) throw new FjageException('configuration only supported on owned agents')
      return new ConfigHelper(parent: delegate)
    }

  }

}

@groovy.transform.PackageScope
class ConfigHelper {

  def parent

  def propertyMissing(String name, value) {
    def rsp = parent.request(new ConfigurationReq().set(name, value), 1000)
    if (!(rsp instanceof ConfigurationRsp))
      throw new FjageException('configuration not supported by agent')
  }

  def propertyMissing(String name) {
    def rsp = parent.request(new ConfigurationReq().get(name), 1000)
    if (rsp instanceof ConfigurationRsp) return rsp.get(name)
    throw new FjageException('configuration not supported by agent')
  }

  String toString() {
    def rsp = parent.request(new ConfigurationReq(), 2000)
    if (rsp instanceof ConfigurationRsp) {
      StringBuffer sb = new StringBuffer()
      sb.append "[${rsp.cfg.name}]\n"
      rsp.cfg.each { k, v ->
        if (k != 'name') sb.append "${k} = ${v}\n"
      }
      return sb.toString()
    }
    throw new FjageException('configuration not supported by agent')
  }

}
