package com.softwaremill.ex5

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.{Behaviors, TimerScheduler}

import scala.concurrent.duration._

sealed trait Command
case object PingMsg extends Command

object TimerBehavior {
  val TimerKey = "scalar-timer"
  val delay    = 300.millis

  val behavior: Behavior[Command] =
    Behaviors.withTimers { timers: TimerScheduler[Command] =>
      timers.startTimerWithFixedDelay(TimerKey, PingMsg, delay)
      Behaviors.setup[Command] { context =>
        Behaviors.receiveMessage[Command] {
          case PingMsg =>
            context.log.info("Self ping received!")
            Behaviors.same
        }
      }
    }
}
