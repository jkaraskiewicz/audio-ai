package com.karaskiewicz.scribely.utils

import timber.log.Timber

/**
 * Utility functions for working with Kotlin Result class
 * to reduce try/catch boilerplate and improve error handling
 *
 * Safely executes a network operation and logs errors
 */
inline fun <T> safeNetworkCall(
  operation: String,
  crossinline block: () -> T,
): Result<T> =
  runCatching {
    block()
  }.onFailure { exception ->
    Timber.e(exception, "Network operation failed: $operation")
  }

// Safely executes a suspending network operation and logs errors
suspend inline fun <T> safeSuspendNetworkCall(
  operation: String,
  crossinline block: suspend () -> T,
): Result<T> =
  runCatching {
    block()
  }.onFailure { exception ->
    Timber.e(exception, "Network operation failed: $operation")
  }

/**
 * Safely executes a file operation and logs errors
 */
inline fun <T> safeFileOperation(
  operation: String,
  crossinline block: () -> T,
): Result<T> =
  runCatching {
    block()
  }.onFailure { exception ->
    Timber.e(exception, "File operation failed: $operation")
  }

/**
 * Safely executes a media operation and logs errors
 */
inline fun <T> safeMediaOperation(
  operation: String,
  crossinline block: () -> T,
): Result<T> =
  runCatching {
    block()
  }.onFailure { exception ->
    Timber.e(exception, "Media operation failed: $operation")
  }

/**
 * Safely executes any operation with custom error logging
 */
inline fun <T> safeOperation(
  operation: String,
  crossinline block: () -> T,
): Result<T> =
  runCatching {
    block()
  }.onFailure { exception ->
    Timber.e(exception, "Operation failed: $operation")
  }

/**
 * Maps a Result to a domain-specific result type
 */
inline fun <T, R> Result<T>.mapToResult(
  onSuccess: (T) -> R,
  onFailure: (Throwable) -> R,
): R =
  fold(
    onSuccess = onSuccess,
    onFailure = onFailure,
  )

/**
 * Converts a nullable value to Result
 */
fun <T> T?.toResult(errorMessage: String): Result<T> =
  if (this != null) {
    Result.success(this)
  } else {
    Result.failure(IllegalStateException(errorMessage))
  }

/**
 * Converts a boolean result to Result<Unit>
 */
fun Boolean.toResult(errorMessage: String): Result<Unit> =
  if (this) {
    Result.success(Unit)
  } else {
    Result.failure(IllegalStateException(errorMessage))
  }
