package com.github.kei10in.model

import scala.math._

import java.io.File
import java.security.MessageDigest
import java.nio.charset.StandardCharsets._
import javax.imageio._

import org.scalatra.servlet.FileItem


class Application(workingDirectory: File) {

  val avatarsDir = new File(workingDirectory, "/avatars")

  def findImageByHash(hash: String) = {
    val dir = new File(avatarsDir, hash.take(2))
    new File(dir, hash.drop(2))
  }

  def updateImage(email: String, imageFile: FileItem) = {
    val avatarHash = avatarHashFor(email)

    val path = new File(workingDirectory, "/avatars")
    if (!path.exists())
      path.mkdir()

    val dir = new File(path, avatarHash.take(2))
    if (!dir.exists())
      dir.mkdir()

    val filepath = new File(dir, avatarHash.drop(2))

    val img = ImageIO.read(imageFile.getInputStream)

    val len = min(img.getHeight(), img.getWidth())

    val x: Int = (img.getWidth - len) / 2;
    val y : Int = (img.getHeight() - len) / 2;
    val subimg = img.getSubimage(x, y, len, len)

    ImageIO.write(subimg, "png", filepath)
  }

  def avatarHashFor(email: String): String = {
    toMD5String(email.trim().toLowerCase())
  }

  private def toMD5String(s: String): String = {
    val digestBytes = MessageDigest.getInstance("MD5").digest(s.getBytes(UTF_8))

    digestBytes.map("%02x".format(_)).mkString
  }

}
