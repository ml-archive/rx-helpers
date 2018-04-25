package com.fuzz.rx

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.addTo

/**
 * Maps the [CompositeDisposable] to a similar name to iOS.
 */
typealias DisposeBag = CompositeDisposable

/**
 * Fluent syntax extension, more friendly than [addTo] or [CompositeDisposable.add]. Matches
 * iOS syntax.
 */
fun Disposable.disposedBy(disposeBag: DisposeBag) = addTo(disposeBag)
