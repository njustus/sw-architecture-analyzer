package com.github.njustus.swarchitectureanalyzer

import com.github.njustus.swarchitectureanalyzer.App.DependencyGraph
import io.github.classgraph.ClassInfo
import org.slf4j.LoggerFactory

object PlantUmlGenerator {

  val log = LoggerFactory.getLogger(this.getClass)

  def generateInterfaceDefinitions(interfaces: Seq[App.InterfaceMeta]) =
    interfaces.map { interface =>
      val name = interface.uniqueName
      s"interface $name"
    }

  def generateClassDefinitions(classes: Seq[App.ClassMeta]) =
    classes.map { classMeta =>
      val name = classMeta.uniqueName
      s"class $name"
    }

  def findIfMeta(dependencyGraph: DependencyGraph, impledInterface: ClassInfo) =
    dependencyGraph.interfaces.find { ifmeta =>
      ifmeta.name == dependencyGraph.getName(impledInterface)
    }

  def generateImplements(dependencyGraph: DependencyGraph)  = {
    val options = for {
      classMeta <- dependencyGraph.classes
      impledInterface <- classMeta.implementedInterfaces
      ifMeta = findIfMeta(dependencyGraph, impledInterface)
    } yield ifMeta match {
      case Some(meta) =>
        Some(s"${classMeta.uniqueName} --> ${meta.uniqueName}")
      case None =>
        log.warn(s"Unknown interface dependency: ${impledInterface.getName}")
        None
    }

    options.collect { case Some(x) => x}
  }

  def generatePlantUml(dependencyGraph: DependencyGraph): String = {
    val ifDefs = generateInterfaceDefinitions(dependencyGraph.interfaces)
    val classDefs = generateClassDefinitions(dependencyGraph.classes)
    val implements = generateImplements(dependencyGraph)

    s"""@startuml
       |${ifDefs.mkString("\n")}
       |${classDefs.mkString("\n")}
       |
       |${implements.mkString("\n")}
       |@enduml
       |""".stripMargin
  }
}
