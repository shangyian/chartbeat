package controllers

import play.api.libs.concurrent.Execution.Implicits._
import models.{PageVisitRepository, PageMetrics}

class PageMetricsController(repository: PageVisitRepository) {

  /**
   * Obtains page metric data and finds the average # of visits, links, references etc for
   * a given range of time (between start and until start - range).
   */
  def metrics(start: Long, range: Long): Map[(String, String), PageMetrics] = {
    val currentDate = new java.util.Date(start)

    val pageVisits = repository.getByTimestampRange(new java.util.Date(start - range), currentDate)
      .groupBy(_.pageUrl)
      .map(record => ((record._1, record._2.head.pageTitle), record._2))

    pageVisits.mapValues {
      record =>
        PageMetrics(record.map(_.visits).sum / record.length,
          record.map(_.links).sum / record.length,
          record.map(_.read).sum / record.length,
          record.map(_.numRefs).sum / record.length,
          record.map(_.search).sum / record.length,
          record.map(_.social).sum / record.length)
    }
  }
}
