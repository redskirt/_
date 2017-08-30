package com.gtan.repox.data

import java.util.concurrent.atomic.AtomicLong

import com.gtan.repox.config.Config
import com.ning.http.client.Realm.{AuthScheme, RealmBuilder}
import com.ning.http.client.{AsyncHttpClientConfig, AsyncHttpClient}
import play.api.libs.json.Json

import scala.concurrent.duration.Duration
import com.ning.http.client.{Realm => JRealm}

case class Realm(user: String, password: String, scheme: String) {
  def toJava: JRealm = new RealmBuilder().setPrincipal(user)
    .setPassword(password)
    .setUsePreemptiveAuth(true)
    .setScheme(AuthScheme.valueOf(scheme))
    .build()
}

case class Connector(id: Option[Long],
                     name: String,
                     connectionTimeout: Duration,
                     connectionIdleTimeout: Duration,
                     maxConnections: Int,
                     maxConnectionsPerHost: Int,
                     credentials: Option[Realm] = None) {


  def createClient = {
    val configBuilder = new AsyncHttpClientConfig.Builder()
      .setRequestTimeout(Int.MaxValue)
      .setReadTimeout(connectionIdleTimeout.toMillis.toInt)
      .setConnectTimeout(connectionTimeout.toMillis.toInt)
      .setPooledConnectionIdleTimeout(connectionIdleTimeout.toMillis.toInt)
      .setAllowPoolingConnections(true)
      .setAllowPoolingSslConnections(true)
      .setMaxConnectionsPerHost(maxConnections)
      .setMaxConnections(maxConnectionsPerHost)
      .setFollowRedirect(true)
    val withCredentials = this.credentials.fold(configBuilder) { x => configBuilder.setRealm(x.toJava) }
    val builder = Config.proxyUsage.get(this).fold(withCredentials) { x => withCredentials.setProxyServer(x.toJava) }

    new AsyncHttpClient(builder.build())
  }
}

object Connector {
  lazy val nextId: AtomicLong = new AtomicLong(Config.connectors.flatMap(_.id).reduceOption[Long](math.max).getOrElse(1L))

  import DurationFormat._

  implicit val realmFormat = Json.format[Realm]
  implicit val format = Json.format[Connector]
}
