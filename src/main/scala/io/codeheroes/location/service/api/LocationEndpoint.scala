package io.codeheroes.location.service.api

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model.StatusCodes._
import de.heikoseeberger.akkahttpjson4s.Json4sSupport
import io.codeheroes.location.service.domain.LocationService
import org.json4s.{DefaultFormats, native}


import scala.concurrent.ExecutionContext

class LocationEndpoint(locationService: LocationService)(implicit ec: ExecutionContext) extends Json4sSupport {

  private implicit val serialization = native.Serialization
  private implicit val formats = DefaultFormats

  val routes =
    (path("location") & get & parameter('address.as[String])) { (address) =>
      onSuccess(locationService.getLocation(address)) {
        case None => complete(NotFound)
        case Some(location) => complete(location)
      }
    }

}
