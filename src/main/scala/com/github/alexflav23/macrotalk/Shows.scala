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

trait Shows[In] {
  def show(z: In): String
}

object Shows {

  // commonly used trick to give us val ev = Shows[In] syntax, instead of using implicitly.
  def apply[In](implicit ev: Shows[In]): Shows[In] = ev


  // now the fun part. The name of this method doesn't matter.
  implicit def materialize[In]: Shows[In] = macro ShowsMacros.materialize[In]
}
