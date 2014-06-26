package com.github.kei10in

import model._

import java.io.File
import java.security.MessageDigest
import java.nio.charset.StandardCharsets._

import org.scalatra._
import scalate.ScalateSupport

import org.apache.commons.validator.routines.EmailValidator


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

    if (EmailValidator.getInstance().isValid(email)) {
      app.updateImage(email, file)
      redirect("/")
    } else {
      contentType = "text/html"
      BadRequest(jade("/avatar", "isInvalidEmail" -> true))
    }
  }

}
