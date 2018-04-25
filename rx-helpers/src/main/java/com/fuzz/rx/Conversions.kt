package com.fuzz.rx

import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import org.reactivestreams.Publisher

/**
 * Alias for [Observable.just]. Nicer syntax.
 */
fun <T> T.asObservable(): Observable<T> = Observable.just(this)

/**
 * Alias for [Maybe.just]
 */
fun <T> T.asMaybe(): Maybe<T> = Maybe.just(this)

/**
 * Alias for [Flowable.just]
 */
fun <T> T.asFlowable(): Flowable<T> = Flowable.just(this)

/**
 * Alias for [Single.just]
 */
fun <T> T.asSingle(): Single<T> = Single.just(this)

/**
 * Alias for [Flowable.fromPublisher]
 */
fun <T, P : Publisher<T>> P.asFlowable(): Flowable<T> = Flowable.fromPublisher(this)

/**
 * Alias for [Observable.fromPublisher]
 */
fun <T, P : Publisher<T>> P.asObservable(): Observable<T> = Observable.fromPublisher(this)
