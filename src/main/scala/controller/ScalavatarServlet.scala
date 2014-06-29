package com.github.kei10in

import scala.util._

import java.io.{ByteArrayOutputStream, File}
import javax.imageio.ImageIO
import java.awt.image.BufferedImage

import org.scalatra._
import org.scalatra.servlet.FileItem

import org.apache.commons.validator.routines.EmailValidator

import model._


class ScalavatarServlet extends ScalavatarStack with UrlGeneratorSupport {

  lazy val app = new Application(new File(servletContext.getRealPath("/")))
  val minSize = 1
  val maxSize = 2048
  val defaultSize = 80

  get("/") {
    contentType = "text/html"
    jade("/index")
  }

  val avatarUrl = get("/avatar/:avatarRequest") {
    val avatarKey = parseAvatarRequest(params("avatarRequest"))
    val s = params.get("s") match {
      case Some(v) =>
        Try(Integer.parseInt(v)) match {
          case Success(size) =>
            if (size <= minSize) minSize
            else if (size <= maxSize) size
            else maxSize
          case Failure(_) => 1  // for gravatar compatible
        }
      case None => defaultSize
    }
    app.findImageByHash (avatarKey) match {
      case Some (avatar) =>
        val os = new ByteArrayOutputStream ()
        ImageIO.write (avatar.imageWithSize (s), "png", os)
        os.flush ()
        val bytes = os.toByteArray ()
        os.close ()
        contentType = "image/png"
        Ok (bytes)
      case None =>
        NotFound ("file not found")
    }
  }

  def parseAvatarRequest(avatarRequest: String) = avatarRequest.take(avatarRequest.indexOf('.'))

  get("/search") {
    val email = params("e-mail")

    contentType = "text/html"
    jade("/search", "imageSource" -> avatarSourceFor(email))
  }

  def avatarSourceFor(email: String) = {
    app.findImageByEmail(email).flatMap { _ =>
      Some(url(avatarUrl, "avatarHash" -> app.avatarHashFor(email)))
    }
  }

  get("/avatar") {
    contentType = "text/html"
    jade("/avatar")
  }

  post("/avatar") {
    val email = params("e-mail")
    val file = fileParams("image-file")

    (isInvalidEmail(email), loadFileAsImage(file)) match {
      case (false, Some(img)) =>
        app.updateImage (email, img)
        redirect ("/")
      case (isInvalidEmail, img) =>
        contentType = "text/html"
        BadRequest(jade("/avatar", "isInvalidEmail" -> isInvalidEmail, "isInvalidImageFile" -> img.isEmpty))
    }
  }

  def isInvalidEmail(email: String) = !EmailValidator.getInstance().isValid(email)

  def loadFileAsImage(file: FileItem): Option[BufferedImage] = {
    file.getContentType match {
      case Some("image/png" | "image/jpeg" | "image/gif" | "image/tiff" | "image/bmp") =>
        val img = ImageIO.read(file.getInputStream)
        if (img == null) None
        else Some(img)
      case _ => None
    }
  }

}
