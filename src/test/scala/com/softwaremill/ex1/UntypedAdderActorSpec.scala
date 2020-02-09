package com.softwaremill.ex1

import akka.actor.{ActorRef, ActorSystem}
import akka.testkit.{ImplicitSender, TestKit, TestProbe}
import com.softwaremill.ex1.UntypedAdderActor.{Add, Result}
import org.scalatest.BeforeAndAfterAll
import org.scalatest.flatspec.AnyFlatSpecLike
import org.scalatest.matchers.should.Matchers

class UntypedAdderActorSpec
    extends TestKit(ActorSystem("untyped-actor-system"))
    with ImplicitSender
    with AnyFlatSpecLike
    with Matchers
    with BeforeAndAfterAll {

  behavior of "UntypedAdderActor"

  it should "add two integers" in {
    // given
    val adder: ActorRef = system.actorOf(UntypedAdderActor.props())
    val probe: TestProbe = TestProbe()

    // when
    adder.tell(Add(2, 3), probe.ref)

    // then
    probe.expectMsg[Result](Result(5))
  }

  override def afterAll: Unit = {
    TestKit.shutdownActorSystem(system)
  }
}
