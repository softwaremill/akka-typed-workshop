package com.softwaremill.ex1

import akka.actor.{Actor, Props}
import com.softwaremill.ex1.UntypedAdderActor.{Add, Result}

class UntypedAdderActor extends Actor {
  override def receive: Receive = {
    case Add(a, b) =>
      sender() ! Result(a + b)
  }
}

object UntypedAdderActor {

  def props(): Props = Props(new UntypedAdderActor)

  case class Add(a: Int, b: Int)
  case class Result(value: Int)
}
