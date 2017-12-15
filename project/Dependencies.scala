import sbt._

object Version {
  val Scala          = "2.12.4"
  val circe          = "0.8.0"
  val discipline     = "0.7.2"
  val scalaCheck     = "0.13.4"
  val scalaTest      = "3.0.1"
  val typesafeConfig = "1.3.1"
}

object Library {
  val circeCore      = "io.circe"       %% "circe-core"    % Version.circe
  val circeParser    = "io.circe"       %% "circe-parser"  % Version.circe
  val circeTesting   = "io.circe"       %% "circe-testing" % Version.circe
  val discipline     = "org.typelevel"  %% "discipline"    % Version.discipline
  val scalaCheck     = "org.scalacheck" %% "scalacheck"    % Version.scalaCheck
  val scalaTest      = "org.scalatest"  %% "scalatest"     % Version.scalaTest
  val typesafeConfig = "com.typesafe"   % "config"         % Version.typesafeConfig % "provided"
}
