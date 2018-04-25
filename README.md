# RX Helpers

This is a a kotlin library that helps bridge the gap between iOS RXSwift and RXKotlin/RXJava. There are two useful artifacts that are built together: `retrofit-rx` and `rx-helpers`.

## Including in your project

```groovy
allProjects {
  repositories {
    // required to find the project's artifacts
    maven { url "https://www.jitpack.io" }
  }
}

```

In your project-level build.gradle:

```groovy

def latest = // 10 digit hash of commit you wish, or version in the "releases" tab.

dependencies {

  // rx helpers
  implementation "com.github.fuzz-productions:rx-helpers:${latest}"

  // retrofit helpers
  implementation "com.github.fuzz-productions:retrofit-rx:${latest}"
}


```



## Extension helpers

1. `T.asObservable()` => converts an object of type `T` into `Observable<T>.just`

2. `T.asMaybe()` => converts an object of type `T` into `Maybe<T>.just`

3. `T.asFlowable()` => converts an object of type `T` into `Flowable<T>.just`

4. `T.asSingle()` => converts an object of type `T` into `Single<T>.just`

5. `P.asFlowable()` => converts a `Publisher<T>` into a `Flowable<T>`

6. `P.asObservable()` => converts a `Publisher<T>` into a `Observable<T>`

## Concepts

On top of just pure extension helpers, this library adds a few concepts to RXKotlin:

1. `Bind`: analogous to `subscribeBy()` with all methods mapped.

2. `DisposeBag`: `CompositeDisposable`

3. `Driver`: RXSwift [Driver](https://github.com/ReactiveX/RxSwift/blob/master/Documentation/Traits.md#driver).

4. `Optional<T>`: Own optional `data class` with corresponding helper extension operators.

### Bind

RXJava has concept of `subscribe()` and `subscribeBy()` (RXKotlin). While these are great,
 there is no syntactic equivalent.

 This library includes the `bind()` operator to map the subscription of an object to the corresponding interfaces:

 1. `Observer`

 2. `Consumer`

 3. `Emitter`

 We support `Maybe`, `Single`, `Observable`, and `Flowable`. These are duplicative in some places because RXJava does not consolidate the objects with similar hierachy trees, thus we cannot simplify implementation.

 Use:

 ```kotlin

val subject: Subject<User> = PublishSubject.create()

user.asObservable()
  .bindTo(subject)
  .disposedBy(disposeBag);

 ```

Bind on error in debug builds throw an `OnErrorNotImplementedException`, while on release builds
we log to `Timber`.

#### Bind to main

We have a convenience operator `bindToMain()` which binds various objects onto the main thread. analogous to `observeOn(AndroidSchedulers.mainThread()).bindTo()`

### DisposeBag

`typealias` to `CompositeDisposable`

Add a `Disposable.disposedBy(disposeBag)` chaining method. It is analogous to RXKotlin `addTo()` method.

### Driver

1. It never fails

2. It delivers events on the `AndroidSchedulers.mainThread()`

3. It replays the last event when subscribed to.

4. It can have multiple subscribers.

5. `drive()` must happen on the main thread, and will return results on the main thread.

Example:
```kotlin

val userName: Subject<String> = PublishSubject.create()

val userNameRelay: Relay<String> = PublishRelay.create()

userName.asDriver(onErrorJustReturn = "")
  .drive(userNameRelay)
  .disposedBy(disposeBag)

```

### Optional


Instead of using the Javas 8 `Optional`, we created our own `data class` for this purpose to support older android SDK versions.
Since RXJava disallows `null` completely, we needed a way to represent nothing without `onError()` terminations from null values.

```kotlin

val op = optionalOf(user)

op.asObservable()
  .filterValue() // only emit if value exists
  .bindTo(userSubject)


```


## RX + Retrofit

We have a few RX helpers on the Retrofit front.

1. `LoadStatus` => loading state
2. `retrofit.Result` extensions.


### LoadStatus

A `sealed class` object that represents loading states.

1. `None`: Initial state `object`. Has not loaded yet.

2. `Loading`: loading state `object`.

3. `Error`: contains an error `Throwable` which describes the network error or some other kind.

4. `Success`: represents successful load. This should contain a `<T>` with a value on completion.


Along these lines we have a few convenience operators to make code more expressive and readable:

```kotlin

val loadStatus: Subject<LoadStatus> = BehaviorSubject.create()

networkService.loadData()
  .startLoading(loadStatus) // trigger loading
  .bindToLoadStatus(loadStatus) { dataSubject.onNext(it) } // on complete, on error, and on success mapped. optional parameters for calllbacks.
  .disposedBy(disposeBag)

loadStatus
  .filterErrors() // only listen for errors. Return `Observable<LoadStatus.Error>`

loadStatus
  .filterSuccess() // listen for success. Return `Observable<Unit>`


loadStatus
  .mapToLoadingState() // convenience for map { it == LoadStatus.Loading }

```

### retrofit.Result helpers

By having a Retrofit Interface return `Single<Result<T>>`, you get status of request back in an rx-y way. We added a few operators to make handling them more expressive.

```kotlin

networkService.loadData() // Single<Result<T>>
  .filterSuccess() // Maybe<T>

networkService.loadData()
  .filterFailures() // Maybe<Throwable>

```

Also we support `Observable` methods for calls listed above.


```kotlin

networkService.loadData()
  .doOnSuccess { // do something here }
  .doOnFailure { // do something on error }

```


## Maintained By

[agrosner](https://github.com/agrosner) ([@agrosner](https://www.twitter.com/agrosner))
