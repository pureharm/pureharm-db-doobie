/*
 * Copyright 2019 BusyMachines
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

//=============================================================================
//============================== build details ================================
//=============================================================================

addCommandAlias("github-gen", "githubWorkflowGenerate")
addCommandAlias("github-check", "githubWorkflowCheck")
addCommandAlias("run-it", "IntegrationTest/test")
Global / onChangedBuildSource := ReloadOnSourceChanges

val Scala213  = "2.13.5"
val Scala3RC1 = "3.0.0-RC1"

//=============================================================================
//============================ publishing details =============================
//=============================================================================

//see: https://github.com/xerial/sbt-sonatype#buildsbt
ThisBuild / sonatypeCredentialHost := "s01.oss.sonatype.org"

ThisBuild / baseVersion  := "0.1.0"
ThisBuild / organization := "com.busymachines"
ThisBuild / organizationName := "BusyMachines"
ThisBuild / homepage     := Option(url("https://github.com/busymachines/pureharm-db-testkit"))

ThisBuild / scmInfo := Option(
  ScmInfo(
    browseUrl  = url("https://github.com/busymachines/pureharm-db-testkit"),
    connection = "git@github.com:busymachines/pureharm-db-core.git",
  )
)

/** I want my email. So I put this here. To reduce a few lines of code,
  * the sbt-spiewak plugin generates this (except email) from these two settings:
  * {{{
  * ThisBuild / publishFullName   := "Loránd Szakács"
  * ThisBuild / publishGithubUser := "lorandszakacs"
  * }}}
  */
ThisBuild / developers := List(
  Developer(
    id    = "lorandszakacs",
    name  = "Loránd Szakács",
    email = "lorand.szakacs@protonmail.com",
    url   = new java.net.URL("https://github.com/lorandszakacs"),
  )
)

ThisBuild / startYear := Some(2019)
ThisBuild / licenses   := List("Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0"))

//until we get to 1.0.0, we keep strictSemVer false
ThisBuild / strictSemVer              := false
ThisBuild / spiewakCiReleaseSnapshots := true
ThisBuild / spiewakMainBranches       := List("main")
ThisBuild / Test / publishArtifact    := false

ThisBuild / scalaVersion       := Scala213
ThisBuild / crossScalaVersions := List(Scala213) //List(Scala213, Scala3RC1)

//required for binary compat checks
ThisBuild / versionIntroduced := Map(
  Scala213  -> "0.1.0",
  Scala3RC1 -> "0.1.0",
)

//=============================================================================
//================================ Dependencies ===============================
//=============================================================================
ThisBuild / resolvers += Resolver.sonatypeRepo("releases")
ThisBuild / resolvers += Resolver.sonatypeRepo("snapshots")

val pureharmCoreV       = "0.1.0-7bc6204"  //https://github.com/busymachines/pureharm-core/releases
val pureharmEffectsV    = "0.1.0-4946221"  //https://github.com/busymachines/pureharm-effects-cats/releases
val pureharmDBCoreV     = "0.1.0-ea46bd5"  //https://github.com/busymachines/pureharm-db-core/releases
val pureharmDBCoreJDBCV = "0.1.0-556c9fc" //https://github.com/busymachines/pureharm-db-core-jdbc/releases
val pureharmJSONCirceV  = "0.1.0-3ab2d2c"  //https://github.com/busymachines/pureharm-json-circe/releases

lazy val doobieV    = "0.12.1" //https://github.com/tpolecat/doobie/releases

//for testing
val pureharmDBTestkitV = "0.1.0-fa3e619" //https://github.com/busymachines/pureharm-db-testkit/releases
val log4catsV = "1.2.0" //https://github.com/typelevel/log4cats/releases
//=============================================================================
//============================== Project details ==============================
//=============================================================================

lazy val root = project
  .in(file("."))
  .aggregate(
    `db-doobie`,
    `db-testkit-doobie`,
  )
  .enablePlugins(NoPublishPlugin)
  .enablePlugins(SonatypeCiReleasePlugin)
  .settings(commonSettings)

lazy val `db-doobie` = project
  .settings(commonSettings)
  .settings(
    name := "pureharm-db-doobie",
    libraryDependencies ++= Seq(
      "com.busymachines" %% "pureharm-core-identifiable" % pureharmCoreV withSources(),
      "com.busymachines" %% "pureharm-core-anomaly"      % pureharmCoreV withSources(),
      "com.busymachines" %% "pureharm-core-sprout"       % pureharmCoreV withSources(),
      "com.busymachines" %% "pureharm-effects-cats"      % pureharmEffectsV withSources(),
      "com.busymachines" %% "pureharm-db-core"           % pureharmDBCoreV withSources(),
      "com.busymachines" %% "pureharm-db-core-jdbc"      % pureharmDBCoreJDBCV withSources(),
      "com.busymachines" %% "pureharm-json-circe"        % pureharmJSONCirceV withSources(),
      
      "org.tpolecat" %% "doobie-core"     % doobieV withSources (),
      "org.tpolecat" %% "doobie-hikari"   % doobieV withSources (),
      "org.tpolecat" %% "doobie-postgres" % doobieV withSources (),
    ),
  ).settings(
    javaOptions ++= Seq("-source", "1.8", "-target", "1.8")
  )

lazy val `db-testkit-doobie` = project
  .settings(commonSettings)
  .configs(IntegrationTest)
  .settings(Defaults.itSettings)
  .settings(
    name := "pureharm-db-testkit-doobie",
    libraryDependencies ++= Seq(
      "com.busymachines" %% "pureharm-db-testkit" % pureharmDBTestkitV withSources(),
      "com.busymachines" %% "pureharm-db-test-data" % pureharmDBTestkitV % "it,test" withSources(),
      "org.typelevel" %% "log4cats-slf4j"   % log4catsV % "it,test" withSources(),
    )
  ).settings(
    javaOptions ++= Seq("-source", "1.8", "-target", "1.8")
  ).dependsOn(
    `db-doobie`
  )

//=============================================================================
//================================= Settings ==================================
//=============================================================================
lazy val commonSettings = Seq(
  Compile / unmanagedSourceDirectories ++= {
    val major = if (isDotty.value) "-3" else "-2"
    List(CrossType.Pure, CrossType.Full).flatMap(
      _.sharedSrcDir(baseDirectory.value, "main").toList.map(f => file(f.getPath + major))
    )
  },
  Test / unmanagedSourceDirectories ++= {
    val major = if (isDotty.value) "-3" else "-2"
    List(CrossType.Pure, CrossType.Full).flatMap(
      _.sharedSrcDir(baseDirectory.value, "test").toList.map(f => file(f.getPath + major))
    )
  },
)