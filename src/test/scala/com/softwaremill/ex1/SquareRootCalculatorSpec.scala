package com.softwaremill.ex1

import akka.actor.testkit.typed.scaladsl.{LoggingTestKit, ScalaTestWithActorTestKit, TestProbe}
import com.softwaremill.ex1.SquareRootCalculator.{Calculate, SqrtResult}
import org.scalatest.flatspec.AnyFlatSpecLike

class SquareRootCalculatorSpec extends ScalaTestWithActorTestKit with AnyFlatSpecLike {
  behavior of "SquareRootCalculator"

  it should "calculate sqrt for positive integer" in {
    // given
    val calculator                   = ???
    val probe: TestProbe[SqrtResult] = ???

    // when
    calculator //TODO

    // then
    probe.expectMessage(SqrtResult.Result(2))
  }

  it should "return error for negative integer" in {
    // given
    val calculator                   = ???
    val probe: TestProbe[SqrtResult] = ???

    // when
    calculator //TODO

    // then
    probe.expectMessageType[SqrtResult.Error]
  }

  it should "log message warn message in case of negative integer" in {
    // given
    val calculator = ???
    val probe      = ???

    // when then
    LoggingTestKit.warn("Negative integer!").expect {
      calculator //TODO
    }
  }
}
