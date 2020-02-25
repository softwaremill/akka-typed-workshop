package com.softwaremill.ex5

import akka.actor.testkit.typed.scaladsl.{LoggingTestKit, ScalaTestWithActorTestKit}
import org.scalatest.concurrent.Eventually
import org.scalatest.flatspec.AnyFlatSpecLike

class TimerBehaviorSpec extends ScalaTestWithActorTestKit with AnyFlatSpecLike with Eventually {
  behavior of "TimerBehavior"

  it should "send periodic self messages" in {
    LoggingTestKit
      .info("Self ping received!")
      .withOccurrences(3)
      .expect {
        testKit.spawn(TimerBehavior.behavior, "timer1")
      }
  }

  it should "cancel and override self message with same key" in {
    LoggingTestKit
      .info("Self ping received (exercise 5-2)!")
      .withOccurrences(4)
      .expect {
        testKit.spawn(TimerBehavior.behavior2, "timer2")
      }
  }
}
