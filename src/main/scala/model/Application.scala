package com.github.kei10in.scalavatar.model

import scala.math._

import java.io.{ByteArrayOutputStream}
import java.nio.file.{Files, Path, Paths}
import java.security.MessageDigest
import java.nio.charset.StandardCharsets._
import java.net.{URL, URLDecoder}
import javax.imageio._
import java.awt.image.BufferedImage


class Application(home: Path, defaultImagePath: Path) {
  if (Files.notExists((home)))
    Files.createDirectory(home)

  val avatarsDir = home.resolve("avatars")
  val minSize = 1
  val maxSize = 2048
  val defaultSize = 80
  lazy val defaultAvatar = Avatar.fromPath(defaultImagePath)

  def findImageByHash(hash: String, default: Option[String]=None) : Option[Avatar] = {
    val dir = avatarsDir.resolve(hash.take(2))
    val filepath = dir.resolve(hash.drop(2))
    if (Files.exists(filepath))
      Some(Avatar.fromPath(filepath))
    else
      default match {
        case Some("404") => None
        case Some(url) => Some(Avatar.fromUrl(new URL(URLDecoder.decode(url, UTF_8.toString))))
        case None => Some(defaultAvatar)
      }
  }

  def findImageByEmail(email: String) = findImageByHash(avatarHashFor(email))

  def avatarImageBytesWithSize(avatar: Avatar, s: Option[Int]) = {
    val size = validateSize(s)

    avatar.imageWithSize(size).map { img =>
      val os = new ByteArrayOutputStream()
      ImageIO.write(img, "png", os)
      os.flush()
      val bytes = os.toByteArray()
      os.close()
      bytes
    }
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

    val path = home.resolve("avatars")
    if (Files.notExists(path))
      Files.createDirectory(path)

    val dir = path.resolve(avatarHash.take(2))
    if (Files.notExists(dir))
      Files.createDirectory(dir)

    val filepath = dir.resolve(avatarHash.drop(2))

    val len = min(img.getHeight(), img.getWidth())

    val x: Int = (img.getWidth - len) / 2;
    val y : Int = (img.getHeight() - len) / 2;
    val subimg = img.getSubimage(x, y, len, len)

    ImageIO.write(subimg, "png", filepath.toFile)
  }

  def avatarHashFor(email: String): String = {
    toMD5String(email.trim().toLowerCase())
  }

  private def toMD5String(s: String): String = {
    val digestBytes = MessageDigest.getInstance("MD5").digest(s.getBytes(UTF_8))

    digestBytes.map("%02x".format(_)).mkString
  }

}
