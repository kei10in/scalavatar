package com.github.kei10in.scalavatar

import scala.util._

import java.nio.file.{Paths}
import javax.imageio.ImageIO
import java.awt.image.BufferedImage

import org.scalatra._
import org.scalatra.servlet.FileItem

import org.apache.commons.validator.routines.EmailValidator

import model._


class ScalavatarServlet extends ScalavatarStack with UrlGeneratorSupport {

  lazy val app = new Application(Paths.get(servletContext.getRealPath("/")))

  get("/") {
    contentType = "text/html"
    jade("/index")
  }

  val avatarUrl = get("/avatar/:avatarRequest") {
    val avatarKey = parseAvatarRequest(params("avatarRequest"))
    val d = params.get("default").orElse(params.get("d"))
    val s = parseAvatarSize(params.get("size").orElse(params.get("s")))

    app.findImageByHash(avatarKey, d) match {
      case Some(avatar) =>
        app.avatarImageBytesWithSize(avatar, s).map { bytes =>
          contentType = "image/png"
          response.setHeader("Content-Disposition", "inline; filename=\"" + avatarKey + ".png\"")
          Ok(bytes)
        } getOrElse {
          BadRequest("400 Bad Request: invalid url was provided")
        }
      case None =>
        NotFound("404 Not Found")
    }
  }

  def parseAvatarRequest(avatarRequest: String) = {
    if (avatarRequest.contains('.'))
      avatarRequest.take(avatarRequest.indexOf('.'))
    else
      avatarRequest
  }

  def parseAvatarSize(s: Option[String]) = s map { v =>
      Try(Integer.parseInt(v)).getOrElse(1)  // 1 for gravatar compatible
    }

  get("/search") {
    val email = params("e-mail")

    contentType = "text/html"
    jade("/search", "imageSource" -> avatarSourceFor(email))
  }

  def avatarSourceFor(email: String) = {
    app.findImageByEmail(email).flatMap { _ =>
      Some(url(avatarUrl, "avatarRequest" -> app.avatarHashFor(email)))
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
