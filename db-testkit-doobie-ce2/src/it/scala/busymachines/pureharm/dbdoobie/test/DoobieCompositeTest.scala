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

package busymachines.pureharm.dbdoobie.test

import busymachines.pureharm.db._
import busymachines.pureharm.db.testdata._
import busymachines.pureharm.dbdoobie._
import busymachines.pureharm.db.testkit._
import busymachines.pureharm.dbdoobie.testkit._
import busymachines.pureharm.effects._
import busymachines.pureharm.testkit._

import org.typelevel.log4cats.slf4j._

final class DoobieCompositeTest extends DBTest[Transactor[IO]] {
  implicit override val testLogger: TestLogger = TestLogger(Slf4jLogger.getLogger[IO])

  override type ResourceType = (DoobiePHRowRepo[IO], DoobieExtPHRowRepo[IO])

  override def setup: DBTestSetup[Transactor[IO]] = DoobieCompositeTest

  override def resource(testOptions: TestOptions, trans: Transactor[IO]): Resource[IO, ResourceType] =
    Resource.pure[IO, ResourceType]((DoobiePHRowRepo(trans), DoobieExtPHRowRepo(trans)))

  private val data = PHRowRepoTest.pureharmRows

  testResource.test("insert - row1 + ext1") { case (row, ext) =>
    for {
      _ <- row.insert(data.row1)
      _ <- ext.insert(data.ext1)
    } yield ()
  }

  testResource.test("insert ext1 -> foreign key does not exist") { case (_, ext) =>
    for {
      att <- ext.insert(data.extNoFPK).attempt
      failure = interceptFailure[DBForeignKeyConstraintViolationAnomaly](att)
    } yield {
      assert(failure.table == "pureharm_external_rows", "table")
      assert(failure.constraint == "pureharm_external_rows_row_id_fkey", "constraint")
      assert(failure.foreignTable == "pureharm_rows", "foreign_table")
      assert(failure.value == "120-3921-039213", "value")
      assert(failure.column == "row_id", "column")
    }
  }
}

object DoobieCompositeTest extends DoobieDBTestSetup {

  override def dbConfig(testOptions: TestOptions)(implicit logger: TestLogger): DBConnectionConfig =
    PHRTestDBConfig.dbConfig.withSchemaFromClassAndTest(prefix = "doobie", testOptions = testOptions)
}
