package io.codeheroes.location.service.infrastructure.google

object GoogleResponses {

  final case class GoogleLocationResponse(results: List[Result])

  final case class Result(geometry: Geometry)

  final case class Geometry(location: Location)

  final case class Location(lat: Double, lng: Double)

}
