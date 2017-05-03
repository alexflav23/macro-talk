/*
 * Copyright 2013 - 2017 Outworkers Ltd.
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
package com.github.alexflav23.macrotalk

object Showers {

  // Let's use this as a backup plan. Eg. if a type has no implicit Shows instance,
  // we can just default to calling .toString on that type.
  // Later we'll hook this up to implicit resolution
  class StringShowers[T] extends Shows[T] {
    override def show(instance: T): String = instance.toString
  }

  // This is to handle optional types, as they are quite common.
  class OptionShows[T : Shows] extends Shows[Option[T]] {
    override def show(instance: Option[T]): String = {
      instance.fold("None")(Shows[T].show)
    }
  }

  // Maps need special handling, just like all collections with two type params.
  // This is because of a compiler limitation around higher kinded type constructors.
  class MapLikeShows[M[A, B] <: TraversableOnce[(A, B)], Key, Value]()(
    implicit kShows: Shows[Key],
    vShows: Shows[Value]
  ) extends Shows[M[Key, Value]] {
    override def show(m: M[Key, Value]): String = m.map { case (key, value) =>
      kShows.show(key) + " " + vShows.show(value)
    } mkString "\n"
  }

  // This will be a generic shows for collections.
  class TraversableShows[M[X] <: TraversableOnce[X], RR]()(
    implicit shows: Shows[RR]
  ) extends Shows[M[RR]] {
    override def show(instance: M[RR]): String = instance.map(shows.show) mkString "\n"
  }
}
