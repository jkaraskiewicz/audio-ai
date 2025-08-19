package com.karaskiewicz.scribely.utils

import android.content.Context
import android.net.Uri
import androidx.test.core.app.ApplicationProvider
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.mockito.kotlin.whenever
import kotlin.test.assertNull

@RunWith(RobolectricTestRunner::class)
class FileUtilsTest {
  @Mock
  private lateinit var mockUri: Uri

  @Before
  fun setup() {
    MockitoAnnotations.openMocks(this)
  }

  @Test
  fun `copyUriToTempFile returns null for invalid URI`() {
    // Given
    val context = ApplicationProvider.getApplicationContext<Context>()
    whenever(mockUri.scheme).thenReturn("invalid")

    // When
    val result = FileUtils.copyUriToTempFile(context, mockUri)

    // Then
    assertNull(result)
  }
}
