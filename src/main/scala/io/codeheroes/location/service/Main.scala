package io.codeheroes.location.service

import com.typesafe.config.ConfigFactory

object Main extends App {
  val config = args.headOption match {
    case Some("docker") => ConfigFactory.load("docker.conf")
    case _ => ConfigFactory.load("default.conf")
  }

  new Application(config).start
}
