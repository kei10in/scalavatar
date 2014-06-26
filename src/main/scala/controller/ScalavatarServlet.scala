package com.github.kei10in

import model._

import java.io.File
import java.security.MessageDigest
import java.nio.charset.StandardCharsets._

import org.scalatra._
import scalate.ScalateSupport


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

  post("/avatar") {
    val email = params("e-mail")
    val file = fileParams("image-file")

    app.updateImage(email, file)

    redirect("/")
  }

}
