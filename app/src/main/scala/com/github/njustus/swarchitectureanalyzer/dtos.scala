package com.github.njustus.swarchitectureanalyzer

import io.github.classgraph.ClassInfo

object dtos {
  case class DependencyGraph(
                            basePackage: String,
                            interfaces: Seq[InterfaceMeta],
                            classes: Seq[ClassMeta]
                            ) {
    def getName(clInfo: ClassInfo): String =
      clInfo.getName.stripPrefix(basePackage+".")
  }
  case class InterfaceMeta(name: String,
                           packageName: String) {
    def uniqueName: String = name
  }

  case class ClassMeta(name: String,
                       packageName: String,
                        dependsOn: Seq[ClassInfo],
                        implementedInterfaces: Seq[ClassInfo]
                      ) {
    def uniqueName: String = name
  }
}
