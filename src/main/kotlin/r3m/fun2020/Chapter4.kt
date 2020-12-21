package r3m.fun2020

import kotlin.collections.List
import kotlin.math.pow

sealed class Option<out A>

data class Some<out A>(val get: A) : Option<A>()
object None : Option<Nothing>()

// 4.1
// Key assumption in map(f): f always returns something of type B
fun <A, B> Option<A>.map(f: (A) -> B): Option<B> =
    when (this) {
        is Some -> Some(f(this.get))
        is None -> None
    }

fun <A, B> Option<A>.flatMap(f: (A) -> Option<B>): Option<B> =
    when (this) {
        is Some -> f(this.get)
        is None -> None
    }

fun <A> Option<A>.getOrElse(default: () -> A): A =
    when (this) {
        is Some -> this.get
        is None -> default()
    }

fun <A> Option<A>.orElse(ob: () -> Option<A>): Option<A> =
    when (this) {
        is Some -> this
        is None -> ob()
    }

fun <A> Option<A>.filter(f: (A) -> Boolean): Option<A> =
    when {
        this is Some && f(this.get) -> this
        else -> None
    }

// 4.2
fun mean(xs: List<Double>): Option<Double> =
    if (xs.isEmpty()) None else Some(xs.sum() / xs.size)

fun variance(xs: List<Double>): Option<Double> =
    // If I have a mean, I have a list.
    mean(xs)
        // Get a list of transformed values.
        .flatMap { mean -> Some(xs.map { (it - mean).pow(2) }) }
        // Get the mean over the list of transformed values.
        .flatMap(::mean)

// 4.3
fun <A, B, C> map2(a: Option<A>, b: Option<B>, f: (A, B) -> C): Option<C> =
    a.flatMap { ax -> b.flatMap { bx -> Some(f(ax, bx)) } }

// 4.4 - This looks like a great candidate to use the List from Chapter 3.
fun <A> sequence(xs: List<Option<A>>): Option<List<A>> =
    xs.fold<Option<A>, Option<List<A>>>(Some(emptyList<A>())) { acc, option ->
        acc.flatMap { list -> option.flatMap { a -> Some(list + a) } }
    }

// 4.5
fun <A, B> traverse(
    xa: List<A>,
    f: (A) -> Option<B>
): Option<List<B>> = xa.fold<A, Option<List<B>>>(Some(emptyList())) { acc, a ->
    acc.flatMap { list -> f(a).flatMap { b -> Some(list + b) } }
}

sealed class Either<out E, out A>
data class Left<out E>(val value: E) : Either<E, Nothing>()
data class Right<out A>(val value: A) : Either<Nothing, A>()

fun <A> catches(a: () -> A): Either<Exception, A> =
    try {
        Right(a())
    } catch (e: Exception) {
        Left(e)
    }

// 4.6
fun <E, A, B> Either<E, A>.map(f: (A) -> B): Either<E, B> =
    when (this) {
        is Left -> this
        is Right -> Right(f(this.value))
    }

fun <E, A, B> Either<E, A>.flatMap(f: (A) -> Either<E, B>): Either<E, B> =
    when (this) {
        is Left -> this
        is Right -> f(this.value)
    }

fun <E, A> Either<E, A>.orElse(
    f: () -> Either<E, A>
): Either<E, A> = when (this) {
    is Left -> f()
    is Right -> this
}

fun <E, A, B, C> map2(
    ae: Either<E, A>,
    be: Either<E, B>,
    f: (A, B) -> C
): Either<E, C> = when {
    ae is Right && be is Right -> Right(f(ae.value, be.value))
    ae is Left -> ae
    be is Left -> be
    else -> throw IllegalStateException("This shouldn't happen")
}

// 4.7
// these are close implementation-wise to 4.4 and 4.5
fun <E, A> sequence(xs: List<Either<E, A>>): Either<E, List<A>> =
    xs.fold(Right(emptyList<A>()) as Either<E, List<A>>)
    { acc, either -> acc.flatMap { list -> either.flatMap { a -> Right(list + a) } } }

fun <E, A, B> traverse2(
    xa: List<A>,
    f: (A) -> Either<E, B>
): Either<E, List<B>> = xa.fold(Right(emptyList<B>()) as Either<E, List<B>>)
{ acc, a -> acc.flatMap { list -> f(a).flatMap { b -> Right(list + b) } } }

// 4.8
fun <E, A, B, C> map2x(
    ae: Either<E, A>,
    be: Either<E, B>,
    f: (A, B) -> C
): Either<List<E>, C> = when {
    ae is Right && be is Right -> Right(f(ae.value, be.value))
    ae is Left && be is Left -> Left(listOf(ae.value, be.value))
    ae is Left -> Left(listOf(ae.value))
    be is Left -> Left(listOf(be.value))
    else -> throw IllegalStateException("This shouldn't happen")
}

