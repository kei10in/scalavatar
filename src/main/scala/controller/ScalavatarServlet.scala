package com.github.kei10in

import java.io.File
import java.security.MessageDigest
import java.nio.charset.StandardCharsets._

import org.scalatra._
import scalate.ScalateSupport


class ScalavatarServlet extends ScalavatarStack {

  get("/") {
    contentType = "text/html"
    jade("/index")
  }

  get("/avatar/:avatarHash") {
    val avatarHash = params("avatarHash")
    val path = new File(servletContext.getRealPath("/avatars"))
    val dir = new File(path, avatarHash.take(2))
    val filepath = new File(dir, avatarHash.drop(2))

    contentType = "image/png"
    filepath
  }

  get("/avatar") {
    val eMail = params("e-mail")
    val avatarHash = toMD5String(eMail.trim().toLowerCase())

    redirect("/avatar/" + avatarHash)
  }

  post("/avatar") {
    val eMail = params("e-mail")
    val file = fileParams("image-file")

    val avatarHash = toMD5String(eMail.trim().toLowerCase())

    val path = new File(servletContext.getRealPath("/avatars"))
    if (!path.exists())
      path.mkdir()

    val dir = new File(path, avatarHash.take(2))
    if (!dir.exists())
      dir.mkdir()

    val filepath = new File(dir, avatarHash.drop(2))
    file.write(filepath)

    redirect("/")
  }

  def toMD5String(s: String): String = {
    val digestBytes = MessageDigest.getInstance("MD5").digest(s.getBytes(UTF_8))

    digestBytes.map("%02x".format(_)).mkString
  }

}
