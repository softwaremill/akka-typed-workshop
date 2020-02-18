package com.softwaremill.ex4

import akka.actor.typed.scaladsl.{ActorContext, Behaviors}
import akka.actor.typed._

object SupervisedBehavior {
  type SignalHandler[T] = PartialFunction[(ActorContext[T], Signal), Behavior[T]]

  sealed trait Command
  case class Request(param: Int, replyTo: ActorRef[ParentResult]) extends Command

  sealed trait ParentResult
  case class Response(param: Int)      extends ParentResult
  case class ParentFailure(param: Int) extends ParentResult

  lazy val behavior: Behavior[Command] =
    Behaviors
      .setup { context =>
        context.log.info("Parent setup")
        val worker: ActorRef[Worker.DoPartialWork] = ???

        lazy val handleRequests: Behavior[Command] =
          Behaviors
            .receiveMessage[Command] {
              case Request(param, replyTo) =>
                context.log.info(s"Delegating work ($param) to a child actor")
                worker ! Worker.DoPartialWork(param, ???)
                working(replyTo)
            }

        def working(respondTo: ActorRef[ParentResult]): Behavior[Command] =
          Behaviors
            .receiveMessage[Command] {
              case Request(param, _) =>
                context.log.error(s"Cannot handle request ($param) while worker is busy!")
                respondTo ! ParentFailure(param)
                Behaviors.same
            }

        handleRequests
      }
}

object Worker {
  case class DoPartialWork(param: Int, replyTo: ActorRef[PartialWorkResponse])

  sealed trait PartialWorkResponse

  case class PartialWorkResult(param: Int) extends PartialWorkResponse
  case class PartialWorkFailed(param: Int) extends PartialWorkResponse

  // Should multiply input by 2
  lazy val behavior: Behavior[DoPartialWork] =
    Behaviors.setup { context =>
      context.log.info("Starting child")
      Behaviors
        .receive[DoPartialWork] {
          case (context, DoPartialWork(param, replyTo)) =>
            context.log.info("Partial job done, returning result")
            val calculatedResult = param * 2
            replyTo ! PartialWorkResult(calculatedResult)
            Behaviors.same
        }
    }
}