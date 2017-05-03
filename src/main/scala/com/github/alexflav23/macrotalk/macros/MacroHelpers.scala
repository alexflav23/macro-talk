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
trait MacroHelpers {
  val c: blackbox.Context

  import c.universe._

  object CaseField {
    def unapply(arg: TermSymbol): Option[(Name, Type)] = {
      if (arg.isVal && arg.isCaseAccessor) {
        Some(TermName(arg.name.toString.trim) -> arg.typeSignature.dealias)
      } else {
        None
      }
    }
  }

  /**
    * Retrieves the accessor fields on a case class and returns an iterable of tuples of the form Name -> Type.
    * For every single field in a case class, a reference to the string name and string type of the field are returned.
    *
    * Example:
    *
    * {{{
    *   case class Test(id: UUID, name: String, age: Int)
    *
    *   accessors(Test) = Iterable("id" -> "UUID", "name" -> "String", age: "Int")
    * }}}
    *
    * @param tpe The input type of the case class definition.
    * @return An iterable of tuples where each tuple encodes the string name and string type of a field.
    */
  def fields(tpe: Type): Iterable[Accessor] = {
    tpe.decls.collect { case CaseField(name, fType) => {
      Accessor(name.toTermName, fType)
    }}
  }

  def tupleTerm(i: Int): TermName = TermName("_" + (i + 1).toString)

  def isTuple(tpe: Type): Boolean = isTuple(tpe.typeSymbol)

  def isTuple(sym: Symbol): Boolean = {
    sym.fullName startsWith "scala.Tuple"
  }

  def isCaseClass(tpe: Type): Boolean = isCaseClass(tpe.typeSymbol)

  def isCaseClass(sym: Symbol): Boolean = {
    sym.isClass && sym.asClass.isCaseClass
  }

  case class Accessor(
    name: TermName,
    paramType: Type
  )
}
