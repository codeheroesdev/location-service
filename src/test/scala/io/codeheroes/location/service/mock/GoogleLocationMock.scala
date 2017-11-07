package io.codeheroes.location.service.mock

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import de.heikoseeberger.akkahttpjson4s.Json4sSupport
import org.json4s.DefaultFormats
import org.json4s.native.Serialization

import scala.concurrent.Await
import scala.concurrent.duration._


class GoogleLocationMock(host: String, port: Int)(implicit mat: ActorMaterializer, system: ActorSystem) extends Json4sSupport {

  private implicit val serialization = Serialization
  private implicit val formats = DefaultFormats

  private val routes =
    (path("maps" / "api" / "geocode" / "json") & get & parameters('address.as[String], 'apikey.as[String])) {
      case ("Kazimierza Wielkiego", "123") => complete(response())
      case _ => complete(response(true))
    }

  private def response(empty: Boolean = false) =
    if (empty) Map("results" -> List.empty)
    else Map(
    "results" -> List(Map(
      "address_components" -> List(
        Map(
          "short_name" -> "ala",
          "long_name" -> "ma kota"
        )
      ),
      "geometry" -> Map(
        "location" -> Map(
          "lat" -> 50.124242,
          "lng" -> 19.812421
        )
      )
    )))

  def start(): Unit = Await.result(Http().bindAndHandle(routes, host, port), 5 seconds)

}