package org.arl.fjage.sentuator

import java.util.logging.Level
import org.arl.fjage.*

/**
 * Sentuator agent base class.
 */
@groovy.transform.CompileStatic
class Sentuator extends Agent {

  final static String ENABLE = "enable"
  final static String POLL   = "poll"

  protected Configuration cfg = null
  protected TickerBehavior poll = null
  protected long pollInterval = -1
  protected boolean enabled = true
  protected AgentID ntf = null
  private Status currentStatus = new Status(Status.OK)

  @Override
  void init() {
    ntf = topic()
    setup()
    add new OneShotBehavior() {
      @Override
      public void action() {
        startup()
      }
    }
    add new MessageBehavior() {
      @Override
      public void onReceive(Message msg) {
        if (msg.performative == Performative.REQUEST) {
          Message rsp = processRequest(msg)
          if (rsp == null) rsp = new Message(msg, Performative.NOT_UNDERSTOOD)
          Sentuator.this.send(rsp)
        }
      }
    }
  }

  @Override
  boolean send(final Message m) {
    if (m instanceof Measurement && m.time < 0) m.time = platform.currentTimeMillis()
    return super.send(m)
  }

  /**
   * Called by Sentuator when the agent is being initialized.
   */
  protected void setup() {
    // do nothing
  }

  /**
   * Called by Sentuator after all agents are fully initialized.
   */
  protected void startup() {
    // do nothing
  }

  /**
   * Make a measurement.
   */
  protected Measurement measure() {
    return null
  }

  /**
   * Make a measurement.
   */
  protected Measurement measure(String type) {
    return null
  }

  /**
   * Actuate.
   */
  protected boolean actuate(Object value) {
    return false
  }

  /**
   * Actuate.
   */
  protected boolean actuate(String type, Object value) {
    return false
  }

  /**
   * Enable/disable sensor/actuator.
   */
  protected void enable(boolean b) {
    enabled = b
    if (!b) {
      setPollingInterval(0)
      setStatus(Status.DISABLED)
    }
  }

  /**
   * Set up polling for measurement.
   *
   * @param ms polling interval in milliseconds, 0 to disable.
   */
  protected void setPollingInterval(long ms) {
    if (poll != null) poll.stop()
    if (ms <= 0) poll = null
    else {
      poll = new TickerBehavior(ms) {
        @Override
        void onTick() {
          Measurement m = measure()
          if (m != null) {
            m.recipient = ntf
            Sentuator.this.send(m)
          }
        }
      }
    }
    pollInterval = ms
  }

  /**
   * Set the status of the sensor/actuator.
   */
  protected void setStatus(String status) {
    setStatus(status, null)
  }

  /**
   * Set the status of the sensor/actuator.
   */
  protected void setStatus(String status, String message) {
    boolean changed = false
    if (currentStatus.status != status || currentStatus.message != message) {
      currentStatus = new Status(status, message)
      currentStatus.recipient = ntf
      changed = true
    }
    currentStatus.time = platform.currentTimeMillis()
    if (changed) send(currentStatus)
  }

  /**
   * Get the status of the sensor/actuator.
   */
  protected Status getStatus() {
    Status s = clone(currentStatus)
    s.time = platform.currentTimeMillis()
    return s
  }

  /**
   * Set configuration parameter.
   */
  protected void setConfigParam(String key, Object value) {
    try {
      if (key == ENABLE) {
        if (value) enabled = true
        else enabled = false
      } else if (key == POLL) {
        setPollingInterval((long)value)
      } else if (cfg != null && key != "class") {
        cfg.setProperty(key, value)
      }
    } catch (Exception ex) {
      // do nothing
    }
  }

  /**
   * Get configuration parameter.
   */
  protected Object getConfigParam(String key) {
    if (key == ENABLE) return enabled
    if (key == POLL) return pollInterval
    if (cfg == "class") return null
    if (cfg == null) return null
    try {
      return cfg.getProperty(key)
    } catch (Exception ex) {
      // do nothing
    }
    return null
  }

  /**
   * Handle request messages.
   *
   * @return response message, or null if request not understood.
   */
  protected Message processRequest(Message req) {
    if (req instanceof MeasurementReq) {
      Measurement m = req.type ? measure(req.type) : measure()
      if (m == null) return new Message(req, Performative.REFUSE)
      m.recipient = req.sender
      m.inReplyTo = req.messageID
      return m
    }
    if (req instanceof ActuationReq) {
      boolean ok = req.type ? actuate(req.type, req.value) : actuate(req.value)
      return new Message(req, ok ? Performative.AGREE : Performative.REFUSE)
    }
    if (req instanceof StatusReq) {
      Status status = getStatus()
      status.recipient = req.sender
      status.inReplyTo = req.messageID
      return status
    }
    if (req instanceof ConfigurationReq) {
      ConfigurationRsp rsp = new ConfigurationRsp(req)
      req.settings.each { k, v ->
        setConfigParam(k, v)
        Object v1 = getConfigParam(k)
        if (v1 != null) rsp.cfg.put(k, v1)
      }
      req.queries.each { q ->
        Object v = getConfigParam(q)
        if (v != null) rsp.cfg.put(q, v)
      }
      if (req.settings.size() == 0 && req.queries.size() == 0) {
        if (cfg != null) {
          try {
            cfg.properties.each { k, v ->
              if (k instanceof String && k != "class") rsp.cfg.put((String)k, v)
            }
          } catch (Exception ex) {
            // do nothing
          }
        }
      }
      return rsp
    }
    return null
  }

  /**
   * Get the current log level for the agent.
   */
  Level getLogLevel() {
    Level lvl = log.getLevel()
    if (lvl == null && log.getParent() != null) lvl = log.getParent().getLevel()
    return lvl
  }

  /**
   * Set the current log level for the agent.
   */
  void setLogLevel(Level lvl) {
    log.setLevel(lvl)
  }

}
