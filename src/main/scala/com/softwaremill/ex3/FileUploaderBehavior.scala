package com.softwaremill.ex3

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import com.softwaremill.ex2.FileManager

import scala.util.{Failure, Success}

object FileUploaderBehavior {

  sealed trait Command

  case class UploadFile(replyTo: ActorRef[FileUploaderResponse]) extends Command

  case class GetStatus(replyTo: ActorRef[Response]) extends Command

  private case object FinishUploading extends Command

  sealed trait Response

  sealed trait FileUploaderResponse extends Response

  case object UploadingInProgress extends FileUploaderResponse

  case object FileUploaded extends FileUploaderResponse

  case object UploadingNotStarted extends Response

  def apply(fileManager: FileManager): Behavior[Command] = Behaviors.setup { context =>
    def uploadingInProgress(replyTo: ActorRef[FileUploaderResponse]): Behavior[Command] = Behaviors.receiveMessage {
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

    def uploadingFinished(): Behavior[Command] =
      Behaviors.setup { context =>
        Behaviors.receiveMessagePartial {
          case UploadFile(replyTo) =>
            startUpload()
            uploadingInProgress(replyTo)
          case GetStatus(replyTo) =>
            replyTo ! FileUploaded
            Behaviors.same
        }
      }


    def startUpload() = {
      context.pipeToSelf(fileManager.startUpload()) {
        case Success(_) => FinishUploading
        case Failure(exception) => throw exception //some error handling here
      }
    }


    Behaviors.receiveMessagePartial {
      case UploadFile(replyTo) =>
        startUpload()
        uploadingInProgress(replyTo)
      case GetStatus(replyTo) =>
        replyTo ! UploadingNotStarted
        Behaviors.same
    }
  }
}
