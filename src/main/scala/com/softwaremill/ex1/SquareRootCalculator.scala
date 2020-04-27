package com.softwaremill.ex1

import akka.actor.typed.scaladsl.{ActorContext, Behaviors}
import akka.actor.typed.{ActorRef, Behavior}
import com.softwaremill.ex1.SquareRootCalculator.SqrtResult.{Error, Result}

object SquareRootCalculator {

  def apply(): Behavior[Calculate] = Behaviors.setup { context =>
    calculate(context)
  }

  def calculate(context: ActorContext[Calculate]): Behavior[Calculate] = Behaviors.receiveMessage {
    case Calculate(value, replyTo) if value >= 0 =>
      val result = Math.sqrt(value)
      replyTo ! Result(result)
      Behaviors.same
    case Calculate(value, replyTo) if value < 0 =>
      context.log.warn("Negative integer!")
      replyTo ! Error("nooooo")
      Behaviors.same
  }

  case class Calculate(value: Double, replyTo: ActorRef[SqrtResult])

  sealed trait SqrtResult

  object SqrtResult {
    case class Error(message: String) extends SqrtResult
    case class Result(value: Double)  extends SqrtResult
  }
}
