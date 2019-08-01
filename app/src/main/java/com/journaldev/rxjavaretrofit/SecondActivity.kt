package com.journaldev.rxjavaretrofit

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

import io.reactivex.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.annotations.SchedulerSupport.IO
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.rxkotlin.toObservable

import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.second_main.*

import java.util.Locale.filter
import java.util.concurrent.TimeUnit







class SecondActivity : AppCompatActivity() {
    //Observer — Any object that wants to be notified when another object changes.
    //Observable — Any object whose state may be of interest, and in whom another object may register an interest.


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.second_main)

//Start the stream when the button is clicked//

        button.setOnClickListener {
            startRStream() }

    }

    @SuppressLint("CheckResult")
    private fun startRStream() {

        val list = listOf("1", "2", "3", "4", "5")

//Apply the toObservable() extension function//

        list.toObservable()

//Construct your Observer using the subscribeBy() extension function//

                .subscribeBy(

                        onNext = { println(it) },
                        onError = { it.printStackTrace() },
                        onComplete = { println("onComplete!") }


                )
        /* just
         The just operator converts an Item into an Observable and emits it.*/
        Observable.just("Apple", "Orange", "Banana")
                .subscribe(
                        { value -> println("Received: $value") }, // onNext
                        { error -> println("Error: $error") },    // onError
                        { println("Completed!") }                 // onComplete

                )

        /* from*
                 There are a few ways you can use from, and some of them are listed below:*/
        Observable.fromArray("Apple", "Orange", "Banana")
                .subscribe(
                        { value -> println("Received: $value") }, // onNext
                        { error -> println("Error: $error") },    // onError
                        { println("Completed!") }                 // onComplete

                )


        /* just
       The just operator converts an Item into an Observable and emits it.*/
        Observable.just("Hello Reactive World")
                .subscribe(  { value -> println(value) },
                        { error -> println("Error: $error")} ,
                        { println("Completed!") }
                )

        //fromIterable
        Observable.fromIterable(listOf("Apple", "Orange", "Banana"))
                .subscribe(
                        { value -> println("Received: $value") },      // onNext
                        { error -> println("Error: $error") },         // onError
                        { println("Completed") }                       // onComplete
                )

        /*create
        This way you can create an Observable from the ground up. Let’s see an example.*/
        fun getObservableFromList(myList: List<String>) =
                Observable.create<String> { emitter ->
                    myList.forEach { kind ->
                        if (kind == "") {
                            emitter.onError(Exception("There's no value to show"))
                        }
                        emitter.onNext(kind)
                    }
                    emitter.onComplete()
                }

        //interval
        //This function will create an infinite sequence of ticks, separated by the specified duration.
        Observable.intervalRange(
                10L,     // Start
                5L,      // Count
                0L,      // Initial Delay
                1L,      // Period
                TimeUnit.SECONDS
        ).subscribe { println("Result we just received: $it") }


        //Flowable
        //It works exactly like an Observable but it supports Backpressure.
        val observable = PublishSubject.create<Int>()
        observable
                .toFlowable(BackpressureStrategy.DROP)
                .observeOn(Schedulers.computation())
                .subscribe (


                        {
                            println("The Number Is: $it")
                        },
                        {t->
                            print(t.message)
                        }

                )
        for (i in 0..10){
            observable.onNext(i)
        }

        // Emitter types/////////////////////////////////////
        //Flowable
        //It works exactly like an Observable but it supports Backpressure.
        Flowable.just("This is a Flowable")
                .subscribe(

                        { value -> println("Received: $value") },
                        { error -> println("Error: $error") },
                        { println("Completed") }
                )

        /* Maybe
         This class is used when you’d like to return a single optional value.
         The methods are mutually exclusive,
         in other words, only one of them is called.
         If there is an emitted value, it calls onSuccess ,
         if there’s no value, it calls onComplete or if there’s an error, it calls onError .*/

        Maybe.just("This is a Maybe")
                .subscribe(
                        { value -> println("Received: $value") },
                        { error -> println("Error: $error") },
                        { println("Completed") }
                )


        /* Single
         It’s used when there’s a single value to be returned.
         If we use this class and there is a value emitted,
         onSuccess will be called. If there’s no value, onError will be called.*/


        Single.just("This is a Single")
                .subscribe(
                        { v -> println("Value is: $v") },
                        { e -> println("Error: $e")}
                )

        /* Completable
         A completable won’t emit any data, what it does is let you know whether the operation was successfully completed.
         If it was, it calls onComplete and if it wasn’t it calls onError . A common use case of completable is for REST APIs,
         where successful access will return HTTP 204 ,
         and errors can ranger from HTTP 301 , HTTP 404 , HTTP 500 , etc. We might do something with the information.*/

        Completable.create { emitter ->
            emitter.onComplete()
            emitter.onError(Exception())
        }
        /*  You can also manually call the methods doOnSubscribe, doOnNext, doOnError, doOnComplete.*/

        Observable.just("Hello")
                .doOnSubscribe { println("Subscribed") }
                .doOnNext { s -> println("Received: $s") }
                .doAfterNext { println("After Receiving") }
                .doOnError { e -> println("Error: $e") }
                .doOnComplete { println("Complete") }
                .doFinally { println("Do Finally!") }
                .doOnDispose { println("Do on Dispose!") }
                .subscribe { println("Subscribe") }


        // Emitter types End/////////////////////////////////////


        //Schedulers////////////////////////////////////////////

        /*subscribeOn
        With subscribeOn you get to decide which thread your Emitter (such as Observable , Flowable , Single , etc) is executed.
        The subscribeOn (as well as the observeOn ) needs the Scheduler param to know which thread to run on.
        Let’s talk about the difference between the threads.
        Scheduler.io() This is the most common types of Scheduler that are used. They’re generally used for IO related stuff,
        such as network requests, file system operations, and it’s backed by a thread pool.
        A Java Thread Pool represents a group of worker threads that are waiting for the job and reuse many times.*/


        Observable.just("Apple", "Orange", "Banana")
                .subscribeOn(Schedulers.io())
                .subscribe{ v -> println("Received: $v") }


        //////Schedulers////////////////////////////////////////////
        //  There are many operators that you can add on the Observable chain, but let’s talk about the most common ones.



        //map
        // Transforms values emitted by an Observable stream into a single value. Let’s take a look at a simple example below:

        Observable.just("Water", "Fire", "Wood")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map { m -> m + " 2" }
                .subscribe { v -> println("Received: $v") }


        /* flatMap()
         Unlike the map() operator, the flatMap() will transform each value in an Observable stream into another Observable ,
         which are then merged into the output Observable after processing.
         Let’s do a visual representation of the difference between those:*/


        Observable.just("Water", "Fire", "Wood")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap { m ->
                    Observable.just(m + " 2")
                            .subscribeOn(Schedulers.io())
                }
                .subscribe { v -> println("Received: $v") }


        /* zip()
         The zip() operator will combine the values of multiple Observable together through a specific function.*/


        Observable.zip(
                Observable.just(
                        "Roses", "Sunflowers", "Leaves", "Clouds", "Violets", "Plastics"),
                Observable.just(
                        "Red", "Yellow", "Green", "White or Grey", "Purple"),
                BiFunction<String, String, String> { type, color ->
                    "$type are $color"
                }
        )
                .subscribe { v -> println("Received: $v") }


        /* concat()
         As the name suggests, concat() will concatenate (join together) two or more Observable .*/

        val test1 = Observable.just("Apple", "Orange", "Banana")
        val test2 = Observable.just("Microsoft", "Google")
        val test3 = Observable.just("Grass", "Tree", "Flower", "Sunflower")

        Observable.concat(test1, test2, test3).subscribe{ x -> println("Received: " + x) }


        /*  merge()
          merge() works similarly to concat() , except merge will intercalate the emissions from both Observable ,
          whereas concat() will wait for one to finish to show another.*/

        Observable.merge(
                Observable.interval(250, TimeUnit.MILLISECONDS).map { i -> "Apple" },
                Observable.interval(150, TimeUnit.MILLISECONDS).map { i -> "Orange" }
        )
                .take(10)
                .subscribe{ v -> println("Received: $v") }



        /*As you can see, the results intercalate between each other. What would happen if instead of themerge()
   operator we used concat() in this situation?*/

        Observable.concat(
                Observable.interval(250, TimeUnit.MILLISECONDS).map { i -> "Apple" },
                Observable.interval(150, TimeUnit.MILLISECONDS).map { i -> "Orange" })
                .take(10)
                .subscribe{ v -> println("Received: $v") }


        /* filter()
         Filter the values according to a set condition.*/
        Observable.just(2, 30, 22, 5, 60, 1)
                .filter{ x -> x < 10 }
                .subscribe{ x -> println("Received: " + x) }


        /*  repeat()
          This operator will repeat the emission of the values however many times we may need.*/

        Observable.just("Apple", "Orange", "Banana")
                .repeat(2)
                .subscribe { v -> println("Repeat: $v") }

        /* take()
         The take() operator is meant to grab however many emissions you’d like. A very simple example would be:*/
        Observable.just("Apple", "Orange", "Banana")
                .take(2)
                .subscribe { v -> println("Received: $v") }


        /*Disposable
        Now that we’ve moved on from basic operators, let’s start talking about Disposable (using Disaposable will remove the dark yellow highlight from Android Studio IDE).
        A Disposable will release memory, resources, and threads used by an Observable .
        So, the main purpose of disposable is to free up system resources and make your app more stable.*/
        Observable.just("Apple", "Orange", "Banana")
                .subscribe(
                        { v -> println("Received: $v") }
                ).dispose()

    }


}