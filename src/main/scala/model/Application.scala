package com.github.kei10in.model

import java.io.File
import java.security.MessageDigest
import java.nio.charset.StandardCharsets._

import org.scalatra.servlet.FileItem


class Application(workingDirectory: File) {

  def updateImage(email: String, imageFile: FileItem) = {
    val avatarHash = toMD5String(email.trim().toLowerCase())

    val path = new File(workingDirectory, "/avatars")
    if (!path.exists())
      path.mkdir()

    val dir = new File(path, avatarHash.take(2))
    if (!dir.exists())
      dir.mkdir()

    val filepath = new File(dir, avatarHash.drop(2))
    imageFile.write(filepath)
  }

  private def toMD5String(s: String): String = {
    val digestBytes = MessageDigest.getInstance("MD5").digest(s.getBytes(UTF_8))

    digestBytes.map("%02x".format(_)).mkString
  }

}
