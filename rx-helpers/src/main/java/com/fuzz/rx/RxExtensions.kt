@file:Suppress("NOTHING_TO_INLINE")

package com.fuzz.rx

import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.Observer
import io.reactivex.Single
import io.reactivex.functions.Consumer
import io.reactivex.internal.util.HalfSerializer.onNext
import io.reactivex.rxkotlin.withLatestFrom
import java.util.concurrent.TimeUnit

/**
 * Simplifies with latest from to register on the source [Observable] to only return the other
 * type that we care about.
 */
fun <T> Observable<*>.withLatestFromOther(other: ObservableSource<T>): Observable<T> = withLatestFrom(other) { _, t -> t }

/**
 * Shorthand for [onNext] with [Unit] passed in
 */
fun Observer<Unit>.onNext() = onNext(Unit)

/**
 * Shorthand for [accept] with [Unit].
 */
fun Consumer<Unit>.accept() = accept(Unit)

/**
 * When a subscriber subscribes, this pushes the previous value (if any) and then the new value.
 * This allows displaying the data in two times:
 *   - immediately show a cached value
 *   - then show a fresh value
 */
fun <T : Any> Observable<T>.cacheAndFresh(): Observable<T> {
    var cache: T? = null

    return Observable.create<T> { subscriber ->
        cache?.let { subscriber.onNext(it) }

        this.doOnNext { cache = it }
                .bindTo(subscriber)
    }
}

/**
 * Combines a set of [Consumer] to respond to a single [Consumer] chain.
 */
fun <T> combineConsumers(vararg consumer: Consumer<in T>): Consumer<in T> = Consumer { value ->
    consumer.forEach { it.accept(value) }
}

/**
 * Default throttle useful for UI button clicks to prevent rapid clicks.
 */
fun <T> Observable<T>.defaultThrottle(): Observable<T> {
    return this.throttleFirst(1, TimeUnit.SECONDS)
}

/**
 * Default that throttles last call within a specified window.
 */
fun <T> Observable<T>.defaultThrottleLast(): Observable<T> {
    return this.throttleLast(500, TimeUnit.MILLISECONDS)
}

/**
 * Zip together the [list] and just complete at unit.
 */
inline fun <T> zipTogether(list: List<Observable<T>>): Observable<Unit> = Observable.zip(list) { Unit }

/**
 * Zip together the [list] and just complete at unit.
 */
inline fun <T> zipTogether(list: List<Single<T>>): Single<Unit> = Single.zip(list) { Unit }

