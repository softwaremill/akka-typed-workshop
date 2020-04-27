package com.softwaremill.ex7

import akka.actor.typed.{ActorSystem, Scheduler}
import akka.actor.typed.receptionist.Receptionist
import akka.actor.typed.scaladsl.AskPattern._
import akka.util.Timeout
import com.softwaremill.ex7.Adder.Result
import com.softwaremill.ex7.HttpController.{Request, Response}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._

class HttpController(system: ActorSystem[Nothing]) {
  implicit val timeout: Timeout = Timeout(3.seconds)
  implicit val scheduler: Scheduler = system.scheduler

  def process(request: Request): Future[Response] = {
    system.receptionist.ask[Receptionist.Listing](Receptionist.Find(Adder.AdderKey, _)).flatMap {
      case Adder.AdderKey.Listing(listing) =>
        listing.headOption match {
          case Some(adder) =>
            for {
              result <- adder.ask[Adder.Result](Adder.Add(request.a, request.b, _))
            } yield Response(result.value)
          case None => throw new IllegalStateException("Adder is missing")
        }
    }
  }
}

object HttpController {
  case class Request(a: Int, b: Int)
  case class Response(value: Int)

  object Response {
    def apply(result: Result): Response = Response(result.value)
  }
}
