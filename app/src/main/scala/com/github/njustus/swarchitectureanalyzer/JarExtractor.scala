package com.github.njustus.swarchitectureanalyzer

import java.net.URLClassLoader
import java.nio.file.Path

object JarExtractor {
  def createClassLoader(jarPath: Path): URLClassLoader = {
    val uri = jarPath.toAbsolutePath.toUri.toURL
    new URLClassLoader(Array(uri), getClass.getClassLoader)
  }
}
