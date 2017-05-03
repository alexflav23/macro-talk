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

import org.scalatest.{ FlatSpec, Matchers }
import org.scalatest.prop.GeneratorDrivenPropertyChecks
import com.outworkers.util.samplers._
import org.scalacheck.Gen

class ShowsTests extends FlatSpec with Matchers with GeneratorDrivenPropertyChecks {

  implicit override val generatorDrivenConfig: PropertyCheckConfiguration = {
    PropertyCheckConfiguration(minSuccessful = 50)
  }

  it should "automatically derive a Shows for a simple type" in {
    val sample = gen[User]
    info(sample.show())
    "sample.show()" should compile
  }

  it should "automatically derive a Shows for a nested type" in {
    val sample = gen[NestedUser]
    info(sample.show())
    "sample.show()" should compile
  }

  it should "automatically derive a Shows for a nested tuple type" in {
    val sample = gen[TupleRecord]
    info(sample.show())
    "sample.show()" should compile
  }

  it should "manually derive a Shows instance for a traversable type" in {
    val shows = new Showers.TraversableShows[List, String]()

    forAll { l: List[String] =>
      shows.show(l) shouldEqual l.mkString(", ")
    }
  }

  it should "manually derive a Shows instance for an Option type" in {
    val shows = new Showers.OptionShows[String]()

    forAll { l: Option[String] =>
      if (l.isDefined) {
        shows.show(l) shouldEqual l.toString
      } else {
        shows.show(l) shouldEqual "None"
      }
    }
  }

  it should "automatically derive a Shows instance for a tuple type" in {
    "val shows = Shows[(Int, String)]" should compile
  }

  it should "manually derive a Shows instance for a Map type" in {
    val shows = new Showers.MapLikeShows[Map, String, String]()

    forAll { map: Map[String, String] =>
      shows.show(map) shouldEqual (map.map { case (k, v) => s"$k -> $v" } mkString("\n"))
    }
  }


  it should "automatically derive a shows for a nested tuple collection type" in {
    implicit val mapShows = new Showers.MapLikeShows[Map, String, String]()

    val sample = gen[TupleCollectionRecord]
    info(sample.show())
    "sample.show()" should compile
  }

  it should "automatically derive a shows for a type with collections" in {
    val sample = gen[CollectionSample]
    info(sample.show())
    "sample.show()" should compile
  }
}
