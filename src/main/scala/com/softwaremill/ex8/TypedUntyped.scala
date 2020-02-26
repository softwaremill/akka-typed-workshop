package com.softwaremill.ex8

import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.{Actor, ActorLogging, Props}
import akka.actor.typed.scaladsl.adapter._
import com.softwaremill.ex8.Worker.DoPartialWork

sealed trait Command
case class DoWork(param: Int)                extends Command
case class HandleWorkerResponse(result: Int) extends Command

object TypedUntyped {

  val behavior: Behavior[Command] = Behaviors
    .setup[Command] { context =>
      context.log.info("Parent setup")
      val workerChild = context.actorOf(Worker.props, "worker")
      val workerResponseMapper: ActorRef[Worker.PartialWorkResponse] =
        context.messageAdapter {
          case Worker.PartialWorkResult(param) => HandleWorkerResponse(param)
        }

      Behaviors.receiveMessage {
        case DoWork(param) =>
          workerChild.tell(DoPartialWork(param), workerResponseMapper.toClassic)
          Behaviors.same

        case HandleWorkerResponse(result) =>
          context.log.info(s"Parent received result: $result")
          Behaviors.same
      }
    }
}

object Worker {
  sealed trait WorkerCommand
  case class DoPartialWork(param: Int)

  sealed trait PartialWorkResponse

  case class PartialWorkResult(param: Int) extends PartialWorkResponse

  def props: Props = Props(new WorkerActor())

  class WorkerActor extends Actor with ActorLogging {

    override def receive = {
      case DoPartialWork(param) =>
        sender ! PartialWorkResult(param * 2)
    }

    override def postStop(): Unit = {
      log.info("Stopping worker actor")
      super.postStop()
    }
  }
}
