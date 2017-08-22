package io.codeheroes.location.service.api.core

import akka.actor.ActorSystem
import akka.http.scaladsl.model.StatusCodes.InternalServerError
import akka.http.scaladsl.server
import akka.http.scaladsl.server.Directives.{handleExceptions, handleRejections}
import akka.http.scaladsl.server.{ExceptionHandler, RejectionHandler}
import akka.http.scaladsl.settings.RoutingSettings

import scala.util.control.NonFatal

class EndpointSettings(endpoint: server.Route)(implicit system: ActorSystem) {
  private val routingSettings = RoutingSettings(system.settings.config)
  private val rejectionHandler = RejectionHandler.default
  private val exceptionHandler = ExceptionHandler {
    case NonFatal(e) => ctx => {
      ctx.log.error(e, "Error during processing of request: '{}'.", ctx.request)
      ctx.complete(InternalServerError)
    }
  }.seal(routingSettings)

  def routing: server.Route = {
    handleRejections(rejectionHandler) {
      handleExceptions(exceptionHandler) {
        endpoint
      }
    }
  }

}
