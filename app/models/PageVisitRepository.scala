package models

import com.datastax.driver.core.Row
import com.datastax.driver.core.BoundStatement

import scala.concurrent.ExecutionContext
import java.util.UUID

/**
 * Represents an entry in the raw_page_visits table
 */
case class PageVisit(id: UUID,
                     timestamp: Long,
                     pageTitle: String,
                     pageUrl: String,
                     visits: Long,
                     links: Long,
                     read: Long,
                     numRefs: Long,
                     search: Long,
                     social: Long)

/**
 * Represents an isolated entry of page metrics
 */
case class PageMetrics(visits: Long, links: Long, read: Long, numRefs: Long, search: Long, social: Long)

/**
 * Provides access to the page visits table in Cassandra
 */
class PageVisitRepository(client: BaseCassandraClient) {

  val tableRef = client.keyspace + "." + client.table

  /**
   * Converts a Cassandra row to a PageVisit object
   */
  private def convert(row: Row): PageVisit =
    PageVisit(row.getUUID("id"), row.getDate("timestamp").getTime, row.getString("page_title"),
      row.getString("page_url"), row.getLong("visits"), row.getLong("links"), row.getLong("read"),
      row.getLong("num_refs"), row.getLong("search"), row.getLong("social"))

  /**
   * Finds an entry by the id
   */
  def getById(id: UUID)(implicit ctxt: ExecutionContext): PageVisit = {
    val stmt = new BoundStatement(client.session.prepare("SELECT * FROM " + tableRef + " WHERE id = ?;"))
    convert(client.session.execute(stmt.bind(id)).one())
  }

  /**
   * Finds page visit records through a time range (start and end)
   */
  def getByTimestampRange(start: java.util.Date, end: java.util.Date)(implicit ctxt: ExecutionContext): List[PageVisit] = {
    import scala.collection.JavaConverters._
    val stmt = new BoundStatement(client.session.prepare("SELECT * FROM " + tableRef + " WHERE timestamp >= ? AND timestamp < ? ALLOW FILTERING;"))
    client.session.execute(stmt.bind(start, end)).all().asScala.toList.map(convert(_))
  }
}
