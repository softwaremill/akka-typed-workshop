package com.softwaremill.ex7
import akka.actor.typed.receptionist.{Receptionist, ServiceKey}
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}

object Adder {
  val AdderKey = ServiceKey[Add]("adderKey")
  case class Add(a: Int, b: Int, replyTo: ActorRef[Result])
  case class Result(value: Int)

  def apply(): Behavior[Add] = Behaviors.logMessages {
    Behaviors
      .receiveMessage[Add] {
        case Add(a, b, replyTo) =>
          replyTo ! Result(a + b)
          Behaviors.same
      }
  }
}
