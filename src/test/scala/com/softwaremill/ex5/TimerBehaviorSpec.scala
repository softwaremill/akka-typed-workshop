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
}
