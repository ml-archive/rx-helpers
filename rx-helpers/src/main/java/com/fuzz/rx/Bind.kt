package com.fuzz.rx

import io.reactivex.Emitter
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.exceptions.OnErrorNotImplementedException
import io.reactivex.functions.Consumer
import io.reactivex.rxkotlin.subscribeBy
import timber.log.Timber

/**
 * Propagate the exception to throw in app if [BuildConfig.DEBUG]
 */
private val onErrorStub: (Throwable) -> Unit = {
    if (BuildConfig.DEBUG) {
        throw OnErrorNotImplementedException(it)
    } else {
        Timber.e(it)
    }
}

private val onCompleteStub: () -> Unit = {}

/**
 * This method subscribes all the methods from the source [Observable] onto the specified [Observer].
 */
fun <T : Any> Observable<T>.bindTo(observer: Observer<in T>) = subscribeBy(
        onNext = observer::onNext,
        onError = observer::onError,
        onComplete = observer::onComplete
)

/**
 * A convenience method that subscribes all the methods from the source [Observable] onto the specified [Observer] on
 * the [AndroidSchedulers.mainThread].
 */
fun <T : Any> Observable<T>.bindToMain(observer: Observer<in T>) = observeOn(AndroidSchedulers.mainThread()).bindTo(observer)

/**
 * This method subscribes all the methods from the source [Flowable] onto the specified [Observer].
 * This is very similiar to iOS' bind operator.
 */
fun <T : Any> Flowable<T>.bindTo(observer: Observer<in T>) = subscribeBy(
        onNext = observer::onNext,
        onError = observer::onError,
        onComplete = observer::onComplete
)

/**
 * A convenience method that subscribes all the methods from the source [Flowable] onto the specified [Observer] on
 * the [AndroidSchedulers.mainThread].
 */
fun <T : Any> Flowable<T>.bindToMain(observer: Observer<in T>) = observeOn(AndroidSchedulers.mainThread()).bindTo(observer)

/**
 * This method subscribes all the methods from the source [Maybe] onto the specified [Observer].
 * This is very similiar to iOS' bind operator.
 */
fun <T : Any> Maybe<T>.bindTo(observer: Observer<in T>) = subscribeBy(
        onSuccess = observer::onNext,
        onError = observer::onError,
        onComplete = observer::onComplete
)

/**
 * A convenience method that subscribes all the methods from the source [Maybe] onto the specified [Observer] on
 * the [AndroidSchedulers.mainThread].
 */
fun <T : Any> Maybe<T>.bindToMain(observer: Observer<in T>) = observeOn(AndroidSchedulers.mainThread()).bindTo(observer)

/**
 * This method subscribes all the methods from the source [Single] onto the specified [Observer].
 */
fun <T : Any> Single<T>.bindTo(observer: Observer<in T>) = subscribeBy(
        onSuccess = observer::onNext,
        onError = observer::onError
)

/**
 * A convenience method that subscribes all the methods from the source [Single] onto the specified [Observer] on
 * the [AndroidSchedulers.mainThread].
 * This is very similiar to iOS' bind operator.
 */
fun <T : Any> Single<T>.bindToMain(observer: Observer<in T>) = observeOn(AndroidSchedulers.mainThread()).bindTo(observer)

/**
 * Binds to a [Consumer]. Since [Consumer] do not have errors, the subscribe will not error out.
 */
fun <T : Any> Observable<T>.bindTo(observer: Consumer<in T>) = subscribeBy(
        onNext = observer::accept,
        onError = onErrorStub,
        onComplete = onCompleteStub
)

/**
 * A convenience method that subscribes all the methods from the source [Observable] onto the specified [Consumer] on
 * the [AndroidSchedulers.mainThread].
 */
fun <T : Any> Observable<T>.bindToMain(observer: Consumer<in T>) = observeOn(AndroidSchedulers.mainThread()).bindTo(observer)

/**
 * This method subscribes all the methods from the source [Observable] onto the specified [Emitter].
 */
fun <T : Any> Observable<T>.bindTo(observer: Emitter<in T>) = subscribeBy(
        onNext = observer::onNext,
        onError = observer::onError,
        onComplete = observer::onComplete
)


/**
 * A convenience method that subscribes all the methods from the source [Observable] onto the specified [Emitter] on
 * the [AndroidSchedulers.mainThread].
 * This is very similiar to iOS' bind operator.
 */
fun <T : Any> Observable<T>.bindToMain(observer: Emitter<in T>) = observeOn(AndroidSchedulers.mainThread()).bindTo(observer)

/**
 * Binds the onNext of the [Flowable] onto the [Consumer]. Since a consumer cannot error out,
 * we stub onError with an exception in debug builds, while log on error.
 */
fun <T : Any> Flowable<T>.bindTo(observer: Consumer<in T>) = subscribeBy(
        onNext = observer::accept,
        onError = onErrorStub,
        onComplete = onCompleteStub
)

/**
 * A convenience method that subscribes all the methods from the source [Flowable] onto the specified [Consumer] on
 * the [AndroidSchedulers.mainThread].
 */
fun <T : Any> Flowable<T>.bindToMain(observer: Consumer<in T>) = observeOn(AndroidSchedulers.mainThread()).bindTo(observer)


/**
 * Binds the onNext of the [Maybe] onto the [Consumer]. Since a consumer cannot error out,
 * we stub onError with an exception in debug builds, while log on error.
 */
fun <T : Any> Maybe<T>.bindTo(observer: Consumer<in T>) = subscribeBy(
        onSuccess = observer::accept,
        onError = onErrorStub,
        onComplete = onCompleteStub
)

/**
 * A convenience method that subscribes all the methods from the source [Maybe] onto the specified [Consumer] on
 * the [AndroidSchedulers.mainThread].
 */
fun <T : Any> Maybe<T>.bindToMain(observer: Consumer<in T>) = observeOn(AndroidSchedulers.mainThread()).bindTo(observer)
