package com.github.kei10in

import org.scalatra._
import scalate.ScalateSupport

class ScalavatarServlet extends ScalavatarStack {

  get("/") {
    <html>
      <body>
        <h1>Hello, world!</h1>
        Say <a href="hello-scalate">hello to Scalate</a>.
      </body>
    </html>
  }
  
}
