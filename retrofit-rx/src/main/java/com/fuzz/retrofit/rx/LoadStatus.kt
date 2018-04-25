package com.fuzz.retrofit.rx

import com.jakewharton.retrofit2.adapter.rxjava2.Result
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import io.reactivex.rxkotlin.subscribeBy

/**
 * Represent Loading state in an app.
 */
sealed class LoadStatus {

    /**
     * State is actively loading.
     */
    object Loading : LoadStatus() {
        operator fun invoke() = Loading
    }

    /**
     * State is error. Show error.
     */
    data class Error(val error: Throwable) : LoadStatus()

    /**
     * State completed successfully. Show success.
     */
    object Success : LoadStatus() {
        operator fun invoke() = Success
    }

    /**
     * State is initialized. We have not loaded anything yet.
     */
    object None : LoadStatus() {
        operator fun invoke() = None
    }
}

/**
 * Tells a [LoadStatus] to start loading state when called.
 */
fun <T> Observable<T>.startLoading(loadStatus: Observer<LoadStatus>): Observable<T> =
        doOnNext { loadStatus.onNext(LoadStatus.Loading()) }

/**
 * Tells a [LoadStatus] to start loading state when called.
 */
fun <T> Observable<T>.startLoading(loadStatus: Consumer<LoadStatus>): Observable<T> =
        doOnNext { loadStatus.accept(LoadStatus.Loading()) }

/**
 * Applies load status calls to the [Observable] chain. Since we're binding [LoadStatus],
 * errors will not get thrown, rather logged to the console. It is up to implementer to ensure
 * that errors are handled properly.
 */
@Suppress("UNCHECKED_CAST")
fun <T : Any> Observable<Result<T>>.bindToLoadStatus(loadStatus: Observer<LoadStatus>,
                                                     onError: (Throwable) -> Unit = {},
                                                     onComplete: () -> Unit = {},
                                                     onNext: (T) -> Unit = {}
): Disposable =
        subscribeBy(onError = {
            loadStatus.onNext(LoadStatus.Error(it))
            onError(it)
        }, onNext = {
            when {
                it.isSuccess -> {
                    loadStatus.onNext(LoadStatus.Success)
                    onNext(it.requireValue())
                }
                it.isError -> {
                    loadStatus.onNext(LoadStatus.Error(it.error()))
                    onError(it.error())
                }
            }
        }, onComplete = onComplete)

/**
 *  Filter down to errors only from a [LoadStatus]
 */
@Suppress("UNCHECKED_CAST")
fun Observable<LoadStatus>.filterErrors(): Observable<LoadStatus.Error> =
        filter { it is LoadStatus.Error }
                .map { it as LoadStatus.Error }

/**
 *  Filter down to errors only from a [LoadStatus]
 */
@JvmName("filterSuccessStatus")
fun Observable<LoadStatus>.filterSuccess(): Observable<Unit> =
        filter { it === LoadStatus.Success }
                .map { it as LoadStatus.Success }
                .map { Unit }


@Suppress("UNCHECKED_CAST")
/**
 * Applies load status calls to the [Observable] chain. Since we're binding [LoadStatus],
 * errors will not get thrown, rather logged to the console. It is up to implementer to ensure
 * that errors are handled properly.
 */
@JvmName("bindToLoadStatusOut")
fun <T : Any> Observable<Result<out T>>.bindToLoadStatus(loadStatus: Observer<LoadStatus>,
                                                         onError: (Throwable) -> Unit = {},
                                                         onComplete: () -> Unit = {},
                                                         onNext: (T) -> Unit = {}
): Disposable =
        (this as Observable<Result<T>>).bindToLoadStatus(loadStatus, onError, onComplete, onNext)


/**
 * If [LoadStatus.Loading] is true, return true to state load status is loading.
 */
fun Observable<LoadStatus>.mapToLoadingState(): Observable<Boolean> = map { it == LoadStatus.Loading }
