package com.softwaremill.ex2

import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.Behaviors

import scala.concurrent.ExecutionContext.Implicits.global

object FileUploaderBehavior {
  case class UploadFile(replyTo: ActorRef[FileUploaded])
  case class FileUploaded()

  def uploaderBehavior(fileManager: FileManager): Behavior[UploadFile] = Behaviors.receiveMessage {
    case UploadFile(replyTo: ActorRef[FileUploaded]) =>
      fileManager.startUpload().foreach(_ => replyTo ! FileUploaded())
      Behaviors.same
  }
}
