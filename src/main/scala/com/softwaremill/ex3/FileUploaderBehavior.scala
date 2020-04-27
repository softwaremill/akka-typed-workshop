package com.softwaremill.ex3

import akka.actor.typed.scaladsl.{ActorContext, Behaviors}
import akka.actor.typed.{ActorRef, Behavior}
import com.softwaremill.ex2.FileManager

import scala.util.{Failure, Success}

object FileUploaderBehavior {

  sealed trait Command

  case class UploadFile(replyTo: ActorRef[Response]) extends Command
  case class GetStatus(replyTo: ActorRef[Response]) extends Command
  private case object FinishUploading extends Command

  sealed trait Response

  case object UploadingInProgress extends Response
  case object FileUploaded extends Response
  case object UploadingNotStarted extends Response

  def waitingForStart(fileManager: FileManager): Behavior[Command] = Behaviors.setup { context =>
    Behaviors.receiveMessage {
      case UploadFile(replyTo) =>
        uploadAndTransitStateToInProgress(fileManager, context, replyTo)
      case GetStatus(replyTo) =>
        replyTo ! UploadingNotStarted
        Behaviors.same
      case otherMsg =>
        context.log.warn("Received unexpected msg in state waitingForStart: " + otherMsg)
        Behaviors.ignore
    }
  }

  private def uploadingInProgress(replyTo: ActorRef[Response], fileManager: FileManager): Behavior[Command] = Behaviors.setup { context =>
    Behaviors.receiveMessage {
      case FinishUploading =>
        replyTo ! FileUploaded
        uploadingFinished(fileManager)
      case UploadFile(replyTo) =>
        replyTo ! UploadingInProgress
        Behaviors.same
      case GetStatus(replyTo) =>
        replyTo ! UploadingInProgress
        Behaviors.same
      case otherMsg =>
        context.log.warn("Received unexpected msg in state uploadingInProgress: " + otherMsg)
        Behaviors.ignore
    }
  }

  private def uploadingFinished(fileManager: FileManager): Behavior[Command] = Behaviors.setup { context =>
    Behaviors.receiveMessage {
      case GetStatus(replyTo) =>
        replyTo ! FileUploaded
        Behaviors.same
      case UploadFile(replyTo) =>
        uploadAndTransitStateToInProgress(fileManager, context, replyTo)
      case otherMsg =>
        context.log.warn("Received unexpected msg in state uploadingFinished: " + otherMsg)
        Behaviors.ignore
    }
  }

  private def uploadAndTransitStateToInProgress(fileManager: FileManager, context: ActorContext[Command], replyTo: ActorRef[Response]) = {
    context.pipeToSelf(fileManager.startUpload()) {
      case Success(_) => FinishUploading
      case Failure(exception) => throw exception //some error handling here
    }
    uploadingInProgress(replyTo, fileManager)
  }
}
