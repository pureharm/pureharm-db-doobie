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

package busymachines.pureharm.dbdoobie.testkit

import busymachines.pureharm.db._
import busymachines.pureharm.db.testkit._
import busymachines.pureharm.dbdoobie._
import busymachines.pureharm.dbdoobie.implicits._
import busymachines.pureharm.effects._
import busymachines.pureharm.testkit._
import busymachines.pureharm.testkit.util._

/** @author
  *   Lorand Szakacs, https://github.com/lorandszakacs
  * @since 26
  *   Jun 2020
  */
trait DoobieDBTestSetup extends DBTestSetup[Transactor[IO]] with PureharmTestRuntimeLazyConversions {

  /** Should be overridden to create a connection config appropriate for the test
    */
  override def dbConfig(testOptions: TestOptions)(implicit logger: TestLogger): DBConnectionConfig

  override def dbTransactorInstance(
    testOptions: TestOptions
  )(implicit rt: PureharmTestRuntime, logger: TestLogger): Resource[IO, Transactor[IO]] = {
    val config = dbConfig(testOptions)
    for {
      _     <- logger
        .info(MDCKeys.testSetup(testOptions))(s"CREATING Transactor[IO] for: ${config.psqlJdbcURL}")
        .to[Resource[IO, *]]
      trans <- Transactor.pureharmTransactor[IO](
        dbConfig  = dbConfig(testOptions),
        dbConnEC  = DoobieConnectionEC.safe(rt.executionContextFT),
        dbBlocker = DoobieBlocker(rt.blocker),
      )

    } yield trans
  }
}
