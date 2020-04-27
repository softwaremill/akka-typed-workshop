package com.softwaremill.ex1

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}

object SquareRootCalculator {
  def apply(): Behavior[Calculate] =
    Behaviors.setup { context =>
      Behaviors.receiveMessage {
        case Calculate(input, replyTo) =>
          if (input <= 0) {
            context.log.warn("Negative integer!")
            replyTo ! SqrtResult.Error("")
          } else {
            replyTo ! SqrtResult.Result(Math.sqrt(input))
          }
          Behaviors.same
    }
  }

  case class Calculate(input: Double, replyTo: ActorRef[SqrtResult]) //TODO add required fields

  sealed trait SqrtResult

  object SqrtResult {

    case class Error(message: String) extends SqrtResult

    case class Result(value: Double) extends SqrtResult

  }

}
