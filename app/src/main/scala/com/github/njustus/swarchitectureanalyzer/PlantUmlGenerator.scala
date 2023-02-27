package com.github.njustus.swarchitectureanalyzer

import io.github.classgraph.ClassInfo
import org.slf4j.LoggerFactory
import dtos._

object PlantUmlGenerator {

  private val log = LoggerFactory.getLogger(this.getClass)

  def generateInterfaceDefinitions(interfaces: Seq[InterfaceMeta]) =
    interfaces.map { interface =>
      val name = interface.uniqueName
      s"interface $name"
    }

  def generateClassDefinitions(classes: Seq[ClassMeta]) =
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
        List(s"${classMeta.uniqueName} ..|> ${meta.uniqueName}")
      case None =>
        log.warn(s"Unknown interface dependency: ${impledInterface.getName}")
        Nil
    }

    options.flatten
  }

  def findDependent(dependencyGraph: DependencyGraph, dependent: ClassInfo) =
    dependencyGraph.classes.find { classMeta =>
      classMeta.uniqueName == dependencyGraph.getName(dependent)
    }

  def generateUses(dependencyGraph: DependencyGraph) = {
    val options = for {
      classMeta <- dependencyGraph.classes
      dependent <- classMeta.dependsOn
      depndentMeta = findDependent(dependencyGraph, dependent)
    } yield depndentMeta match {
      case Some(meta) =>
        List(s"${classMeta.uniqueName} ..> ${meta.uniqueName}")
      case None =>
        log.warn(s"Unknown dependency: ${dependent.getName}")
        Nil
    }

    options.flatten
  }

  def generatePlantUml(dependencyGraph: DependencyGraph): String = {
    val ifDefs = generateInterfaceDefinitions(dependencyGraph.interfaces)
    val classDefs = generateClassDefinitions(dependencyGraph.classes)
    val implements = generateImplements(dependencyGraph)
    val uses = generateUses(dependencyGraph)

    s"""@startuml
       |${ifDefs.mkString("\n")}
       |${classDefs.mkString("\n")}
       |
       |
       |${implements.mkString("\n")}
       |${uses.mkString("\n")}
       |@enduml
       |""".stripMargin
  }
}
