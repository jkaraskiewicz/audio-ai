package com.karaskiewicz.scribely.utils

import android.content.Context
import android.net.Uri
import androidx.test.core.app.ApplicationProvider
import com.karaskiewicz.scribely.TestApplication
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.mockito.kotlin.whenever
import kotlin.test.assertNull

@RunWith(RobolectricTestRunner::class)
@Config(
  // Specify Android SDK version
  sdk = [28],
  application = TestApplication::class,
)
class FileUtilsTest {
  @Mock
  private lateinit var mockUri: Uri

  private lateinit var context: Context

  @Before
  fun setup() {
    MockitoAnnotations.openMocks(this)
    context = ApplicationProvider.getApplicationContext()
  }

  @Test
  fun `copyUriToTempFile handles invalid URI gracefully`() {
    // Given - Create a mock URI that will fail to open stream
    whenever(mockUri.scheme).thenReturn("content")
    whenever(mockUri.path).thenReturn("/invalid/path")
    whenever(mockUri.toString()).thenReturn("content://invalid/path")

    // When
    val result = FileUtils.copyUriToTempFile(context, mockUri)

    // Then - Should return null for invalid URI
    assertNull(result)
  }

  @Test
  fun `copyUriToTempFile handles exception gracefully`() {
    // Given - Create a mock URI that will cause an exception
    whenever(mockUri.scheme).thenReturn("invalid")
    whenever(mockUri.path).thenReturn(null)
    whenever(mockUri.toString()).thenReturn("invalid://malformed")

    // When
    val result = FileUtils.copyUriToTempFile(context, mockUri)

    // Then - Should return null when exceptions occur
    assertNull(result)
  }

  @Test
  fun `copyUriToTempFile handles mock URI gracefully`() {
    // Given
    whenever(mockUri.toString()).thenReturn("content://example.com/test.mp3")

    // When
    val result = FileUtils.copyUriToTempFile(context, mockUri)

    // Then - In test environment this will fail gracefully and return null
    // which is the expected behavior for invalid/inaccessible URIs
    assertNull(result)
  }

  // Note: getFileName is private, so we test it indirectly through copyUriToTempFile
  // The private function handles MIME type detection and filename generation
}
