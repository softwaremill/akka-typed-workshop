package com.softwaremill.ex8

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.{Actor, ActorLogging, Props}

sealed trait Command
case class DoWork(param: Int) extends Command

object TypedUntyped {

  val behavior: Behavior[Command] = Behaviors
    .setup[Command] { context =>
      context.log.info("Parent setup")
      // TODO
      Behaviors.receiveMessage {
        case DoWork(param) =>
          Behaviors.same
      }
    }
}

object Worker {
  sealed trait WorkerCommand
  case class DoPartialWork(param: Int)

  sealed trait PartialWorkResponse

  case class PartialWorkResult(param: Int) extends PartialWorkResponse

  class WorkerActor extends Actor with ActorLogging {

    override def receive = {
      case DoPartialWork(param) =>
    }

    override def postStop(): Unit = {
      log.info("Stopping worker actor")
      super.postStop()
    }
  }
}
