package com.softwaremill.ex3

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import com.softwaremill.ex2.FileManager
import com.softwaremill.ex3.FileUploaderBehavior.{Command, FinishUploading, GetStatus, Response, UploadFile}

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

  def waitingForStart(fileManager: FileManager): Behavior[Command] =
    new FileUploaderBehavior(fileManager).waitingForStart(false)
}

class FileUploaderBehavior(fileManager: FileManager) {
  import FileUploaderBehavior._

  def waitingForStart(uploadedSomething: Boolean): Behavior[Command] = Behaviors.setup { context =>
    Behaviors.receiveMessage {
      case UploadFile(replyTo) =>
        context.pipeToSelf(fileManager.startUpload()) {
          case Success(_)         => FinishUploading
          case Failure(exception) => throw exception //some error handling here
        }
        uploadingInProgress(replyTo)
      case GetStatus(replyTo) =>
        replyTo ! (if (uploadedSomething) FileUploaded else UploadingNotStarted)
        Behaviors.same
    }
  }

  def uploadingInProgress(replyTo: ActorRef[Response]): Behavior[Command] = Behaviors.receiveMessage {
    case FinishUploading =>
      replyTo ! FileUploaded
      waitingForStart(true)
    case UploadFile(replyTo) =>
      replyTo ! UploadingInProgress
      Behaviors.same
    case GetStatus(replyTo) =>
      replyTo ! UploadingInProgress
      Behaviors.same
  }
}
