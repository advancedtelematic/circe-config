import com.typesafe.sbt.GitPlugin
import com.typesafe.sbt.GitPlugin.autoImport._
import de.heikoseeberger.sbtheader.HeaderPlugin
import de.heikoseeberger.sbtheader.HeaderPlugin.autoImport._
import de.heikoseeberger.sbtheader.HeaderPattern
import de.heikoseeberger.sbtheader.license._
import org.scalafmt.sbt.ScalaFmtPlugin
import org.scalafmt.sbt.ScalaFmtPlugin.autoImport._
import sbt._
import sbt.Keys._
import sbt.plugins.JvmPlugin

object Build extends AutoPlugin {

  override def requires =
    JvmPlugin && HeaderPlugin && GitPlugin && ScalaFmtPlugin

  override def trigger = allRequirements

  lazy val compilerOptions = Vector(
    "-deprecation",
    "-encoding",
    "UTF-8", // yes, this is 2 args
    "-feature",
    "-language:existentials",
    "-language:higherKinds",
    "-language:implicitConversions",
    "-unchecked",
    "-Xfatal-warnings",
    "-Xlint:-missing-interpolator",
    "-Yno-adapted-args",
    "-Ywarn-dead-code",
    "-Ywarn-numeric-widen",
    "-Ywarn-value-discard",
    "-Xfuture",
    "-target:jvm-1.8"
  )

  override def projectSettings =
//    reformatOnCompileSettings ++
    Vector(
      // Compile settings
      scalaVersion := Version.Scala,
      crossScalaVersions := Vector(scalaVersion.value, "2.11.11"),
      scalacOptions ++= compilerOptions ++ (
        CrossVersion.partialVersion(scalaVersion.value) match {
          case Some((2, p)) if p == 11 => Seq("-Ywarn-unused-import")
          case _                       => Nil
        }
      ),
      unmanagedSourceDirectories.in(Compile) :=
        Vector(scalaSource.in(Compile).value),
      unmanagedSourceDirectories.in(Test) :=
        Vector(scalaSource.in(Test).value),
      // Publish settings
      organization := "com.advancedtelematic",
      licenses += ("Apache 2.0",
      url("http://www.apache.org/licenses/LICENSE-2.0")),
      mappings.in(Compile, packageBin)
        += baseDirectory.in(ThisBuild).value / "LICENSE" -> "LICENSE",
      // scalafmt settings
      formatSbtFiles := false,
      scalafmtConfig := Some(baseDirectory.in(ThisBuild).value / ".scalafmt.conf"),
      ivyScala := ivyScala.value
        .map(_.copy(overrideScalaVersion = sbtPlugin.value)), // TODO Remove once this workaround no longer needed (https://github.com/sbt/sbt/issues/2786)!

      // Git settings
      git.useGitDescribe := true,
      // Header settings
      headers := Map("scala" -> Apache2_0("2016", "ATS Advanced Telematic Systems GmbH"))
    )
}
