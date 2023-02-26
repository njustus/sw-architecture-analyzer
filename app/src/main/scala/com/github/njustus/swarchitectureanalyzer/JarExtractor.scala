package com.github.njustus.swarchitectureanalyzer

import java.net.URLClassLoader
import java.nio.file.Path

object JarExtractor {
  def extract(jarPath: Path) = {
    new URLClassLoader(Array(jarPath.toUri.toURL), getClass.getClassLoader)
  }
}
