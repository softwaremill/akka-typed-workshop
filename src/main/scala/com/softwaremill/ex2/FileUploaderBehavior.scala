package com.softwaremill.ex2

import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.Behaviors

import scala.concurrent.ExecutionContext.Implicits.global

object FileUploaderBehavior {
  case class UploadFile(replyTo: ActorRef[FileUploaded])
  case class FileUploaded()

  def uploaderBehavior(fileManager: FileManager): Behavior[UploadFile] = Behaviors.setup{ context =>
    Behaviors.receiveMessage{
      case UploadFile(replyTo) => replyTo ! FileUploaded()
        Behaviors.same
    }


  }

}
