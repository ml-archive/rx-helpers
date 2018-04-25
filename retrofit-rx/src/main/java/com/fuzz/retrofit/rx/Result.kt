package com.fuzz.retrofit.rx

import com.jakewharton.retrofit2.adapter.rxjava2.Result
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single


val <T> Result<T>.value: T?
    get() = response().body()

fun <T> Result<T>.requireValue() = value
        ?: throw NullPointerException("Null value found for $this")

val <T> Result<T>.isSuccess: Boolean
    get() = !isError

/**
 * Filters out success state and maps it to the success type.
 */
fun <T> Observable<Result<T>>.filterSuccess(): Observable<T> =
        filter { it.isSuccess }
                .map { it.requireValue() }

/**
 * Filters out success state and maps it to the success type.
 */
fun <T> Observable<out Result<out T>>.filterSuccessOut(): Observable<T> =
        filter { it.isSuccess }
                .map { it.requireValue() }

/**
 * Filters out success state and maps it to the success type.
 */
fun <T> Single<Result<T>>.filterSuccess(): Maybe<T> =
        filter { it.isSuccess }
                .map { it.requireValue() }

/**
 * Filters out success state and maps it to the success type.
 */
fun <T> Single<out Result<out T>>.filterSuccessOut(): Maybe<T> =
        filter { it.isSuccess }
                .map { it.requireValue() }

/**
 * Filters out success state and maps it to the success type.
 */
@Suppress("UNCHECKED_CAST")
fun <T> Observable<Result<T>>.filterFailures(): Observable<Throwable> =
        filter { it.isError }
                .map { it.error() }

@Suppress("UNCHECKED_CAST")
fun <T> Single<Result<T>>.filterFailures(): Maybe<Throwable> =
        filter { it.isError }
                .map { it.error() }

@Suppress("UNCHECKED_CAST")
fun <T> Observable<out Result<out T>>.filterFailuresOut(): Observable<Throwable> =
        filter { it.isError }
                .map { it.error() }

@Suppress("UNCHECKED_CAST")
fun <T> Single<out Result<out T>>.filterFailuresOut(): Maybe<Throwable> =
        filter { it.isError }
                .map { it.error() }

/**
 * Do something on success state only.
 */
inline fun <T> Observable<Result<T>>.doOnSuccess(crossinline onSuccess: (T) -> Unit): Observable<Result<T>> =
        doOnNext { if (it.isSuccess) onSuccess(it.requireValue()) }

/**
 * Do something on failure state only.
 */
inline fun <T> Observable<Result<T>>.doOnFailure(crossinline onFailure: (Throwable) -> Unit): Observable<Result<T>> =
        doOnNext { if (it.isError) onFailure(it.error()) }

/**
 * Do something on success state only.
 */
inline fun <T> Single<Result<T>>.doOnSuccess(crossinline onSuccess: (T) -> Unit): Single<Result<T>> =
        doOnSuccess { if (it.isSuccess) onSuccess(it.requireValue()) }

/**
 * Do something on failure state only.
 */
inline fun <T> Single<Result<T>>.doOnFailure(crossinline onFailure: (Throwable) -> Unit): Single<Result<T>> =
        doOnSuccess { if (it.isError) onFailure(it.error()) }

