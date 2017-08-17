package io.codeheroes.location.service

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.typesafe.config.Config
import com.typesafe.scalalogging.StrictLogging
import io.codeheroes.location.service.api.LocationEndpoint
import io.codeheroes.location.service.api.core.EndpointSettings
import io.codeheroes.location.service.infrastructure.google.GoogleLocationService

import scala.util.{Failure, Success}

class Application(config: Config) extends StrictLogging {

  private implicit val system = ActorSystem("BeerServiceSystem", config)
  private implicit val dispatcher = system.dispatcher
  private implicit val scheduler = system.scheduler
  private implicit val materializer = ActorMaterializer()

  private val locationService = new GoogleLocationService(config.getString("application.google.api.key"))

  private val locationEndpoint = new LocationEndpoint(locationService)

  private val endpointSettings = new EndpointSettings(locationEndpoint.routes)

  def start = {
    val bindHost = config.getString("application.bind-host")
    val bindPort = config.getInt("application.bind-port")

    Http().bindAndHandle(endpointSettings.routing, bindHost, bindPort).onComplete {
      case Success(_) => logger.info(s"Client API started at $bindHost:$bindPort")
      case Failure(ex) => logger.error(s"Cannot bind API to $bindHost:$bindPort", ex)
    }
  }

}
