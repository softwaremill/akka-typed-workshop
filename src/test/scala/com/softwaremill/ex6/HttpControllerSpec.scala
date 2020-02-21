package com.softwaremill.ex6

import akka.actor.testkit.typed.scaladsl.LoggingTestKit
import akka.actor.typed.{ActorSystem, SpawnProtocol}
import com.softwaremill.ex6.HttpController.{Request, Response}
import org.scalatest.BeforeAndAfterAll
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.flatspec.AnyFlatSpecLike
import org.scalatest.matchers.should.Matchers

class HttpControllerSpec extends AnyFlatSpecLike with Matchers with ScalaFutures with BeforeAndAfterAll {
  implicit private val system: ActorSystem[SpawnProtocol.Command] = ActorSystem(SpawnActorSystem(), "test-as")
  private val httpController                                      = new HttpController(system)

  "HttpController" should "calculate result" in {
    //when
    val result = httpController.process(Request(1, 2)).futureValue

    //then
    result shouldBe Response(3)
  }

  it should "create and stop actor per request" in {
    LoggingTestKit
      .info("My job is done, releasing resources.")
      .withOccurrences(3)
      .expect {
        httpController.process(Request(1, 2)).futureValue shouldBe Response(3)
        httpController.process(Request(2, 2)).futureValue shouldBe Response(4)
        httpController.process(Request(3, 2)).futureValue shouldBe Response(5)
      }
  }

  override def afterAll(): Unit = {
    system.terminate()
  }
}
