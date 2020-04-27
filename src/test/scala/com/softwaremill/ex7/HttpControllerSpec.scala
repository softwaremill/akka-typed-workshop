package com.softwaremill.ex7

import akka.actor.typed.ActorSystem
import akka.actor.typed.receptionist.Receptionist
import akka.actor.typed.scaladsl.Behaviors
import com.softwaremill.ex7.HttpController.{Request, Response}
import org.scalatest.BeforeAndAfterAll
import org.scalatest.concurrent.{Eventually, ScalaFutures}
import org.scalatest.flatspec.AnyFlatSpecLike
import org.scalatest.matchers.should.Matchers

class HttpControllerSpec extends AnyFlatSpecLike with Matchers with ScalaFutures with BeforeAndAfterAll with Eventually {

  implicit private val system: ActorSystem[Nothing] = ActorSystem(Behaviors.setup[Unit] { ctx =>
    val adder = ctx.spawn(Adder(), "adder")
    ctx.system.receptionist ! Receptionist.Register(Adder.AdderKey, adder)
    Behaviors.empty
  }, "test-as")
  private val httpController = new HttpController(system)
  behavior of "HttpController"

  it should "calculate result" in {
    //when then
    eventually {
      httpController.process(Request(1, 2)).futureValue shouldBe Response(3)
      httpController.process(Request(2, 2)).futureValue shouldBe Response(4)
      httpController.process(Request(3, 2)).futureValue shouldBe Response(5)
    }
  }

  override def afterAll(): Unit = {
    system.terminate()
  }
}
