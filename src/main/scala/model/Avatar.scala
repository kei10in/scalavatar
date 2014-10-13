package com.github.kei10in.scalavatar.model

import java.io.File
import java.nio.file.Path
import java.net.URL
import javax.imageio._
import java.awt.RenderingHints
import java.awt.image.BufferedImage

import scala.util.Try


trait Avatar {
  def image(): Option[BufferedImage]
  def imageWithSize(s: Int): Option[BufferedImage] = image().map { img=>
    val scaledImg = new BufferedImage(s, s, img.getType)
    val g = scaledImg.createGraphics()

    g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
    g.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
    g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);

    if (s < img.getWidth) {
      g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
      g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
    }

    g.drawImage(img, 0, 0, s, s, null)
    scaledImg
  }
}

object Avatar {
  def fromPath(path: Path) = {
    new PathAvatar(path)
  }

  def fromUrl(imageUrl: URL) = {
    new UrlAvatar(imageUrl)
  }
}


class PathAvatar(path: Path) extends Avatar {
  def image() = {
    Try(ImageIO.read(path.toFile())).toOption
  }
}

class UrlAvatar(imageUrl: URL) extends Avatar {
  def image() = {
    Try(ImageIO.read(imageUrl)).toOption
  }
}