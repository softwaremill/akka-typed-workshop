package com.softwaremill.ex1

import akka.actor.typed.Behavior

object SquareRootCalculator {
  def apply(): Behavior[Calculate] = ???

  case class Calculate() //TODO add required fields

  sealed trait SqrtResult

  object SqrtResult {
    case class Error(message: String) extends SqrtResult
    case class Result(value: Double)  extends SqrtResult
  }
}
