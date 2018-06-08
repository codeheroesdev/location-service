package io.codeheroes.location.service.infrastructure.google

object GoogleResponses {

  final case class GoogleLocationResponse(results: List[Result])

  final case class Result(geometry: Geometry, address_components: Option[List[GoogleAddressComponent]])

  final case class Geometry(location: Location)

  final case class Location(lat: Double, lng: Double)

  final case class GoogleAddressComponent(long_name: String, short_name: String)

}
