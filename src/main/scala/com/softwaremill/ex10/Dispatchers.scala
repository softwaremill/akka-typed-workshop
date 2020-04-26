package com.softwaremill.ex10

import akka.actor.typed.{Behavior, DispatcherSelector}
import akka.actor.typed.scaladsl.Behaviors

import scala.concurrent.{ExecutionContext, Future}

object InnocentActor {

  def apply(): Behavior[Integer] =
    Behaviors.setup { context =>
      val log = context.log
      Behaviors.receiveMessage { i =>
        log.info(s"Innocent actor received msg: $i")
        Behaviors.same
      }
    }
}

object SlowBlockingActor {

  def apply(): Behavior[Int] =
    Behaviors.setup { context =>
      implicit val executionContext: ExecutionContext =
        context.system.dispatchers.lookup(DispatcherSelector.fromConfig("my-blocking-dispatcher"))

      Behaviors.receiveMessage { i =>
        triggerFutureBlockingOperation(i)
        Behaviors.same
      }
    }

  def triggerFutureBlockingOperation(i: Int)(implicit ec: ExecutionContext): Future[Unit] = {
    println(s"Calling blocking Future: $i")
    Future {
      Thread.sleep(5000)
      println(s"Blocking future finished $i")
    }
  }
}
