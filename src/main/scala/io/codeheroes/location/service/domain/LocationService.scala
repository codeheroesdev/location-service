package io.codeheroes.location.service.domain

import scala.concurrent.Future

trait LocationService {

  def getLocation(address: String): Future[Option[Location]]

}

final case class Location(longitude: Double, latitude: Double)