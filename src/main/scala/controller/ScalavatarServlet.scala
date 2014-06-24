package com.github.kei10in

import model._

import java.io.File
import java.security.MessageDigest
import java.nio.charset.StandardCharsets._

import org.scalatra._
import scalate.ScalateSupport


class ScalavatarServlet extends ScalavatarStack {

  lazy val app = new Application(new File(servletContext.getRealPath("/")))

  get("/") {
    contentType = "text/html"
    jade("/index")
  }

  get("/avatar/:avatarHash") {
    val avatarHash = params("avatarHash")

    contentType = "image/png"
    app.findImageByHash(avatarHash)
  }

  get("/avatar") {
    val eMail = params("e-mail")
    val avatarHash = toMD5String(eMail.trim().toLowerCase())

    redirect("/avatar/" + avatarHash)
  }

  post("/avatar") {
    val email = params("e-mail")
    val file = fileParams("image-file")

    app.updateImage(email, file)

    redirect("/")
  }

  def toMD5String(s: String): String = {
    val digestBytes = MessageDigest.getInstance("MD5").digest(s.getBytes(UTF_8))

    digestBytes.map("%02x".format(_)).mkString
  }

}
