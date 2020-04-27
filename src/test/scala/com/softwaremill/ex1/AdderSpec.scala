package com.softwaremill.ex1

import akka.actor.testkit.typed.scaladsl.{ScalaTestWithActorTestKit, TestProbe}
import akka.actor.typed.ActorRef
import com.softwaremill.ex1.Adder.{Add, Result}
import org.scalatest.flatspec.AnyFlatSpecLike

class AdderSpec extends ScalaTestWithActorTestKit with AnyFlatSpecLike {

  behavior of "AdderActor"

  it should "add two integers" in {
    // given
    val adder: ActorRef[Add] = spawn(Adder())
    val probe: TestProbe[Result] = createTestProbe[Result]()

    // when
    adder ! Add(2, 3, probe.ref)

    // then
    probe.expectMessage(Result(5))
  }

  it should "sqrt" in {
    // given
    val x = Math.sqrt(-5)
    x shouldEqual 2
  }
}
