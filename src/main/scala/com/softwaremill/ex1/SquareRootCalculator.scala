package com.softwaremill.ex1

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior, LogOptions}
import com.softwaremill.ex1.SquareRootCalculator.SqrtResult.{Error, Result}
import org.slf4j.event.Level

object SquareRootCalculator {

  def apply(): Behavior[Calculate] =
    Behaviors.logMessages(
      LogOptions().withLevel(Level.INFO),
      Behaviors.receiveMessage {
        case Calculate(number, replyTo) =>
          if (number >= 0) {
            replyTo ! Result(Math.sqrt(number))
          } else {
            replyTo ! Error("negative numbers are not supported")
          }
          Behaviors.same
      }
    )

  def behaviorWithLogger(): Behavior[Calculate] = Behaviors.setup { context =>
    Behaviors.receiveMessage {
      case Calculate(number, replyTo) =>
        if (number >= 0) {
          replyTo ! Result(Math.sqrt(number))
        } else {
          context.log.warn("Negative integer!")
          replyTo ! Error("negative numbers are not supported")
        }
        Behaviors.same
    }
  }

  case class Calculate(a: Int, replyTo: ActorRef[SqrtResult])

  sealed trait SqrtResult

  object SqrtResult {
    case class Error(message: String) extends SqrtResult
    case class Result(value: Double)  extends SqrtResult
  }
}
