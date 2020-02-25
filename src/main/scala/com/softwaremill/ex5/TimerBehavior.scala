package com.softwaremill.ex5

import akka.actor.typed.Behavior
import akka.actor.typed.scaladsl.{Behaviors, TimerScheduler}

import scala.concurrent.duration._

sealed trait Command
case object PingMsg         extends Command
case object CancellablePing extends Command

object TimerBehavior {
  val TimerKey = "scalar-timer"
  val delay    = 300.millis

  val behavior: Behavior[Command] =
    Behaviors.withTimers { timers: TimerScheduler[Command] =>
      timers.startTimerWithFixedDelay(TimerKey, PingMsg, delay)
      Behaviors.setup[Command] { context =>
        Behaviors.receiveMessagePartial[Command] {
          case PingMsg =>
            context.log.info("Self ping received!")
            Behaviors.same
        }
      }
    }

  val CancellablePingTimerKey = "scalar-timer-2"

  def behavior2: Behavior[Command] =
    Behaviors.withTimers { timers: TimerScheduler[Command] =>
      timers.startSingleTimer(TimerKey, PingMsg, 300.millis)
      timers.startSingleTimer(CancellablePingTimerKey, CancellablePing, 1500.millis)

      Behaviors.setup[Command] { context =>
        Behaviors.receiveMessage[Command] {
          case PingMsg =>
            context.log.info("Self ping received (exercise 5-2)!")
            Behaviors.same
          case CancellablePing =>
            context.log.info("This should not happen!")
            Behaviors.same
        }
      }
    }
}
