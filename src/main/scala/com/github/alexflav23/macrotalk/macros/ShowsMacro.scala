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
package com.github.alexflav23.macrotalk.macros

import scala.reflect.macros.blackbox

@macrocompat.bundle
class ShowsMacros(val c: blackbox.Context) extends MacroHelpers {
  import c.universe._

  val packagePrefix = q"_root_.com.github.alexflav23.macrotalk"
  val macroPkg = q"_root_.com.github.alexflav23.macrotalk.macros"
  private[this] val stringType = tq"_root_.java.lang.String"

  def materialize[T : WeakTypeTag]: Tree = {
    val tpe = weakTypeOf[T]

    tpe match {
      case t if isTuple(tpe) => tupleShows(tpe)
      case t if isCaseClass(t) => fieldShows(tpe, fields(tpe))

      case t if tpe <:< typeOf[Option[_]] => tpe.typeArgs match {
        case head :: Nil => q"new $packagePrefix.Showers.OptionShows[$head]"
        case _ => c.abort(
          c.enclosingPosition,
          s"Found option type with more than two arguments"
        )
      }

      case t if tpe <:< typeOf[TraversableOnce[_]] =>
        tpe.typeArgs match {
          case head :: Nil =>
            q"new $packagePrefix.Showers.TraversableShows[${tpe.typeConstructor}, $head]"
          case first :: second :: Nil =>
            q"new $packagePrefix.Showers.MapLikeShows[${tpe.typeConstructor}, $first, $second]"
          case _ =>
            q"new $packagePrefix.Showers.StringShows[$tpe]"
      }

      case _ => q"new $packagePrefix.Showers.StringShows[$tpe]"
    }
  }

  def tupleShows(tpe: Type): Tree = {
    val cmp = tpe.typeSymbol.name

    val appliers = tpe.typeArgs.zipWithIndex.map { case (tp, i) =>
      val term = tupleTerm(i)
      q""" "  " + ${term.toString} + " = " + $packagePrefix.Shows[$tp].show(
        instance.$term
      )"""
    }

    val t = q"_root_.scala.collection.immutable.List.apply(..$appliers)"

    q"""
      new $packagePrefix.Shows[$tpe] {
        def show(instance: $tpe): $stringType = {
          ${cmp.toString} + "(\n" + $t.mkString("\n") + "\n)"
        }
      }
    """
  }

  def fieldShows(tpe: Type, fields: Iterable[Accessor]): Tree = {
    val cmp = tpe.typeSymbol.name

    val appliers = fields.map { accessor =>
      q""" "  " + ${accessor.name.toString} + " = " + $packagePrefix.Shows[${accessor.paramType}].show(
        instance.${accessor.name}
      )"""
    }

    val t = q"_root_.scala.collection.immutable.List.apply(..$appliers)"

    q"""
      new $packagePrefix.Shows[$tpe] {
        def show(instance: $tpe): $stringType = {
          ${cmp.toString} + "(\n" + $t.mkString("\n") + "\n)"
        }
      }
    """
  }
}
