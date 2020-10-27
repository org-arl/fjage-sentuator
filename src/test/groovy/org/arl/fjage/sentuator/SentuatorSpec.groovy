import groovy.transform.CompileStatic
import spock.lang.*
import org.arl.fjage.*
import org.arl.fjage.param.*
import org.arl.fjage.remote.Gateway
import org.arl.fjage.sentuator.*

class SentuatorSpec extends Specification {

  static class MySentuator extends Sentuator {

    static class MyMeasurement extends Measurement {
      String type
      float x
    }

    private int setupCount = 0
    private int setupCountAtStartup = 0
    private float x = 0

    @Override
    void setup() {
      config.ofs = 0.0
      setupCount++
      enable = true
    }

    @Override
    void startup() {
      setupCountAtStartup = setupCount
    }

    @Override
    Measurement measure() {
      return new MyMeasurement(x: x+config.ofs)
    }

    @Override
    Measurement measure(String type) {
      return new MyMeasurement(type: type, x: x+1)
    }

    @Override
    boolean actuate(Object value) {
      x = value as float
      return true
    }

    @Override
    boolean actuate(String type, Object value) {
      if (type == 'bad') {
        if (value) setStatus(Status.WARNING, 'Bad actuation')
        else setStatus(Status.OK)
        return true
      }
      return false
    }

  }

  @Shared Platform platform
  @Shared Container container
  @Shared MySentuator aut
  @Shared Gateway gw

  def setupSpec() {
    org.arl.fjage.GroovyExtensions.enable()
    org.arl.fjage.sentuator.GroovyExtensions.enable()
    platform = new RealTimePlatform()
    container = new Container(platform)
    aut = new MySentuator()
    container.add 'aut', aut
    platform.start()
    gw = new Gateway(container)
  }

  def cleanupSpec() {
    platform.shutdown()
  }

  def "setup, then startup" () {
    expect:
      aut.setupCount == 1
      aut.setupCountAtStartup == 1
  }

  def "must advertise service" () {
    expect:
      gw.agentForService(org.arl.fjage.sentuator.Services.SENTUATOR)?.name == aut.name
  }

  def "status OK" () {
    when:
      def aid = gw.agentForService(org.arl.fjage.sentuator.Services.SENTUATOR)
      def rsp = gw.request new StatusReq(recipient: aid)
    then:
      rsp.status == Status.OK
      rsp.time >= 0
  }

  def "basic actuation and measurement" () {
    when:
      def aid = gw.agentForService(org.arl.fjage.sentuator.Services.SENTUATOR)
      def rsp1 = aid << new ActuationReq(1.0)
      def rsp2 = aid << new MeasurementReq()
      def rsp3 = aid << new ActuationReq(2.0)
      def rsp4 = aid << new MeasurementReq()
    then:
      rsp1.performative == Performative.AGREE
      rsp2 instanceof Measurement
      rsp2.performative == Performative.INFORM
      rsp2.x == 1.0
      rsp2.time >= 0
      rsp3.performative == Performative.AGREE
      rsp4 instanceof Measurement
      rsp4.performative == Performative.INFORM
      rsp4.x == 2.0
      rsp4.time >= rsp2.time
  }

  def "typed actuation and measurement" () {
    when:
      def aid = gw.agentForService(org.arl.fjage.sentuator.Services.SENTUATOR)
      def rsp1 = aid << new ActuationReq(7.0)
      def rsp2 = aid << new MeasurementReq('special')
      def rsp3 = aid << new ActuationReq('special', 2.0)
      def rsp4 = aid << new MeasurementReq('special')
    then:
      rsp1.performative == Performative.AGREE
      rsp2 instanceof Measurement
      rsp2.performative == Performative.INFORM
      rsp2.type == 'special'
      rsp2.x == 8.0
      rsp2.time >= 0
      rsp3.performative == Performative.REFUSE
      rsp4 instanceof Measurement
      rsp4.performative == Performative.INFORM
      rsp4.type == 'special'
      rsp4.x == 8.0
      rsp4.time >= rsp2.time
  }

  def "disable and enable" () {
    when:
      def aid = gw.agentForService(org.arl.fjage.sentuator.Services.SENTUATOR)
      aid << new ConfigurationReq().set('enable', false)
      def rsp1 = aid << new StatusReq()
      def rsp2 = aid << new MeasurementReq()
      def rsp3 = aid << new ActuationReq(0)
      aid << new ConfigurationReq().set('enable', true)
      def rsp4 = aid << new StatusReq()
      def rsp5 = aid << new MeasurementReq()
      def rsp6 = aid << new ActuationReq(0)
    then:
      rsp1.status == Status.DISABLED
      rsp1.time >= 0
      rsp2.performative == Performative.REFUSE
      rsp3.performative == Performative.REFUSE
      rsp4.status == Status.OK
      rsp4.time >= 0
      rsp5.performative == Performative.INFORM
      rsp6.performative == Performative.AGREE
  }

  def "get configuration" () {
    when:
      def aid = gw.agentForService(org.arl.fjage.sentuator.Services.SENTUATOR)
      def rsp1 = aid << new ConfigurationReq().get('ofs')
      def rsp3 = aid << new ConfigurationReq().get('missing').get('ofs')
      def rsp4 = aid << new ConfigurationReq()
    then:
      rsp1.parameters().size() == 1
      rsp1.get(new ConfigParam('ofs')) == 0.0
      rsp3.parameters().size() == 1
      rsp3.get(new ConfigParam('ofs')) == 0.0
      rsp4.parameters().size() == 4
      rsp4.get(new ConfigParam('ofs')) == 0.0
      rsp4.get(SentuatorParam.enable) == true
      rsp4.get(SentuatorParam.poll) == 0
  }

  def "set configuration" () {
    when:
      def aid = gw.agentForService(org.arl.fjage.sentuator.Services.SENTUATOR)
      aid << new ActuationReq(1.0)
      def rsp1 = aid << new ConfigurationReq().set('ofs', 1.0)
      def rsp2 = aid << new MeasurementReq()
      def rsp3 = aid << new ConfigurationReq().get('ofs')
      def rsp4 = aid << new ConfigurationReq().set('ofs', 0.0)
      def rsp5 = aid << new MeasurementReq()
    then:
      rsp1.parameters().size() == 1
      rsp1.get(new ConfigParam('ofs')) == 1.0
      rsp2.x == 2.0
      rsp3.get(new ConfigParam('ofs')) == 1.0
      rsp4.parameters().size() == 1
      rsp4.get(new ConfigParam('ofs')) == 0.0
      rsp5.x == 1.0
  }

  def "status management" () {
    when:
      def aid = gw.agentForService(org.arl.fjage.sentuator.Services.SENTUATOR)
      def rsp1 = aid << new StatusReq()
      def rsp2 = aid << new ActuationReq('bad', true)
      def rsp3 = aid << new StatusReq()
      def rsp4 = aid << new ActuationReq('bad', false)
      def rsp5 = aid << new StatusReq()
    then:
      rsp1.status == Status.OK
      rsp1.message == null
      rsp2.performative == Performative.AGREE
      rsp3.status == Status.WARNING
      rsp3.message == 'Bad actuation'
      rsp4.performative == Performative.AGREE
      rsp5.status == Status.OK
      rsp5.message == null
  }

  def "polling" () {
    when:
      def aid = gw.agentForService(org.arl.fjage.sentuator.Services.SENTUATOR)
      gw.subscribe(gw.topic(aid))
      aid << new ActuationReq(1.0)
      aid << new ConfigurationReq().set("poll", 100)
      sleep(1000)
      def ntf
      int cnt1 = 0
      while ((ntf = gw.receive()) != null) {
        if (ntf instanceof Measurement) cnt1++
      }
      aid << new ConfigurationReq().set("poll", 0)
      sleep(1000)
      int cnt2 = 0
      while ((ntf = gw.receive()) != null) {
        if (ntf instanceof Measurement) cnt2++
      }
      gw.unsubscribe(gw.topic(aid))
    then:
      println("expecting 10 messages, got ${cnt1}")
      cnt1 >= 9
      cnt1 <= 11
      cnt2 == 0
  }

  def "sentuator name" () {
    when:
      def aid = gw.agentForService(org.arl.fjage.sentuator.Services.SENTUATOR)
      def rsp1 = aid << new ConfigurationReq().get(Sentuator.NAME)
      aut.sentuatorName = 'MySentuator'
      def rsp2 = aid << new ConfigurationReq().get(Sentuator.NAME)
      aut.sentuatorName = null
    then:
      rsp1.get(Sentuator.NAME) == 'aut'
      rsp2.get(Sentuator.NAME) == 'MySentuator'
  }

  def "groovy extensions" () {
    given:
      def aid = gw.agentForService(org.arl.fjage.sentuator.Services.SENTUATOR)
      aid.actuate(1.0)
    expect:
      aid.measure().x == 1.0
      aid.measure('special').x == 2.0
      aid.status == Status.OK
      aid.enable == true
  }

  def "groovy extensions setter" () {
    when:
      def aid = gw.agentForService(org.arl.fjage.sentuator.Services.SENTUATOR)
      aid.enable = false
      def c1 = aid.enable
      def s1 = aid.status
      aid.enable = true
      def c2 = aid.enable
      def s2 = aid.status
    then:
      c1 == false
      s1 == Status.DISABLED
      c2 == true
      s2 == Status.OK
  }

  @groovy.transform.CompileStatic
  class GenericMeasurementTest {
    static boolean run() {
      def m = new GenericMeasurement(id: 'ID', type: 'XXX')
      def q1 = new Quantity(27.42)
      def q2 = new Quantity(42.27, 'm')
      m.abc = q1
      m.xyz = q2
      assert m.getSensorID() == 'ID'
      assert m.getSensorType() == 'XXX'
      assert m.getSensorType() == 'XXX'
      assert m.abc == q1
      assert m.xyz == q2
      assert q1.toString() == '27.42'
      assert q2.toString() == '42.27 m'
      return true
    }
  }

  def "generic measurements" () {
    when:
      def passed = GenericMeasurementTest.run()
    then:
      passed
  }
}
