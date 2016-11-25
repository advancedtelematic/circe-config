import ReleaseTransformations._
import sbtrelease.ExtraReleaseCommands
import java.net.URI

def readSettings(envKey: String, propKey: Option[String] = None): String = {
  sys.env.get(envKey).orElse(propKey.flatMap(sys.props.get(_))).getOrElse("")
}

val nexus = readSettings("PUBLISH_URL")

lazy val `circe-config` =
  project
    .in(file("."))
    .settings(
      releaseCrossBuild := true,
      licenses := Seq("Apache 2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0")),
      publishMavenStyle := true,
      publishArtifact in Test := false,
      pomIncludeRepository := { _ =>
        false
      },
      publishTo := {
        if (isSnapshot.value)
          Some("snapshots" at nexus + "content/repositories/snapshots")
        else
          Some("releases" at nexus + "content/repositories/releases")
      },
      autoAPIMappings := true,
      scmInfo := Some(
        ScmInfo(
          url("https://github.com/advancedtelematic/circe-config"),
          "scm:git:git@github.com:advancedtelematic/circe-config.git"
        )
      ),
      pomExtra := (
        <developers>
        <developer>
          <id>koshelev</id>
          <name>Vladimir Koshelev</name>
        </developer>
      </developers>
      ),
      credentials +=
        Credentials(
          readSettings("PUBLISH_REALM"),
          URI.create(nexus).getHost,
          readSettings("PUBLISH_USER"),
          readSettings("PUBLISH_PASSWORD")
        ),
      releaseProcess := Seq(
        checkSnapshotDependencies,
        releaseStepCommand(ExtraReleaseCommands.initialVcsChecksCommand),
        inquireVersions,
        runClean,
        runTest,
        setReleaseVersion,
        commitReleaseVersion,
        tagRelease,
        publishArtifacts,
        setNextVersion,
        commitNextVersion,
        pushChanges
      )
    )
    .enablePlugins(AutomateHeaderPlugin, GitVersioning, ReleasePlugin)

libraryDependencies ++= Vector(Library.circeCore, Library.typesafeConfig) ++ Vector(
  Library.scalaTest,
  Library.circeTesting
).map(_ % "test")

initialCommands := """|import com.advancedtelematic.circe.config._
                      |""".stripMargin
