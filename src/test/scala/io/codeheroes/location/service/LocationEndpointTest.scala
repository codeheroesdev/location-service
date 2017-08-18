package io.codeheroes.location.service

import akka.http.scaladsl.model.StatusCodes._
import io.codeheroes.location.service.core.EndpointSpec

class LocationEndpointTest extends EndpointSpec {

  "Location Service" should "respond with 200 and location for address" in
    Get("/location?address=Kazimierza%20Wielkiego") ~> endpoint ~> check {
      status shouldBe OK
      val json = responseAsJson
      (json \ "latitude").extract[Double] shouldBe 50.124242
      (json \ "longitude").extract[Double] shouldBe 19.812421
    }

  it should "return 404 for non known address" in
    Get("/location?address=unknown") ~> endpoint ~> check {
      status shouldBe NotFound
    }

}
