package com.softwaremill.ex1

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}

object SquareRootCalculator {

  def apply(): Behavior[Calculate] = Behaviors.setup { context =>
    Behaviors.receiveMessage {
      case Calculate(x, replyTo) if x < 0 =>
        context.log.warn("Negative integer!")
        replyTo ! SqrtResult.Error(s"$x is negative, need more powerful math")
        Behaviors.same

      case Calculate(x, replyTo) =>
        replyTo ! SqrtResult.Result(math.sqrt(x))
        Behaviors.same
    }
  }

  case class Calculate(x: Int, replyTo: ActorRef[SqrtResult])

  sealed trait SqrtResult

  object SqrtResult {
    case class Error(message: String) extends SqrtResult
    case class Result(value: Double)  extends SqrtResult
  }
}
