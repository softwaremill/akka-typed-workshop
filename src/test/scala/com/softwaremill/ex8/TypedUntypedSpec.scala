package com.softwaremill.ex8

import akka.actor.testkit.typed.scaladsl.{LoggingTestKit, ScalaTestWithActorTestKit}
import org.scalatest.concurrent.Eventually
import org.scalatest.flatspec.AnyFlatSpecLike

class TypedUntypedSpec extends ScalaTestWithActorTestKit with AnyFlatSpecLike with Eventually {
  behavior of "TypedUntyped"

  it should "supervise untyped child actor" in {
    val actor = testKit.spawn(TypedUntyped.behavior)
    LoggingTestKit.info("Stopping worker actor").expect {
      testKit.stop(actor)
    }
  }
}
