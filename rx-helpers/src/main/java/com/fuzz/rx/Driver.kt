package com.fuzz.rx

import android.os.Looper
import android.support.annotation.MainThread
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.rxkotlin.subscribeBy

/**
 * Description: Represents an observable sequence with following properties:
 *
 * 1. It never fails
 * 2. It delivers events on the [AndroidSchedulers.mainThread]
 * 3. It replays the last event when subscribed to.
 * 4. It can have multiple subscribers.
 * 5. [drive] must happen on the main thread, and will return results on the main thread.
 */
@MainThread
class Driver<T : Any> internal constructor(source: Observable<T>) {

    private val source: Observable<T> = source.replay(1).share()

    /**
     * Converts the [Driver] back to an [Observable].
     */
    fun asObservable(): Observable<T> = source

    /**
     * Creates a new subscription and sends elements to the [Observer]. Logically equivalent
     * to the [Observable.bindTo] method.
     */
    fun drive(observer: Observer<T>): Disposable {
        assertOnMainThread()
        return asObservable().bindTo(observer)
    }

    /**
     * Creates a new subscription and sends elements to the [Consumer]. Logically equivalent
     * to the [Observable.bindTo] method.
     */
    fun drive(observer: Consumer<in T>): Disposable {
        assertOnMainThread()
        return asObservable().bindTo(observer)
    }


    /**
     * Subscribes to [Observable] sequence using custom binder function.
     */
    fun <R> drive(transformation: (Observable<T>) -> R): R {
        assertOnMainThread()
        return transformation(asObservable())
    }

    /**
     * Subscribes [onNext] and [onComplete] to the underlying [Observable].
     */
    fun drive(onComplete: () -> Unit = {},
              onNext: (T) -> Unit = {}): Disposable {
        assertOnMainThread()
        return asObservable().subscribeBy(onComplete = onComplete, onNext = onNext)
    }

    private fun assertOnMainThread() {
        if (Thread.currentThread() != Looper.getMainLooper().thread) {
            throw MethodCalledFromWrongThreadException()
        }
    }
}

internal class MethodCalledFromWrongThreadException : RuntimeException("This method must be called on a main thread.")

/**
 * Converts [Observable] to a [Driver]. [onErrorJustReturn] is the value to return when error occurs in the
 * source. It shares the source so multiple subscribers can bind to it.
 */
fun <T : Any> Observable<T>.asDriver(onErrorJustReturn: T): Driver<T> {
    val source = observeOn(AndroidSchedulers.mainThread())
            .onErrorReturnItem(onErrorJustReturn)
    return Driver(source)
}

/**
 * Converts [Observable] to a [Driver]. [onErrorDriveWith] is the [Driver] to continue with in case of error.
 * It shares the source so multiple subscribers can bind to it.
 */
fun <T : Any> Observable<T>.asDriver(onErrorDriveWith: Driver<T>): Driver<T> {
    val source = observeOn(AndroidSchedulers.mainThread())
            .onExceptionResumeNext(onErrorDriveWith.asObservable())
    return Driver(source)
}

/**
 * Converts [Observable] to a [Driver]. [onErrorReceiver] is calculated when an error occurs.
 * It shares the source so multiple subscribers can bind to it.
 */
fun <T : Any> Observable<T>.asDriver(onErrorReceiver: (Throwable) -> Driver<T>): Driver<T> {
    val source = observeOn(AndroidSchedulers.mainThread())
            .onErrorResumeNext { t: Throwable -> onErrorReceiver(t).asObservable() }
    return Driver(source)
}

