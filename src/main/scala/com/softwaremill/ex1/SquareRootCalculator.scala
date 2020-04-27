package com.softwaremill.ex1

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import com.softwaremill.ex1.SquareRootCalculator.SqrtResult.{Error, Result}

object SquareRootCalculator {

  def apply(): Behavior[Calculate] = Behaviors.setup { context =>
    Behaviors.receiveMessage {
      case Calculate(number, replyTo) =>
        if (number < 0) {
          context.log.warn("Negative integer!")
          replyTo ! Error("Sqrt of number < 0")
        } else {
          replyTo ! Result(Math.sqrt(number))
        }
        Behaviors.same
    }
  }

  case class Calculate(number: Double, replyTo: ActorRef[SqrtResult])

  sealed trait SqrtResult

  object SqrtResult {
    case class Error(message: String) extends SqrtResult
    case class Result(value: Double)  extends SqrtResult
  }
}
