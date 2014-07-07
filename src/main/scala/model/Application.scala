package com.github.kei10in.scalavatar.model

import scala.math._

import java.io.{ByteArrayOutputStream, File}
import java.security.MessageDigest
import java.nio.charset.StandardCharsets._
import javax.imageio._
import java.awt.image.BufferedImage


class Application(workingDirectory: File) {

  val avatarsDir = new File(workingDirectory, "/avatars")
  val minSize = 1
  val maxSize = 2048
  val defaultSize = 80
  lazy val defaultAvatar = new FileAvatar(new File(workingDirectory, "/img/default.png"))

  def findImageByHash(hash: String, default: Option[String]=None) : Option[Avatar] = {
    val dir = new File(avatarsDir, hash.take(2))
    val filepath = new File(dir, hash.drop(2))
    if (filepath.exists())
      Some(Avatar.fromFile(filepath))
    else
      default match {
        case Some("404") => None
        case _ => Some(defaultAvatar)
      }
  }

  def findImageByEmail(email: String) = findImageByHash(avatarHashFor(email))

  def avatarImageBytesWithSize(avatar: Avatar, s: Option[Int]) = {
    val size = validateSize(s)

    val os = new ByteArrayOutputStream ()
    ImageIO.write (avatar.imageWithSize (size), "png", os)
    os.flush ()
    val bytes = os.toByteArray ()
    os.close ()
    bytes
  }

  private def validateSize(s: Option[Int]) = {
    s map { size =>
      if (size <= minSize) minSize
      else if (size <= maxSize) size
      else maxSize
    } getOrElse(defaultSize)
  }

  def updateImage(email: String, img: BufferedImage) = {
    val avatarHash = avatarHashFor(email)

    val path = new File(workingDirectory, "/avatars")
    if (!path.exists())
      path.mkdir()

    val dir = new File(path, avatarHash.take(2))
    if (!dir.exists())
      dir.mkdir()

    val filepath = new File(dir, avatarHash.drop(2))

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
