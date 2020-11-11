package r3m.fun2020

sealed class Option<out A>

data class Some<out A>(val get: A) : Option<A>()
object None : Option<Nothing>()

// 4.1
// Key assumption in map(f): f always returns something of type B
fun <A, B> Option<A>.map(f: (A) -> B): Option<B> = if (this is Some) Some(f(this.get)) else None

fun <A, B> Option<A>.flatMap(f: (A) -> Option<B>): Option<B> = if (this is Some) f(this.get) else None

fun <A> Option<A>.getOrElse(default: () -> A): A = if (this is Some) this.get else default()

fun <A> Option<A>.orElse(ob: () -> Option<A>): Option<A> = if (this is Some) this else ob()

fun <A> Option<A>.filter(f: (A) -> Boolean): Option<A> = if (this is Some && f(this.get)) this else None
