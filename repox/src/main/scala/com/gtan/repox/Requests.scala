package com.gtan.repox

import com.gtan.repox.data.Repo
import io.undertow.server.HttpServerExchange

object Requests {
  trait Request
  case class Get(exchange: HttpServerExchange) extends Request
  case class Head(exchange: HttpServerExchange) extends Request
}
