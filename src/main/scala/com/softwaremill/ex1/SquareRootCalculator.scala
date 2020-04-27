package com.softwaremill.ex1

import akka.actor.typed.{ActorRef, Behavior, LogOptions}
import akka.actor.typed.scaladsl.Behaviors
import com.softwaremill.ex1.SquareRootCalculator.SqrtResult.{Error, Result}
import org.slf4j.event.Level

object SquareRootCalculator {

  def withLog(): Behavior[Calculate] = Behaviors.logMessages(LogOptions().withLevel(Level.WARN), apply())

  def apply(): Behavior[Calculate] = Behaviors.setup { context =>
    Behaviors.receiveMessage {
      case Calculate(number, replyTo) =>
        if (number < 0) {
          context.log.warn("Negative integer!")
          replyTo ! Error("Number " + number + " is less than 0")
        } else {
          replyTo ! Result(Math.sqrt(number))

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
