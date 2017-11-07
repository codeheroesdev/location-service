package io.codeheroes.location.service.infrastructure.google

import akka.actor.{ActorSystem, Scheduler}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.StatusCodes.OK
import akka.http.scaladsl.model.Uri.Query
import akka.http.scaladsl.model.{HttpRequest, Uri}
import akka.pattern.CircuitBreaker
import akka.stream.ActorMaterializer
import com.typesafe.scalalogging.StrictLogging
import io.codeheroes.location.service.domain._
import io.codeheroes.location.service.infrastructure.JsonParsing
import io.codeheroes.location.service.infrastructure.google.GoogleResponses.GoogleLocationResponse

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}

class GoogleLocationService(apiUrl: String, apiKey: String)(
    implicit mat: ActorMaterializer,
    system: ActorSystem,
    ec: ExecutionContext,
    scheduler: Scheduler)
    extends LocationService
    with StrictLogging
    with JsonParsing {

  private val client = Http()
  private val breaker =
    new CircuitBreaker(scheduler,
                       maxFailures = 3,
                       callTimeout = 15 seconds,
                       resetTimeout = 15 seconds)
      .onOpen(logger.error("CircuitBreaker for RESTLocationService opened."))
      .onHalfOpen(
        logger.warn("CircuitBreaker for RESTLocationService half opened."))
      .onClose(logger.info("CircuitBreaker for RESTLocationService closed."))

  def _getLocation(address: String): Future[Option[Location]] = {
    val request = HttpRequest(uri = Uri(s"$apiUrl/maps/api/geocode/json").withQuery(Query(Map("address" -> address, "apikey" -> apiKey))))

    client
      .singleRequest(request)
      .map(response => (response.status, response))
      .flatMap {
        case (OK, response) =>
          toJson[GoogleLocationResponse](response).map(response =>
            response.results.headOption.map { response =>
              Location(
                response.geometry.location.lng,
                response.geometry.location.lat,
                response.address_components
                  .map(_.map(address =>
                    AddressComponent(address.short_name, address.long_name)))
                  .toList
                  .flatten
              )

          })
        case (_, response) =>
          response
            .discardEntityBytes()
            .future()
            .map(_ =>
              throw new IllegalStateException(
                s"Unhandled response: [$response] for request: [$request]"))
      }
  }

  override def getLocation(address: String): Future[Option[Location]] =
    breaker.withCircuitBreaker(_getLocation(address))
}
