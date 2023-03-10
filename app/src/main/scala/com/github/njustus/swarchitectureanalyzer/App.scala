/*
 * This Scala source file was generated by the Gradle 'init' task.
 */
package com.github.njustus.swarchitectureanalyzer

import io.github.classgraph.{ClassGraph, ClassInfo}
import org.slf4j.LoggerFactory

import java.io.File
import java.nio.file.{Files, Paths}
import scala.jdk.CollectionConverters._
import dtos._

object App {
  private val log = LoggerFactory.getLogger(this.getClass)

  def main(args: Array[String]): Unit = {
    val conf = CliArguments.parse(args.toSeq)
    val basePackage = conf.basePackage
    log.info(s"reading jar file: ${conf.jarFile} with basePackage: ${basePackage}")
    log.info(s"generating image to: ${conf.outputFile}")
    val cl = JarExtractor.createClassLoader(conf.jarFile)

    val classGraph = new ClassGraph()
      .addClassLoader(cl)
      .verbose()
      .enableAllInfo()
      .enableInterClassDependencies()
      .acceptPackages(basePackage)
      .filterClasspathElements { pathElement =>
        !pathElement.contains("$anon") &&
        !pathElement.contains("$lazy")
      }

    val result = classGraph.scan()

    val interfaces = result.getAllInterfaces.asScala.map { interface =>
      InterfaceMeta(interface.getName.stripPrefix(basePackage + "."),
        interface.getPackageName.stripPrefix(basePackage + "."),
      )
    }.toSeq

    val classes = result.getAllStandardClasses.asScala.map { clInfo =>
      println(clInfo)
      ClassMeta(
        clInfo.getName.stripPrefix(basePackage+"."),
        clInfo.getPackageName.stripPrefix(basePackage+"."),
        clInfo.getClassDependencies.asScala.toSeq,
        clInfo.getInterfaces.asScala.toSeq
      )
    }.toSeq

    val graph = DependencyGraph(basePackage, interfaces, classes)
    val plantUmlContent = PlantUmlGenerator.generatePlantUml(graph)

    Files.writeString(conf.outputFile, plantUmlContent)
    log.info(s"graph saved to ${conf.outputFile}")
    println(s"call: plantuml -tsvg ${conf.outputFile}")

    result.close()
  }
}
