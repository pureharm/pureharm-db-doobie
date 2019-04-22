/**
  * Copyright (c) 2019 BusyMachines
  *
  * See company homepage at: https://www.busymachines.com/
  *
  * Licensed under the Apache License, Version 2.0 (the "License");
  * you may not use this file except in compliance with the License.
  * You may obtain a copy of the License at
  *
  * http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  */
package busymachines.pureharm
/**
  *
  * @author Lorand Szakacs, https://github.com/lorandszakacs
  * @since 02 Apr 2019
  *
  */
package object db {
  final type ConnectionIO[T] = slick.dbio.DBIO[T]
  final val ConnectionIO: slick.dbio.DBIO.type = slick.dbio.DBIO

  final type SlickBackendDB      = slick.jdbc.JdbcProfile#Backend#Database
  final type SlickJDBCProfileAPI = slick.jdbc.JdbcProfile#API

  final object JDBCUrl extends PhantomType[String]
  final type JDBCUrl = JDBCUrl.Type

  final object DBUsername extends PhantomType[String]
  final type DBUsername = DBUsername.Type

  final object DBPassword extends PhantomType[String]
  final type DBPassword = DBPassword.Type

  final object JDBCProfileAPI extends PhantomType[SlickJDBCProfileAPI]
  final type JDBCProfileAPI = JDBCProfileAPI.Type

  final object DatabaseBackend extends PhantomType[SlickBackendDB]
  final type DatabaseBackend = DatabaseBackend.Type

  final object TableName extends PhantomType[String]
  final type TableName = TableName.Type

  /**
    * Basically used to run computation when mapping slick's DBIO's,
    * mostly used for CPU bound computation, since all IO is done
    * within slick's AsyncExecutor configured via the [[DBBlockingIOExecutionConfig]]
    * when instantiating a [[Transactor]]
    */
  final object ConnectionIOEC extends PhantomType[scala.concurrent.ExecutionContext]
  final type ConnectionIOEC = ConnectionIOEC.Type
}
