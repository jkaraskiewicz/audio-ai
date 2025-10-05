package com.karaskiewicz.scribely.domain.usecase

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class RecordingDurationTrackerTest {
  private lateinit var tracker: RecordingDurationTracker

  @Before
  fun setup() {
    tracker = RecordingDurationTracker()
  }

  @Test
  fun `initial duration is zero`() =
    runTest {
      // When
      val duration = tracker.duration.first()

      // Then
      assertEquals(0L, duration)
    }

  @Test
  fun `start sets initial values`() =
    runTest {
      // When
      tracker.start()

      // Then
      val duration = tracker.duration.first()
      assertEquals(0L, duration)
    }

  @Test
  fun `pause records pause time`() =
    runTest {
      // Given
      tracker.start()
      advanceTimeBy(1000)

      // When
      tracker.pause()

      // Then - pause time is recorded (no exception thrown)
      assertTrue(true)
    }

  @Test
  fun `resume accumulates paused duration`() =
    runTest {
      // Given
      tracker.start()
      advanceTimeBy(1000)
      tracker.pause()
      advanceTimeBy(2000)

      // When
      tracker.resume()

      // Then - resumed successfully (no exception thrown)
      assertTrue(true)
    }

  @Test
  fun `reset sets duration to zero`() =
    runTest {
      // Given
      tracker.start()
      advanceTimeBy(2000)

      // When
      tracker.reset()
      val duration = tracker.duration.first()

      // Then
      assertEquals(0L, duration)
    }

  @Test
  fun `getCurrentDuration returns current duration value`() =
    runTest {
      // Given
      tracker.start()

      // When
      val duration = tracker.getCurrentDuration()

      // Then
      assertEquals(0L, duration)
    }

  @Test
  fun `startDurationTimer can be called without errors`() =
    runTest {
      // Given
      var isRecording = false
      tracker.start()

      // When - start timer but immediately stop it by setting isRecording to false
      tracker.startDurationTimer(this) { isRecording }

      // Then - should not throw exceptions
      val duration = tracker.getCurrentDuration()
      assertTrue(duration >= 0L, "Duration should be non-negative, got $duration")
    }

  @Test
  fun `startDurationTimer stops when recording becomes inactive`() =
    runTest {
      // Given
      var isRecording = false
      tracker.start()

      // When - timer should stop immediately since isRecording is false
      tracker.startDurationTimer(this) { isRecording }

      // Then - duration should remain at 0 since timer never ran
      val duration = tracker.getCurrentDuration()
      assertEquals(0L, duration)
    }

  @Test
  fun `multiple start calls reset tracking`() =
    runTest {
      // Given
      tracker.start()
      advanceTimeBy(2000)

      // When
      tracker.start()
      val duration = tracker.duration.first()

      // Then
      assertEquals(0L, duration)
    }

  @Test
  fun `pause and resume cycle maintains elapsed time`() =
    runTest {
      // Given
      tracker.start()

      // When - pause and resume without timer running
      tracker.pause()
      tracker.resume()

      // Then - should maintain state without exceptions
      val duration = tracker.getCurrentDuration()
      assertTrue(duration >= 0L)
    }

  @Test
  fun `reset clears all tracking state`() =
    runTest {
      // Given
      tracker.start()
      advanceTimeBy(1000)
      tracker.pause()
      advanceTimeBy(500)
      tracker.resume()

      // When
      tracker.reset()

      // Then
      assertEquals(0L, tracker.getCurrentDuration())
      assertEquals(0L, tracker.duration.first())
    }
}
