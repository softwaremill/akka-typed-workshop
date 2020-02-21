package com.softwaremill.ex6

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior, PostStop}

object Adder {
  case class Add(a: Int, b: Int, replyTo: ActorRef[Result])
  case class Result(value: Int)

  def apply(): Behavior[Add] = Behaviors.logMessages {
    Behaviors
      .receiveMessage[Add] {
        case Add(a, b, replyTo) =>
          replyTo ! Result(a + b)
          Behaviors.same
      }
      .receiveSignal {
        case (context, PostStop) =>
          context.log.info("My job is done, releasing resources.")
          Behaviors.same
      }
  }
}
