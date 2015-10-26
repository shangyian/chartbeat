package models

import com.datastax.driver.core.querybuilder.QueryBuilder
import com.datastax.driver.core.Cluster
import com.datastax.driver.core.ResultSetFuture

/**
 * Basic client for accessing Cassandra
 * @param node the Cassandra node
 * @param keyspace keyspace in Cassandra
 * @param table table name
 */
class BaseCassandraClient(node: String, val keyspace: String, val table: String) {

  private val cluster = Cluster.builder().addContactPoint(node).build()
  val session = cluster.connect()

  def getRows: ResultSetFuture = {
    val query = QueryBuilder.select().all().from(keyspace, table)
    session.executeAsync(query)
  }

  def close() {
    session.close
    cluster.close
  }
}