package com.github.kei10in.scalavatar.model

import java.io.File
import java.net.URL
import javax.imageio._
import java.awt.RenderingHints
import java.awt.image.BufferedImage


trait Avatar {
  def image(): BufferedImage
  def imageWithSize(s: Int): BufferedImage = {
    val img = image()
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
  def fromFile(filepath: File) = {
    new FileAvatar(filepath)
  }

  def fromUrl(imageUrl: URL) = {
    new UrlAvatar(imageUrl)
  }
}

class FileAvatar(filepath: File) extends Avatar {
  def image() = {
    ImageIO.read(filepath)
  }
}

class UrlAvatar(imageUrl: URL) extends Avatar {
  def image() = {
    ImageIO.read(imageUrl)
  }
}