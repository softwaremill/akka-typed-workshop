package com.softwaremill.ex2

import akka.actor.Actor
import akka.pattern.pipe
import com.softwaremill.ex2.FileUploaderActor.{FileUploaded, UploadFile}

import scala.concurrent.ExecutionContext.Implicits.global

class FileUploaderActor(fileManager: FileManager) extends Actor {

  override def receive: Receive = {
    case UploadFile =>
      fileManager.startUpload().map { _ =>
        FileUploaded //ERROR invoking sender() method in Future callback
      }.pipeTo(sender())

//      corrected version
//      val currentSender = sender()
//      fileManager.startUpload().map { _ =>
//        currentSender ! FileUploaded
//      }

//      corrected version with pipe pattern
//      fileManager.startUpload().map(_ => FileUploaded).pipeTo(sender())
  }
}

object FileUploaderActor {
  case object UploadFile

  case object FileUploaded
}
