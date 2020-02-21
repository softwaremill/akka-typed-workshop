package com.softwaremill.ex6

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{Behavior, SpawnProtocol}

object SpawnActorSystem {

  def apply(): Behavior[SpawnProtocol.Command] =
    Behaviors.setup { ctx =>
      //spawning other child actors here
      SpawnProtocol()
    }
}
