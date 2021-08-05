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
addCommandAlias("run-it", "++IntegrationTest/test")

Global / onChangedBuildSource := ReloadOnSourceChanges

val Scala213 = "2.13.6"
val Scala3   = "3.0.1"

//=============================================================================
//============================ publishing details =============================
//=============================================================================

//see: https://github.com/xerial/sbt-sonatype#buildsbt
ThisBuild / sonatypeCredentialHost := "s01.oss.sonatype.org"

ThisBuild / baseVersion      := "0.3"
ThisBuild / organization     := "com.busymachines"
ThisBuild / organizationName := "BusyMachines"
ThisBuild / homepage         := Option(url("https://github.com/busymachines/pureharm-db-testkit"))

ThisBuild / scmInfo := Option(
  ScmInfo(
    browseUrl  = url("https://github.com/busymachines/pureharm-db-testkit"),
    connection = "git@github.com:busymachines/pureharm-db-core.git",
  )
)

/** I want my email. So I put this here. To reduce a few lines of code, the sbt-spiewak plugin generates this (except
  * email) from these two settings:
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

ThisBuild / startYear  := Some(2019)
ThisBuild / licenses   := List("Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0"))

//until we get to 1.0.0, we keep strictSemVer false
ThisBuild / strictSemVer              := false
ThisBuild / spiewakCiReleaseSnapshots := false
ThisBuild / spiewakMainBranches       := List("main")
ThisBuild / Test / publishArtifact    := false

ThisBuild / scalaVersion       := Scala213
ThisBuild / crossScalaVersions := List(Scala213, Scala3)

//required for binary compat checks
ThisBuild / versionIntroduced := Map(
  Scala213 -> "0.1.0",
  Scala3   -> "0.3.0",
)

//=============================================================================
//================================ Dependencies ===============================
//=============================================================================
ThisBuild / resolvers += Resolver.sonatypeRepo("releases")
ThisBuild / resolvers += Resolver.sonatypeRepo("snapshots")

// format: off
val pureharmCoreV           = "0.3.0"          //https://github.com/busymachines/pureharm-core/releases
val pureharmEffectsV        = "0.5.0"          //https://github.com/busymachines/pureharm-effects-cats/releases
val pureharmDBCoreV         = "0.5.0"          //https://github.com/busymachines/pureharm-db-core/releases
val pureharmDBCoreJDBCV     = "0.6.0"          //https://github.com/busymachines/pureharm-db-core-jdbc/releases
val pureharmJSONCirceV      = "0.3.0-M1"       //https://github.com/busymachines/pureharm-json-circe/releases
val pureharmDBTestkitV      = "0.3.0"          //https://github.com/busymachines/pureharm-db-testkit/releases
val doobieV                 = "1.0.0-M5"       //https://github.com/tpolecat/doobie/releases
val doobieCE2V              = "0.13.4"         //https://github.com/tpolecat/doobie/releases
val log4catsV               = "2.1.1"          //https://github.com/typelevel/log4cats/releases
val log4catsCE2V            = "1.3.1"          //https://github.com/typelevel/log4cats/releases
// format: on

//=============================================================================
//============================== Project details ==============================
//=============================================================================

lazy val root = project
  .in(file("."))
  .aggregate(
    `db-doobie`,
    `db-testkit-doobie`,
    `db-doobie-ce2`,
    `db-testkit-doobie-ce2`,
  )
  .enablePlugins(NoPublishPlugin)
  .enablePlugins(SonatypeCiReleasePlugin)
  .settings(commonSettings)

lazy val `db-doobie` = project
  .settings(commonSettings)
  .settings(
    name := "pureharm-db-doobie",
    libraryDependencies ++= Seq(
      // format: off
      "com.busymachines"    %% "pureharm-core-identifiable"     % pureharmCoreV                     withSources(),
      "com.busymachines"    %% "pureharm-core-anomaly"          % pureharmCoreV                     withSources(),
      "com.busymachines"    %% "pureharm-core-sprout"           % pureharmCoreV                     withSources(),
      "com.busymachines"    %% "pureharm-effects-cats"          % pureharmEffectsV                  withSources(),
      "com.busymachines"    %% "pureharm-db-core"               % pureharmDBCoreV                   withSources(),
      "com.busymachines"    %% "pureharm-db-core-jdbc"          % pureharmDBCoreJDBCV               withSources(),
      "com.busymachines"    %% "pureharm-json-circe"            % pureharmJSONCirceV                withSources(),
      "org.tpolecat"        %% "doobie-core"                    % doobieV                           withSources(),
      "org.tpolecat"        %% "doobie-hikari"                  % doobieV                           withSources(),
      "org.tpolecat"        %% "doobie-postgres"                % doobieV                           withSources(),
      // format: on
    ),
  )
  .settings(
    javaOptions ++= Seq("-source", "1.8", "-target", "1.8")
  )

lazy val `db-testkit-doobie` = project
  .settings(commonSettings)
  .configs(IntegrationTest)
  .settings(Defaults.itSettings)
  .settings(
    name := "pureharm-db-testkit-doobie",
    libraryDependencies ++= Seq(
      // format: off
      "com.busymachines"    %% "pureharm-db-testkit"            % pureharmDBTestkitV                withSources(),
      "com.busymachines"    %% "pureharm-db-test-data"          % pureharmDBTestkitV    % "it,test" withSources(),
      "org.typelevel"       %% "log4cats-slf4j"                 % log4catsV             % "it,test" withSources(),
      // format: on
    ),
  )
  .settings(
    javaOptions ++= Seq("-source", "1.8", "-target", "1.8")
  )
  .dependsOn(
    `db-doobie`
  )

lazy val `db-doobie-ce2` = project
  .settings(commonSettings)
  .settings(
    name := "pureharm-db-doobie-ce2",
    libraryDependencies ++= Seq(
      // format: off
      "com.busymachines"    %% "pureharm-core-identifiable"     % pureharmCoreV                     withSources(),
      "com.busymachines"    %% "pureharm-core-anomaly"          % pureharmCoreV                     withSources(),
      "com.busymachines"    %% "pureharm-core-sprout"           % pureharmCoreV                     withSources(),
      "com.busymachines"    %% "pureharm-effects-cats-2"        % pureharmEffectsV                  withSources(),
      "com.busymachines"    %% "pureharm-db-core"               % pureharmDBCoreV                   withSources(),
      "com.busymachines"    %% "pureharm-db-core-jdbc"          % pureharmDBCoreJDBCV               withSources(),
      "com.busymachines"    %% "pureharm-json-circe"            % pureharmJSONCirceV                withSources(),
      "org.tpolecat"        %% "doobie-core"                    % doobieCE2V                        withSources(),
      "org.tpolecat"        %% "doobie-hikari"                  % doobieCE2V                        withSources(),
      "org.tpolecat"        %% "doobie-postgres"                % doobieCE2V                        withSources(),
      // format: on
    ),
  )
  .settings(
    javaOptions ++= Seq("-source", "1.8", "-target", "1.8")
  )

lazy val `db-testkit-doobie-ce2` = project
  .settings(commonSettings)
  .configs(IntegrationTest)
  .settings(Defaults.itSettings)
  .settings(
    name := "pureharm-db-testkit-doobie-ce2",
    libraryDependencies ++= Seq(
      // format: off
      "com.busymachines"    %% "pureharm-db-testkit-ce2"        % pureharmDBTestkitV                withSources(),
      "com.busymachines"    %% "pureharm-db-test-data-ce2"      % pureharmDBTestkitV    % "it,test" withSources(),
      "org.typelevel"       %% "log4cats-slf4j"                 % log4catsCE2V          % "it,test" withSources(),
      // format: on
    ),
  )
  .settings(
    javaOptions ++= Seq("-source", "1.8", "-target", "1.8")
  )
  .dependsOn(
    `db-doobie-ce2`
  )

//=============================================================================
//================================= Settings ==================================
//=============================================================================

lazy val commonSettings = Seq(
  scalacOptions ++= scalaCompilerOptions(scalaVersion.value)
)

def scalaCompilerOptions(scalaVersion: String): Seq[String] =
  CrossVersion.partialVersion(scalaVersion) match {
    case Some((2, _)) =>
      Seq[String](
        //"-Xsource:3"
      )
    case _            => Seq.empty[String]
  }
