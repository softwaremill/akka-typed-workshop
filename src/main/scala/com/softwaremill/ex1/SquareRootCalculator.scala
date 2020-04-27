package com.softwaremill.ex1

import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.Behaviors
import com.softwaremill.ex1.SquareRootCalculator.SqrtResult.{Error, Result}

object SquareRootCalculator {
  def apply(): Behavior[Calculate] = Behaviors.setup { context =>
    Behaviors.receiveMessage {
      case Calculate(number, sender) =>
        if (number >= 0) sender ! Result(Math.sqrt(number))
        else {
          context.log.warn("Negative integer!")
          sender ! Error("Negative")
        }
        Behaviors.same
    }
  }

  case class Calculate(number: Int, replyTo: ActorRef[SqrtResult])

  sealed trait SqrtResult

  object SqrtResult {
    case class Error(message: String) extends SqrtResult
    case class Result(value: Double)  extends SqrtResult
  }
}
