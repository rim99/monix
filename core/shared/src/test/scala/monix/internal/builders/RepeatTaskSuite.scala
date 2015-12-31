/*
 * Copyright (c) 2014-2016 by its authors. Some rights reserved.
 * See the project homepage at: https://monix.io
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

package monix.internal.builders

import minitest.TestSuite
import monix.concurrent.Scheduler
import monix.concurrent.schedulers.TestScheduler
import monix.Ack.Continue
import monix.Observable

object RepeatTaskSuite extends TestSuite[TestScheduler] {
  def setup() = TestScheduler()
  def tearDown(s: TestScheduler): Unit = {
    assert(s.state.get.tasks.isEmpty,
      "TestScheduler should have no pending tasks")
  }

  test("first execution is async") { implicit s =>
    var received = 0
    Observable.repeatTask(1)
      .take(1).foreach(x => received += x)

    assertEquals(received, 0)
    s.tickOne()
    assertEquals(received, 1)
    s.tickOne()
  }

  test("should do synchronous execution in batches") { implicit s =>
    var received = 0
    Observable.repeatTask(1)
      .take(Scheduler.recommendedBatchSize * 2)
      .subscribe { x => received += 1; Continue }

    s.tickOne()
    assertEquals(received, Scheduler.recommendedBatchSize)
    s.tickOne()
    assertEquals(received, Scheduler.recommendedBatchSize * 2)
    s.tickOne()
  }
}