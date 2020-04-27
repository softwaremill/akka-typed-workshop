package com.softwaremill.ex3

import akka.actor.typed.scaladsl.{ActorContext, Behaviors}
import akka.actor.typed.{ActorRef, Behavior}
import com.softwaremill.ex2.FileManager

import scala.util.{Failure, Success}

object FileUploaderBehavior {
  sealed trait Command
  case class UploadFile(replyTo: ActorRef[FileUploadResponse]) extends Command
  case class GetStatus(replyTo: ActorRef[Response])  extends Command
  private case object FinishUploading                extends Command

  sealed trait Response
  sealed trait FileUploadResponse extends Response
  case object UploadingInProgress extends FileUploadResponse
  case object FileUploaded        extends FileUploadResponse
  case object UploadingNotStarted extends Response

  def waitingForStart(fileManager: FileManager): Behavior[Command] = Behaviors.setup { context =>
    Behaviors.receiveMessage {
      case UploadFile(replyTo) =>
        startUpload(fileManager, context)
        uploadingInProgress(replyTo, fileManager)
      case GetStatus(replyTo) =>
        replyTo ! UploadingNotStarted
        Behaviors.same
      case FinishUploading =>
        Behaviors.ignore
    }
  }

  private def uploadingInProgress(replyTo: ActorRef[FileUploadResponse], fileManager: FileManager): Behavior[Command] = Behaviors.receiveMessage {
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

  private def uploadingFinished(fileManager: FileManager): Behavior[Command] = Behaviors.setup { context =>
    Behaviors.receiveMessagePartial {
      case GetStatus(replyTo) =>
        replyTo ! FileUploaded
        Behaviors.same
      case UploadFile(replyTo) =>
        startUpload(fileManager, context)
        uploadingInProgress(replyTo, fileManager)
    }
  }

  private def startUpload(fileManager: FileManager, context: ActorContext[Command]): Unit = {
    context.pipeToSelf(fileManager.startUpload()) {
      case Success(_)         => FinishUploading
      case Failure(exception) => throw exception //some error handling here
    }
  }
}
