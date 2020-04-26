package com.softwaremill.ex10

import akka.actor.testkit.typed.scaladsl.ScalaTestWithActorTestKit
import org.scalatest.flatspec.AnyFlatSpecLike

class DispatchersSpec extends ScalaTestWithActorTestKit with AnyFlatSpecLike {
  behavior of "Dispatchers"

  it should "Run actors without thread starvation" in {
    for (i <- 1 to 100) {
      testKit.spawn(SlowBlockingActor(), s"blockingSlowActor-$i") ! i
      testKit.spawn(InnocentActor(), s"innocentActor-$i") ! i
    }
  }
}
