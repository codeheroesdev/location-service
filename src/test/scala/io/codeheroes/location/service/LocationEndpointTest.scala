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

      (json \ "addressComponents" \ "shortName").extract[List[String]] should contain only "ala"
      (json \ "addressComponents" \ "longName").extract[List[String]] should contain only "ma kota"
    }

  it should "return 404 for non known address" in
    Get("/location?address=unknown") ~> endpoint ~> check {
      status shouldBe NotFound
    }

  it should "return 200 with location for address with special characters" in
    Get("/location?address=Krak%C3%B3w,%20Sta%C5%84czyka%21,%20/%5C23413%C5%9B%C4%87%C5%BA%C5%84%C5%82%C3%B3-%27%5C%21%20,./") ~> endpoint ~> check {
      status shouldBe OK
    }

}
