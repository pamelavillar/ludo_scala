name := "ludo-gui"

version := "0.1.0"

scalaVersion := "3.3.7"

// ScalaFX
libraryDependencies += "org.scalafx" %% "scalafx" % "21.0.0-R32"

val javafxVersion = "21.0.2"

val osName = sys.props("os.name").toLowerCase
val osArch = sys.props("os.arch").toLowerCase

val platform =
  if (osName.contains("mac")) {
    if (osArch.contains("aarch64") || osArch.contains("arm"))
      "mac-aarch64"
    else
      "mac"
  } else if (osName.contains("win")) {
    "win"
  } else {
    "linux"
  }

libraryDependencies ++= Seq(
  "org.openjfx" % "javafx-controls" % javafxVersion classifier platform,
  "org.openjfx" % "javafx-fxml" % javafxVersion classifier platform,
  "org.openjfx" % "javafx-graphics" % javafxVersion classifier platform,
  "org.openjfx" % "javafx-base" % javafxVersion classifier platform
)

fork := true