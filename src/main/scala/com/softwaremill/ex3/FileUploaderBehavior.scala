package com.softwaremill.ex3

import akka.actor.typed.scaladsl.{ActorContext, Behaviors}
import akka.actor.typed.{ActorRef, Behavior}
import com.softwaremill.ex2.FileManager
import com.softwaremill.ex3.FileUploaderBehavior._

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

  def apply(fileManager: FileManager): Behavior[Command] = Behaviors.setup { context =>
    new FileUploaderBehavior(fileManager, context).waitingForStart()
  }
}

class FileUploaderBehavior(fileManager: FileManager, context: ActorContext[Command]) {

  def waitingForStart(): Behavior[Command] = Behaviors.receiveMessage {
      case UploadFile(replyTo) =>
        upload(replyTo)
        uploadingInProgress(replyTo)
      case GetStatus(replyTo) =>
        replyTo ! UploadingNotStarted
        Behaviors.same
      case _ => Behaviors.ignore
  }

  private def uploadingInProgress(replyTo: ActorRef[FileUploadResponse]): Behavior[Command] = Behaviors.receiveMessage {
    case FinishUploading =>
      replyTo ! FileUploaded
      uploadingFinished()
    case UploadFile(replyTo) =>
      replyTo ! UploadingInProgress
      Behaviors.same
    case GetStatus(replyTo) =>
      replyTo ! UploadingInProgress
      Behaviors.same
  }

  private def uploadingFinished(): Behavior[Command] = Behaviors.receiveMessagePartial {
      case UploadFile(replyTo) =>
        upload(replyTo)
        uploadingInProgress(replyTo)
      case GetStatus(replyTo) =>
        replyTo ! FileUploaded
        Behaviors.same
    }

  private def upload(replyTo: ActorRef[FileUploadResponse]) = {
    context.pipeToSelf(fileManager.startUpload()) {
      case Success(_) => FinishUploading
      case Failure(exception) => throw exception //some error handling here
    }
  }
}
