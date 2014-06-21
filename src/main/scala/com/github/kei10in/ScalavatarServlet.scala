package com.github.kei10in

import org.scalatra._
import scalate.ScalateSupport

class ScalavatarServlet extends ScalateServlet with ScalavatarStack {

  get("/") {
    contentType = "text/html"
    jade("/index")
  }
  
}
