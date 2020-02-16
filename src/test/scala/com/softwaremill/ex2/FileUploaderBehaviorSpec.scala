package com.softwaremill.ex2

import akka.actor.testkit.typed.scaladsl.ScalaTestWithActorTestKit
import com.softwaremill.ex2.FileUploaderBehavior.{FileUploaded, UploadFile}
import org.scalatest.flatspec.AnyFlatSpecLike

class FileUploaderBehaviorSpec extends ScalaTestWithActorTestKit with AnyFlatSpecLike {
  behavior of "FileUploader"

  private val fileManager = new FileManager

  it should "respond after uploading file" in {
    // given
    val uploader = testKit.spawn(FileUploaderBehavior.uploaderBehavior(fileManager))
    val probe    = createTestProbe[FileUploaded]()

    // when
    uploader ! UploadFile(probe.ref)

    // then
    probe.expectMessageType[FileUploaded]
  }
}
