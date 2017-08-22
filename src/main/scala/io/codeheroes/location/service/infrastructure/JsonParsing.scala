package io.codeheroes.location.service.infrastructure

import akka.http.scaladsl.model.HttpResponse
import akka.stream.ActorMaterializer
import com.typesafe.scalalogging.StrictLogging
import org.json4s.DefaultFormats
import org.json4s.native.Serialization.read

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}

trait JsonParsing extends StrictLogging {

  implicit val formats = DefaultFormats

  def toJson[T: Manifest](response: HttpResponse)(implicit mat: ActorMaterializer, ec: ExecutionContext): Future[T] =
    response.entity.toStrict(5 seconds)
      .map(_.getData().utf8String)
      .map(body => try {
        read[T](body)
      } catch {
        case ex: Throwable =>
          logger.error(s"Error when trying to parse $body", ex)
          throw new IllegalStateException(s"Cannot parse response $body", ex)
      })

}
