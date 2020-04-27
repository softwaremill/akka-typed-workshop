package com.softwaremill.ex3

import akka.actor.typed.scaladsl.{ActorContext, Behaviors}
import akka.actor.typed.{ActorRef, Behavior}
import com.softwaremill.ex2.FileManager

import scala.util.{Failure, Success}

object FileUploaderBehavior {
  sealed trait Command
  case class UploadFile(replyTo: ActorRef[Response]) extends Command
  case class GetStatus(replyTo: ActorRef[Response])  extends Command
  case object FinishUploading                        extends Command

  sealed trait Response
  case object UploadingInProgress extends Response
  case object FileUploaded        extends Response
  case object UploadingNotStarted extends Response

  def waitingForStart(fileManager: FileManager): Behavior[Command] = Behaviors.setup { context =>
    Behaviors.receiveMessage {
      case UploadFile(replyTo) =>
        startUpload(context, replyTo, fileManager)
      case GetStatus(replyTo) =>
        replyTo ! UploadingNotStarted
        Behaviors.same
    }
  }

  private def startUpload(context: ActorContext[Command], replyTo: ActorRef[Response], fileManager: FileManager) = {
    context.pipeToSelf(fileManager.startUpload()) {
      case Success(_) => FinishUploading
      case Failure(exception) => throw exception //some error handling here
    }
    uploadingInProgress(replyTo, fileManager)
  }

  def uploadingInProgress(replyTo: ActorRef[Response], fileManager: FileManager): Behavior[Command] = Behaviors.receiveMessage {
    case FinishUploading =>
      replyTo ! FileUploaded
      uploadingFinished(fileManager)
    case UploadFile(replyTo) =>
      replyTo ! UploadingInProgress
      Behaviors.same
    case GetStatus(replyTo) =>
      replyTo ! UploadingInProgress
      Behaviors.same
  }

  def uploadingFinished(fileManager: FileManager): Behavior[Command] = Behaviors.setup { context =>
    Behaviors.receiveMessage {
      case GetStatus(replyTo) =>
        replyTo ! FileUploaded
        waitingForStart(fileManager)
      case UploadFile(replyTo) =>
        startUpload(context, replyTo, fileManager)
    }
  }
}
