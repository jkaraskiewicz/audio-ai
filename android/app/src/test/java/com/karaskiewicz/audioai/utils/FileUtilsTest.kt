package com.karaskiewicz.audioai.utils

import android.content.Context
import android.net.Uri
import androidx.test.core.app.ApplicationProvider
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.whenever
import kotlin.test.assertNull

@RunWith(MockitoJUnitRunner::class)
class FileUtilsTest {

  @Mock
  private lateinit var mockUri: Uri

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