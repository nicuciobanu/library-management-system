package library.management.system.com.queue

import fs2.kafka._
import io.circe.{Decoder, Json}
import cats.effect.IO
import library.management.system.com.model.Exceptions._

object QueueCodec {
  def deserializer[Message: Decoder]: Deserializer[IO, Either[DeserializationError, Message]] =
    Deserializer
      .string[IO]
      .flatMap(rawBody =>
        io.circe.parser
          .parse(rawBody)
          .fold(
            error => GenericDeserializer.fail[IO, Json](InvalidJson(rawBody, error)),
            GenericDeserializer.const[IO, Json]
          )
      )
      .flatMap(json =>
        json
          .as[Message]
          .fold(
            decodingFailure =>
              GenericDeserializer
                .fail[IO, Message](InvalidEntity(json, decodingFailure)),
            GenericDeserializer.const[IO, Message]
          )
      )
      .attempt
      .map(_.left.map {
        case desError: DeserializationError => desError
        case unexpectedError                => UnexpectedError(unexpectedError)
      })
}
