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

package busymachines.pureharm.dbdoobie.internals

import busymachines.pureharm.effects._
import busymachines.pureharm.effects.implicits._
import busymachines.pureharm.sprout._
import doobie.Meta

final class PureharmMetaOps[O](val m: Meta[O]) extends AnyVal {
  def sprout[N](implicit sp: NewType[O, N]): Meta[N] = m.imap(sp.newType)(sp.oldType)

  def sproutRefined[N](implicit sp: RefinedTypeThrow[O, N], so: Show[O]): Meta[N] = new Meta[N](
    get = m.get.temap(o => sp.newType[Attempt](o).leftMap(_.getMessage)),
    put = m.put.contramap(sp.oldType),
  )
}
