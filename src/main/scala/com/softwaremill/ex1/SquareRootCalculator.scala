package com.softwaremill.ex1

import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.Behaviors
import com.softwaremill.ex1.SquareRootCalculator.SqrtResult.{Error, Result}

import scala.util.{Failure, Success, Try}

object SquareRootCalculator {
  case class Calculate(x: Double, replyTo: ActorRef[SqrtResult])

  sealed trait SqrtResult

  object SqrtResult {
    case class Error(message: String) extends SqrtResult
    case class Result(value: Double)  extends SqrtResult
  }

  def apply(): Behavior[Calculate] = Behaviors.setup { context =>
    Behaviors.receiveMessage {
      case Calculate(x, replyTo) =>

        val res: SqrtResult = Try(Math.sqrt(x)) match {
          case Failure(exception) =>
            val msg = "Unexpected error: " + exception.getMessage
            context.log.error(msg)
            Error(msg)
          case Success(value) if value.isNaN =>
            context.log.warn("Negative number!")
            Error("Couldn't calculate sqrt for negative number")
          case Success(value) =>
            Result(value)
        }
        replyTo ! res
        Behaviors.same
    }
  }

}
