package com.github.kei10in

import java.io.File
import java.security.MessageDigest
import java.nio.charset.StandardCharsets._
import javax.imageio.ImageIO
import java.awt.image.BufferedImage

import org.scalatra._
import org.scalatra.servlet.FileItem
import scalate.ScalateSupport

import org.apache.commons.validator.routines.EmailValidator

import model._


class ScalavatarServlet extends ScalavatarStack with UrlGeneratorSupport {

  lazy val app = new Application(new File(servletContext.getRealPath("/")))

  get("/") {
    contentType = "text/html"
    jade("/index")
  }

  val avatarUrl = get("/avatar/:avatarHash") {
    val avatarHash = params("avatarHash")

    app.findImageByHash(avatarHash) match {
      case Some(filepath) =>
        contentType = "image/png"
        Ok(filepath)
      case None =>
        NotFound("file not found")
    }
  }

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

    val isInvalidEmail = !EmailValidator.getInstance().isValid(email)
    val img = loadFileAsImage(file)

    (isInvalidEmail, img) match {
      case (false, Some(imageBuffer)) =>
        app.updateImage (email, file)
        redirect ("/")
      case (_, _) =>
        contentType = "text/html"
        BadRequest(jade("/avatar", "isInvalidEmail" -> isInvalidEmail, "isInvalidImageFile" -> img.isEmpty))
    }
  }

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
