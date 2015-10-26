package controllers

import play.api.mvc._
import models.PageVisitRepository
import play.api.Routes

class Application(repository: PageVisitRepository) extends Controller {

  private val pageMetrics: PageMetricsController = new PageMetricsController(repository)

  /**
   * Gets the page url metrics from the page visits table and prepares it for display
   */
  def index = Action {
    val current = System.currentTimeMillis
    val pageUrlToMetricsMap = pageMetrics.metrics(current, Intervals.HALFHOURLY).toList.sortBy(entry => entry._2.visits).reverse
    Ok(views.html.index(pageUrlToMetricsMap))
  }

  /**
   * Sets up the javascript routing
   */
  def javascriptRoutes = Action { implicit request =>
    import routes.javascript._
    Ok(
      Routes.javascriptRouter("jsRoutes")(
        Application.index
      )
    ).as("text/javascript")
  }
}

/**
 * Represents the different time intervals expressed in milliseconds
 */
object Intervals {
  val MINUTELY = 60*1000
  val HALFHOURLY = 30*60*1000
  val HOURLY = 60*MINUTELY
  val DAILY = 24*HOURLY
  val WEEKLY = 7*DAILY
}