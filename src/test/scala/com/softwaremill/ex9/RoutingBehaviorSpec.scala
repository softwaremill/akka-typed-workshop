package com.softwaremill.ex9

import akka.actor.testkit.typed.scaladsl.{LoggingTestKit, ScalaTestWithActorTestKit}
import com.softwaremill.ex9.RoutingBehavior.SendMessage
import org.scalatest.flatspec.AnyFlatSpecLike

class RoutingBehaviorSpec extends ScalaTestWithActorTestKit with AnyFlatSpecLike {
  behavior of "RoutingBehavior"

  it should "route message when routing config is empty" in {
    // given
    val configurationRepository = new RoutingConfigurationRepository
    val originalDestination     = "destination"
    val router                  = testKit.spawn(RoutingBehavior(configurationRepository))

    // when // then
    LoggingTestKit.info(s"Sending message to $originalDestination").withOccurrences(3).expect {
      router ! SendMessage(originalDestination, "payload1")
      router ! SendMessage(originalDestination, "payload1")
      router ! SendMessage(originalDestination, "payload1")
    }
  }

  it should "route message to new destination" in {
    // given
    val configurationRepository = new RoutingConfigurationRepository
    val originalDestination     = "destination"
    val newDestination          = "newDestination"
    configurationRepository.updateConfig(originalDestination, newDestination).futureValue
    val router = testKit.spawn(RoutingBehavior(configurationRepository))

    // when // then
    LoggingTestKit.info(s"Sending message to $newDestination").withOccurrences(3).expect {
      router ! SendMessage(originalDestination, "payload1")
      router ! SendMessage(originalDestination, "payload1")
      router ! SendMessage(originalDestination, "payload1")
    }
  }

  it should "reload routing config" in {
    // given
    val configurationRepository = new RoutingConfigurationRepository
    val originalDestination     = "destination"
    val newDestination          = "newDestination"
    configurationRepository.updateConfig(originalDestination, newDestination).futureValue
    val router = testKit.spawn(RoutingBehavior(configurationRepository))

    // when // then
    LoggingTestKit.info(s"Sending message to $newDestination").withOccurrences(3).expect {
      router ! SendMessage(originalDestination, "payload1")
      router ! SendMessage(originalDestination, "payload1")
      router ! SendMessage(originalDestination, "payload1")
    }

    // given
    val updatedDestination = "updatedDestination"
    configurationRepository.updateConfig(originalDestination, updatedDestination).futureValue

    // when // then
    LoggingTestKit.info(s"Sending message to $updatedDestination").withOccurrences(3).expect {
      //TODO
      router ! SendMessage(originalDestination, "payload1")
      router ! SendMessage(originalDestination, "payload1")
      router ! SendMessage(originalDestination, "payload1")
    }
  }
}
