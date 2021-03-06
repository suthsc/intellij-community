/*
 * Copyright 2000-2015 JetBrains s.r.o.
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
package org.jetbrains.concurrency

import com.intellij.util.Function

inline fun <T, SUB_RESULT> Promise<T>.then(crossinline handler: (T) -> SUB_RESULT) = then(object : Function<T, SUB_RESULT> {
  override fun `fun`(param: T) = handler(param)
})

inline fun <T, SUB_RESULT> Promise<T>.thenAsync(crossinline handler: (T) -> Promise<SUB_RESULT>) = then(object : AsyncFunction<T, SUB_RESULT> {
  override fun `fun`(param: T) = handler(param)
})

inline fun <T> Promise<T>.thenAsyncVoid(crossinline handler: (T) -> Promise<*>) = then(object : AsyncFunction<T, Any?> {
  override fun `fun`(param: T): Promise<Any?> {
    @Suppress("UNCHECKED_CAST")
    return handler(param) as Promise<Any?>
  }
})

fun ResolvedPromise(): Promise<*> = Promise.DONE