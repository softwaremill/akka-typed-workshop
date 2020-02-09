package com.softwaremill.ex1

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}

object Adder {

  case class Add(a: Int, b: Int, replyTo: ActorRef[Result])
  case class Result(value: Int)

  def apply(): Behavior[Add] = Behaviors.receiveMessage {
    case Add(a, b, replyTo) =>
      replyTo ! Result(a + b)
      Behaviors.same
  }
}
