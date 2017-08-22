package io.codeheroes.location.service.core

import akka.http.scaladsl.server
import akka.http.scaladsl.testkit.{RouteTestTimeout, ScalatestRouteTest}
import com.typesafe.config.{ConfigFactory, ConfigValueFactory}
import de.heikoseeberger.akkahttpjson4s.Json4sSupport
import io.codeheroes.location.service.Application
import io.codeheroes.location.service.mock.GoogleLocationMock
import org.json4s.DefaultFormats
import org.json4s.native.JsonMethods._
import org.json4s.native.Serialization
import org.scalatest.{BeforeAndAfterAll, FlatSpec, Matchers}

import scala.concurrent.Await
import scala.concurrent.duration.{FiniteDuration, _}
import scala.language.postfixOps
import scala.util.Random

trait EndpointSpec extends FlatSpec with Matchers with ScalatestRouteTest with Json4sSupport with BeforeAndAfterAll {

  protected implicit val format = DefaultFormats
  protected implicit val serialization = Serialization
  protected implicit val timeout = RouteTestTimeout(10 seconds)

  private val googleMockPort = 10000 + Random.nextInt(999)

  protected var endpoint: server.Route = _

  override protected def beforeAll(): Unit = {
    val config = ConfigFactory.load("test.conf")
      .withValue("application.google.api.url", ConfigValueFactory.fromAnyRef(s"http://127.0.0.1:$googleMockPort"))

    new GoogleLocationMock("127.0.0.1", googleMockPort).start()

    endpoint = new Application(config).publicRoutes
  }

  def responseAsList(implicit timeout: FiniteDuration = 3 second) = responseAs[List[String]]

  def responseAsJson(implicit timeout: FiniteDuration = 3 second) =
    Await.result(responseEntity.toStrict(timeout).map(_.data.utf8String).map(parse(_)), timeout)

}
