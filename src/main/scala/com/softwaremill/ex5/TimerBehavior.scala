package com.softwaremill.ex5

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.Behaviors

sealed trait Command
case object PingMsg extends Command

object TimerBehavior {

  val behavior: Behavior[Command] =
    // TODO
    Behaviors.setup[Command] { context =>
      Behaviors.receiveMessage[Command] {
        case PingMsg =>
          context.log.info("Self ping received!")
          Behaviors.same
      }
    }
}
