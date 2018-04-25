package com.fuzz.rx

import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single

fun <T> optionalOf(value: T?) = Optional(value)

/**
 * Description: A class with optional value. Useful for returning nullable type in RX. This class
 * is properly typed and is useful as a replacement for Java Optional class.
 */
data class Optional<out T>(val value: T?)

/**
 * Only emit if the [Optional.value] exists.
 */
fun <T> Observable<Optional<T>>.filterValue(): Observable<T> = filterFlatMap({ it.value != null }) { it.value!! }


/**
 * Only emit if the [Optional.value] exists.
 */
fun <T> Single<Optional<T>>.filterValue(): Maybe<T> = filterFlatMap({ it.value != null }) { it.value!! }

/**
 * Only emit if the [Optional.value] exists.
 */
fun <T> Maybe<Optional<T>>.filterValue(): Maybe<T> = filterFlatMap({ it.value != null }) { it.value!! }

/**
 * Only emit if the [Optional.value] exists.
 */
fun <T> Flowable<Optional<T>>.filterValue(): Flowable<T> = filterFlatMap({ it.value != null }) { it.value!! }

/**
 * Turns an [Observable] of type [T] into an [Observable] of type [Optional][T]
 */
fun <T> Observable<T>.mapOptional(): Observable<Optional<T>> = map { Optional(it) }

/**
 * Turns a [Flowable] of type [T] into an [Flowable] of type [Optional][T]
 */
fun <T> Flowable<T>.mapOptional(): Flowable<Optional<T>> = map { Optional(it) }