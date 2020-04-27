package com.softwaremill.ex2

import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.Behaviors



object FileUploaderBehavior {
  case class UploadFile(replyTo: ActorRef[FileUploaded])
  case class FileUploaded()

  def uploaderBehavior(fileManager: FileManager): Behavior[UploadFile] = Behaviors.receiveMessage {
    case UploadFile(replyTo) =>
      replyTo ! FileUploaded()
      Behaviors.same
  }
}
