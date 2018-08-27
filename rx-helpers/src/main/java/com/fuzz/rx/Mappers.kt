@file:Suppress("NOTHING_TO_INLINE")

package com.fuzz.rx

import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single

/**
 * Convenience operator for mapping any observable into a single [Unit] type.
 */
inline fun Observable<*>.mapUnit(): Observable<Unit> = map { Unit }

/**
 * Maps an [Observable] of [CharSequence] into [Observable] of [String].
 */
fun Observable<CharSequence>.mapToString(): Observable<String> = map { it.toString() }

/**
 * Reverses the [Boolean] value.
 */
fun Observable<Boolean>.mapInverse(): Observable<Boolean> = map { !it }

/**
 * Filter and flat maps the passed code in one go without extra chain.
 */
inline fun <T, R> Observable<T>.filterFlatMapObservable(
        crossinline filter: (T) -> Boolean,
        crossinline mapper: (T) -> Observable<R>): Observable<R> = flatMap {
    return@flatMap if (filter(it)) {
        mapper(it)
    } else Observable.empty()
}

/**
 * Filter and flat maps the passed code in one go without extra chain. [mapper] is converted to an [Observable]
 * to satisfy [Observable.empty] in flatmap.
 */
inline fun <T, R> Observable<T>.filterFlatMap(
        crossinline filter: (T) -> Boolean,
        crossinline mapper: (T) -> R): Observable<R> = filterFlatMapObservable(filter) { mapper(it).asObservable() }

/**
 * Filter and flat maps the passed code in one go without extra chain.
 */
inline fun <T, R> Flowable<T>.filterFlatMapFlowable(
        crossinline filter: (T) -> Boolean,
        crossinline mapper: (T) -> Flowable<R>): Flowable<R> = flatMap {
    return@flatMap if (filter(it)) {
        mapper(it)
    } else Flowable.empty()
}

/**
 * Filter and flat maps the passed code in one go without extra chain. [mapper] is converted to a [Flowable]
 * to satisfy [Flowable.empty] in flatmap.
 */
inline fun <T, R> Flowable<T>.filterFlatMap(
        crossinline filter: (T) -> Boolean,
        crossinline mapper: (T) -> R): Flowable<R> = filterFlatMapFlowable(filter) { mapper(it).asFlowable() }

/**
 * Filter and flat maps the passed code in one go without extra chain.
 */
inline fun <T, R> Single<T>.filterFlatMapSingle(
        crossinline filter: (T) -> Boolean,
        crossinline mapper: (T) -> Single<R>): Maybe<R> = flatMapMaybe {
    return@flatMapMaybe if (filter(it)) {
        mapper(it).toMaybe()
    } else Maybe.empty<R>()
}

/**
 * Filter and flat maps the passed code in one go without extra chain. [mapper] is converted to an [Single]
 * to satisfy [Single.never] in flatmap.
 */
inline fun <T, R> Single<T>.filterFlatMap(
        crossinline filter: (T) -> Boolean,
        crossinline mapper: (T) -> R): Maybe<R> = filterFlatMapSingle(filter) { mapper(it).asSingle() }

/**
 * Filter and flat maps the passed code in one go without extra chain.
 */
inline fun <T, R> Maybe<T>.filterFlatMapMaybe(
        crossinline filter: (T) -> Boolean,
        crossinline mapper: (T) -> Maybe<R>): Maybe<R> = flatMap {
    return@flatMap if (filter(it)) {
        mapper(it)
    } else Maybe.empty<R>()
}

/**
 * Filter and flat maps the passed code in one go without extra chain. [mapper] is converted to an [Maybe]
 * to satisfy [Maybe.empty] in flatmap.
 */
inline fun <T, R> Maybe<T>.filterFlatMap(
        crossinline filter: (T) -> Boolean,
        crossinline mapper: (T) -> R): Maybe<R> = filterFlatMapMaybe(filter) { mapper(it).asMaybe() }

/**
 * Only emits values of type [R] and returns an [Observable] of [R]. This is similar to [Observable.cast]
 * except it is a safe and does not cause a [ClassCastException].
 */
inline fun <T, reified R : T> Observable<T>.filterTo(): Observable<R> =
        filterFlatMap({ it is R }, { it as R })

/**
 * Only emits values of type [R] and returns a [Flowable] of [R]. This is similar to [Observable.cast]
 * except it is a safe and does not cause a [ClassCastException].
 */
inline fun <T, reified R : T> Flowable<T>.filterTo(): Flowable<R> =
        filterFlatMap({ it is R }, { it as R })

/**
 * Only emits values of type [R] and returns a [Maybe] of [R]. This is similar to [Observable.cast]
 * except it is a safe and does not cause a [ClassCastException].
 */
inline fun <T, reified R : T> Single<T>.filterTo(): Maybe<R> =
        filterFlatMap({ it is R }, { it as R })

/**
 * Only emits values of type [R] and returns a [Maybe] of [R]. This is similar to [Observable.cast]
 * except it is a safe and does not cause a [ClassCastException].
 */
inline fun <T, reified R : T> Maybe<T>.filterTo(): Maybe<R> =
        filterFlatMap({ it is R }, { it as R })