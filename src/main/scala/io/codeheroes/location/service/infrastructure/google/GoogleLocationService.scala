package io.codeheroes.location.service.infrastructure.google

import akka.actor.{ActorSystem, Scheduler}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.model.StatusCodes.OK
import akka.pattern.CircuitBreaker
import akka.stream.ActorMaterializer
import com.typesafe.scalalogging.StrictLogging
import io.codeheroes.location.service.domain.{Location, LocationService}
import io.codeheroes.location.service.infrastructure.JsonParsing
import io.codeheroes.location.service.infrastructure.google.GoogleResponses.GoogleLocationResponse

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}

class GoogleLocationService(apiUrl: String, apiKey: String)(implicit mat: ActorMaterializer, system: ActorSystem, ec: ExecutionContext, scheduler: Scheduler) extends LocationService with StrictLogging with JsonParsing {

  private val client = Http()
  private val breaker =
    new CircuitBreaker(
      scheduler,
      maxFailures = 3,
      callTimeout = 15 seconds,
      resetTimeout = 15 seconds)
      .onOpen(logger.error("CircuitBreaker for RESTLocationService opened."))
      .onHalfOpen(logger.warn("CircuitBreaker for RESTLocationService half opened."))
      .onClose(logger.info("CircuitBreaker for RESTLocationService closed."))

  def _getLocation(address: String): Future[Option[Location]] = {
    val request = HttpRequest(uri = s"$apiUrl/maps/api/geocode/json?address=${address.replaceAll(" ", "+").replacePolishCharactersAndToLow}&apikey=$apiKey")

    client
      .singleRequest(request)
      .map(response => (response.status, response))
      .flatMap {
        case (OK, response) => toJson[GoogleLocationResponse](response).map(response => response.results.headOption.map(_.geometry.location).map(result => Location(result.lng, result.lat)))
        case (_, response) => throw new IllegalStateException(s"Unhandled response: [$response] for request: [$request]")
      }
  }

  implicit class PolishString(value: String) {
    private val replacements = Map(
      'ą' -> 'a',
      'ć' -> 'c',
      'ę' -> 'e',
      'ł' -> 'l',
      'ń' -> 'n',
      'ó' -> 'o',
      'ś' -> 's',
      'ż' -> 'z',
      'ź' -> 'z'
    )

    def replacePolishCharactersAndToLow = value.toLowerCase().map(c => replacements.getOrElse(c, c))
  }

  override def getLocation(address: String): Future[Option[Location]] = breaker.withCircuitBreaker(_getLocation(address))
}
