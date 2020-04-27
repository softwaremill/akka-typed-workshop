package com.softwaremill.ex6

import akka.actor.typed._
import akka.actor.typed.scaladsl.AskPattern._
import akka.util.Timeout
import com.softwaremill.ex6.HttpController.{Request, Response}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._

class HttpController(system: ActorSystem[SpawnProtocol.Command]) {
  implicit val timeout: Timeout     = Timeout(3.seconds)
  implicit val scheduler: Scheduler = system.scheduler

  def process(request: Request): Future[Response] = {
    val createAdder: Future[ActorRef[Adder.Add]] =
      system.ask(SpawnProtocol.Spawn(behavior = Adder(), name = "adder", props = Props.empty, _))

    for {
      ref    <- createAdder
      result <- ref.ask[Adder.Result](Adder.Add(request.a, request.b, _))
    } yield Response(result.value)
  }
}

object HttpController {
  case class Request(a: Int, b: Int)
  case class Response(value: Int)
}
