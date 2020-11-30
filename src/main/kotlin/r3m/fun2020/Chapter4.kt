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


