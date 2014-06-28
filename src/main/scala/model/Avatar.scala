package com.github.kei10in.model

import java.io.File
import javax.imageio._
import java.awt.image.BufferedImage


trait Avatar {
  def image(): BufferedImage
}

object Avatar {
  def fromFile(filepath: File) = {
    new FileAvatar(filepath)
  }
}

class FileAvatar(filepath: File) extends Avatar {
  def image() = {
    ImageIO.read(filepath)
  }
}