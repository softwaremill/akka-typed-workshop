package com.softwaremill.ex1

import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.Behaviors
import com.softwaremill.ex1.SquareRootCalculator.SqrtResult._

object SquareRootCalculator {
  def apply(): Behavior[Calculate] = Behaviors.setup { context =>
    Behaviors.receiveMessage {
      case Calculate(i, replyTo) =>
        if (i > 0) {
          replyTo ! Result(math.sqrt(i))
        } else {
          replyTo ! Error("Negative integer!")
          context.log.warn("Negative integer!")
        }

        Behaviors.same
    }
  }


  case class Calculate(i: Double, replyTo: ActorRef[SqrtResult]) //TODO add required fields

  sealed trait SqrtResult

  object SqrtResult {

    case class Error(message: String) extends SqrtResult

    case class Result(value: Double) extends SqrtResult

  }

}
