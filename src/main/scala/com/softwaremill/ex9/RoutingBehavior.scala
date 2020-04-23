package com.softwaremill.ex9

import akka.actor.typed.scaladsl.{ActorContext, Behaviors, StashBuffer}
import akka.actor.typed.{scaladsl, Behavior}
import com.softwaremill.ex9.RoutingBehavior.{ConfigLoaded, ConfigLoadingFailed, RoutingCommand, SendMessage}

import scala.util.{Failure, Success}

object RoutingBehavior {
  trait RoutingCommand
  case class SendMessage(destination: String, payload: String) extends RoutingCommand
  case class ConfigLoaded(config: Map[String, String])         extends RoutingCommand
  case class ConfigLoadingFailed(cause: Throwable)             extends RoutingCommand

  def apply(routingConfigurationRepository: RoutingConfigurationRepository): Behavior[RoutingCommand] =
    Behaviors.withStash(100) { buffer =>
      Behaviors.setup[RoutingCommand] { context: scaladsl.ActorContext[RoutingCommand] =>
        new RoutingBehavior(context, buffer, routingConfigurationRepository).init()
      }
    }
}

class RoutingBehavior(
    context: ActorContext[RoutingCommand],
    buffer: StashBuffer[RoutingCommand],
    routingConfigurationRepository: RoutingConfigurationRepository
) {
  private val log = context.log

  def init(): Behavior[RoutingCommand] = {
    context.pipeToSelf(routingConfigurationRepository.getConfig()) {
      case Success(config) => ConfigLoaded(config)
      case Failure(cause)  => ConfigLoadingFailed(cause)
    }

    Behaviors.receiveMessage[RoutingCommand] {
      case ConfigLoaded(config) =>
        log.info("Config loaded {}, unstashing.", config)
        buffer.unstashAll(startRouting(config))
      case ConfigLoadingFailed(cause) => throw cause
      case msg: SendMessage =>
        log.info("Stashing: {}, waiting for configuration", msg)
        buffer.stash(msg)
        Behaviors.same
    }
  }

  def startRouting(config: Map[String, String]): Behavior[RoutingCommand] = {
    Behaviors.receiveMessage[RoutingCommand] {
      case SendMessage(destination, _) =>
        config.get(destination) match {
          case Some(routingDestination) => log.info("Sending message to {}", routingDestination)
          case None                     => log.info("Sending message to {}", destination)
        }
        Behaviors.same
    }
  }
}
