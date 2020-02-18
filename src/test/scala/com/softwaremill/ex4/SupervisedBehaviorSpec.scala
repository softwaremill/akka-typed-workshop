package com.softwaremill.ex4

import akka.actor.testkit.typed.Effect.Spawned
import akka.actor.testkit.typed.scaladsl.{BehaviorTestKit, LoggingTestKit, ScalaTestWithActorTestKit, TestInbox}
import akka.testkit.TestActor.Spawn
import com.softwaremill.ex4.SupervisedBehavior.{ParentResult, Request, Response}
import org.scalatest.concurrent.Eventually
import org.scalatest.flatspec.AnyFlatSpecLike

class SupervisedBehaviorSpec extends ScalaTestWithActorTestKit with AnyFlatSpecLike with Eventually {
  behavior of "SupervisedBehavior"

  it should "deliver partial work to a child actor" in {
    // given
    val testKit     = BehaviorTestKit(SupervisedBehavior.behavior)
    val parentInbox = TestInbox[SupervisedBehavior.ParentResult]("inbox")

    // when
    testKit.run(Request(3, parentInbox.ref))

    // then
    val child = testKit.expectEffectType[Spawned[Worker.DoPartialWork]]
    testKit.childInbox(child.ref).receiveMessage().param shouldBe (3)
  }

  it should "delegate work to a child actor and return response" in {
    // given
    val actor = testKit.spawn(SupervisedBehavior.behavior, "parent")
    val probe = createTestProbe[ParentResult]()

    // when
    actor ! Request(3, probe.ref)

    // then
    probe.expectMessage(Response(6))
  }

  it should "restart child actor" in {
    // given
    val actor = testKit.spawn(SupervisedBehavior.behavior, "parent2")
    val probe = createTestProbe[ParentResult]()

    // when
    actor ! Request(8, probe.ref)

    // then
    var i = 15
    eventually {
      i = i + 1
      actor ! Request(i, probe.ref)
      probe.expectMessage(Response(46))
    }
  }

  it should "restart child actor together with parent actor" in {
    // given
    val actor = testKit.spawn(SupervisedBehavior.behavior, "parent3")
    val probe = createTestProbe[ParentResult]()

    // when & then
    LoggingTestKit.info("Restarting parent actor").expect {
      actor ! Request(8, probe.ref)
    }
  }
}
