package com.github.njustus.swarchitectureanalyzer

import cats.data.Validated.{Invalid, Valid}

import java.nio.file.{Path, Paths}
import com.monovore.decline._
object CliArguments {
  import cats.implicits._
  case class Config(jarFile: Path,
                    basePackage: String,
                    outputFile: Path)

  private val outFile = Opts.option[Path]("output", short="o", help="output file")
  private val inFile = Opts.argument[Path]("input-file")
  private val pck = Opts.argument[String]("base-package")

  def parse(args: Seq[String]): Config = {
    val conf: Opts[Config] = (inFile, pck, outFile.orNone).mapN { case (jar, pck, outOpt) =>
      val out = outOpt.getOrElse { Paths.get(s"${jar.getFileName}.puml") }
      Config(jar, pck, out)
    }

    val cmd = Command("sw-architecture", "A software-dependency-visualizer")(conf)
    cmd.parse(args, sys.env) match {
      case Left(help) =>
        println(help)
        sys.exit(1)
      case Right(config) => config
    }
  }

}
