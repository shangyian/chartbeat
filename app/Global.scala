import models.BaseCassandraClient
import models.PageVisitRepository
import play.api.Application
import play.api.GlobalSettings

object Global extends GlobalSettings {

  private var cassandra: BaseCassandraClient = _
  private var controller: controllers.Application = _

  override def onStart(app: Application) {
    cassandra = new BaseCassandraClient(app.configuration.getString("cassandra.node")
      .getOrElse(throw new IllegalArgumentException("No 'cassandra.node' config found.")), "chartbeat", "raw_page_visits")
    controller = new controllers.Application(new PageVisitRepository(cassandra))
  }

  override def getControllerInstance[A](clazz: Class[A]): A = {
    if (clazz == classOf[controllers.Application])
      controller.asInstanceOf[A]
    else
      throw new IllegalArgumentException(s"Controller of class $clazz not yet supported")
  }

  override def onStop(app: Application) {
    cassandra.close
  }
}