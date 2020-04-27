package com.softwaremill.ex3

import akka.actor.typed.scaladsl.{ActorContext, Behaviors}
import akka.actor.typed.{ActorRef, Behavior}
import com.softwaremill.ex2.FileManager
import com.softwaremill.ex3.FileUploaderBehavior.{
  waitingForStart,
  Command,
  FileUploadResponse,
  FileUploaded,
  FinishUploading,
  GetStatus,
  UploadFile,
  UploadingInProgress,
  UploadingNotStarted
}

import scala.util.{Failure, Success}

class FileUploaderBehavior(fileManager: FileManager, context: ActorContext[Command]) {

  def init(): Behaviors.Receive[Command] = Behaviors.receiveMessagePartial {
    case UploadFile(replyTo) =>
      startUpload(context, replyTo, fileManager)
    case GetStatus(replyTo) =>
      replyTo ! UploadingNotStarted
      Behaviors.same
  }

  private def startUpload(context: ActorContext[Command], replyTo: ActorRef[FileUploadResponse], fileManager: FileManager) = {
    context.pipeToSelf(fileManager.startUpload()) {
      case Success(_)         => FinishUploading
      case Failure(exception) => throw exception //some error handling here
    }
    uploadingInProgress(replyTo, fileManager)
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
        waitingForStart(fileManager)
      case UploadFile(replyTo) =>
        startUpload(context, replyTo, fileManager)
    }
  }

}

object FileUploaderBehavior {
  sealed trait Command
  case class UploadFile(replyTo: ActorRef[Response]) extends Command
  case class GetStatus(replyTo: ActorRef[Response])  extends Command
  case object FinishUploading                        extends Command

  sealed trait Response
  sealed trait FileUploadResponse extends Response
  case object UploadingInProgress extends FileUploadResponse
  case object FileUploaded        extends FileUploadResponse
  case object UploadingNotStarted extends Response

  def apply(fileManager: FileManager): Behavior[Command] = waitingForStart(fileManager)

  private def waitingForStart(fileManager: FileManager): Behavior[Command] = Behaviors.setup { context =>
    new FileUploaderBehavior(fileManager, context).init()
  }
}
