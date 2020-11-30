package r3m.fun2020

import r3m.fun2020.List.Cons
import r3m.fun2020.List.Nil

sealed class List<out A> {
    object Nil : List<Nothing>()

    data class Cons<out A>(
        val head: A,
        val tail: List<A>
    ) : List<A>()

    companion object {
        fun <A> of(vararg aa: A): List<A> {
            val tail = aa.sliceArray(1 until aa.size)
            return if (aa.isEmpty()) Nil else Cons(aa[0], of(*tail))
        }

        fun <A> empty(): List<A> = Nil
    }
}

// 3.1
fun <A> tail(xs: List<A>): List<A> = when (xs) {
    is Cons -> xs.tail
    is Nil -> Nil // either itself, or Nil?
}

// 3.2
fun <A> setHead(xs: List<A>, x: A): List<A> = when (xs) {
    is Cons -> Cons(x, xs.tail)
    is Nil -> Nil // should throw
}

// 3.3
tailrec fun <A> drop(l: List<A>, n: Int): List<A> = when {
    n == 0 -> l
    l is Cons -> drop(l.tail, n - 1)
    else -> l
}

fun <A> tail2(xs: List<A>): List<A> = drop(xs, 1)

// 3.4
fun <A> dropWhile(l: List<A>, f: (A) -> Boolean): List<A> = when (l) {
    // being too fancy by trying to filter here, rather than stopping the drop
    is Cons -> if (f(l.head)) dropWhile(l.tail, f) else Cons(l.head, dropWhile(l.tail, f))
    is Nil -> l
}

// 3.5
// init is all but the last element (but whyyy?)
fun <A> init(l: List<A>): List<A> = when (l) {
    is Cons -> when {
        // Special-case one-length lists
        l.tail is Nil -> Nil
        // Exit condition: Cons(Cons(_, Nil))
//        l.tail is Cons && l.tail.tail is Nil -> Cons(l.head, Nil)
        else -> Cons(l.head, init(l.tail))
    }
    // Special-case zero-length lists
    is Nil -> l
}

// 3.6
fun <A, B> foldRight(xs: List<A>, z: B, f: (A, B) -> B): B = when (xs) {
    is Nil -> z
    is Cons -> f(xs.head, foldRight(xs.tail, z, f))
}

/**
 * foldRight([1,2,3], z, f)
 * = f(1, foldRight([2,3], z, f))
 * = f(1, f(2, foldRight([3], z, f)))
 * = f(1, f(2, f(3, foldRight([], z, f))))
 * = f(1, f(2, f(3, z) ) )
 */

val x = foldRight(
    List.of(1, 2, 3),
    List.empty<Int>(),
    { x: Int, y: List<Int> -> Cons(x, y) }
)

/*
    Can this below method short-circuit if the product is zero?
    I don't think it can in its current form, but we can introduce a predicate to
    short-circuit and stop performing folds.
    It's also possible to wrap this in a type that has something to indicate that
    we should keep folding.

    fun product2(dbs: List<Double>): Double = foldRight(dbs, 1.0, { a, b -> a * b })
*/

// 3.8
fun <A> length(xs: List<A>): Int =
    foldRight(xs, 0) { _, i -> i + 1 }

// 3.9
tailrec fun <A, B> foldLeft(xs: List<A>, z: B, f: (B, A) -> B): B = when (xs) {
    is Nil -> z
    is Cons -> foldLeft(xs.tail, f(z, xs.head), f)
}

/**
 * foldLeft([1,2,3], z,f)
 * = foldLeft([2,3], aa=f(z, 1), f)
 * = foldLeft([3], f(aa, 2), f)
 */

// 3.10
fun sum(dbs: List<Double>): Double =
    foldLeft(dbs, 0.0) { b, a -> b + a }

fun product(dbs: List<Double>): Double =
    foldLeft(dbs, 1.0) { b, a -> b * a }

fun <A> length2(xs: List<A>): Int =
    foldLeft(xs, 0) { b, _ -> b + 1 }

// 3.11
fun <A> reverse(xs: List<A>): List<A> =
    foldLeft(xs, List.empty()) { list, a -> Cons(a, list) }

// 3.12
fun <A, B> foldRight2(xs: List<A>, z: B, f: (A, B) -> B): B =
    foldLeft(reverse(xs), z) { b, a -> f(a, b) }

fun <A, B> foldLeft2(xs: List<A>, z: B, f: (B, A) -> B): B =
    foldRight(reverse(xs), z) { a, b -> f(b, a) }

// 3.13
fun <A> append(a1: List<A>, a2: List<A>): List<A> =
    foldRight2(a1, a2) { a, list -> Cons(a, list) }


/**
 * a1 [1,2,3]
 * a2 [4,5,6]
 *
 * Cons(3, [4,5,6])
 *
 *
 */













// 3.14
fun <A> concat(ax: List<List<A>>): List<A> = when (ax) {
    is Nil -> Nil
    is Cons -> foldLeft(ax.tail, ax.head) { a1, a2 -> append(a1, a2) }
}

// 3.15 - 3.17
fun <A, B> map(xs: List<A>, f: (A) -> B): List<B> = when (xs) {
    is Nil -> Nil
    is Cons -> Cons(f(xs.head), map(xs.tail, f))
}

fun addOne(dbs: List<Double>): List<Double> =
    map(dbs) { db -> db + 1 }

fun doublesToStrings(dbs: List<Double>): List<String> =
    map(dbs) { db -> db.toString() }

// 3.18
fun <A> filter(xs: List<A>, f: (A) -> Boolean): List<A> = when (xs) {
    is Nil -> Nil
    is Cons -> if (f(xs.head)) Cons(xs.head, filter(xs.tail, f)) else filter(xs.tail, f)
}

// 3.19
fun <A, B> flatMap(xa: List<A>, f: (A) -> List<B>): List<B> = when (xa) {
    is Nil -> Nil
    is Cons -> foldLeft(xa, List.empty()) { list, a -> append(list, f(a)) }
}

// 3.20
fun <A> filter2(xs: List<A>, f: (A) -> Boolean): List<A> =
    flatMap(xs) { if (f(it)) Cons(it, Nil) else Nil }

// 3.21
fun addEach(a: List<Int>, b: List<Int>): List<Int> = when {
    a is Cons && b is Cons -> Cons(a.head + b.head, addEach(a.tail, b.tail))
    // Weird edge case alert: if one of them isn't Cons (i.e. different-length lists),
    // throw all of them away.
    else -> Nil
}

// 3.22
fun <A> zipWith(a1: List<A>, a2: List<A>, f: (A, A) -> A): List<A> = when {
    a1 is Cons && a2 is Cons -> Cons(f(a1.head, a2.head), zipWith(a1.tail, a2.tail, f))
    else -> Nil
}

// 3.23
tailrec fun <A> hasSubsequence(xs: List<A>, sub: List<A>): Boolean = when {
    xs == sub -> true
    sub is Nil -> true
    xs is Cons && sub is Cons -> when {
        xs.head == sub.head -> hasSubsequence(xs.tail, sub.tail)
        else -> hasSubsequence(xs.tail, sub)
    }
    else -> false
}

// Trees
sealed class Tree<out A>

data class Leaf<A>(val value: A) : Tree<A>()

data class Branch<A>(
    val left: Tree<A>,
    val right: Tree<A>
) : Tree<A>()

// 3.24
fun size(t: Tree<*>): Int = when (t) {
    is Leaf -> 1
    is Branch -> size(t.left) + size(t.right) + 1
}

// 3.25
fun <A> maximum(t: Tree<A>, gt: (A, A) -> Boolean): A = when (t) {
    is Leaf -> t.value
    is Branch -> {
        val left = maximum(t.left, gt)
        val right = maximum(t.right, gt)
        if (gt(left, right)) left else right
    }
}

// 3.26
fun depth(t: Tree<*>): Int = when(t) {
    is Leaf -> 1
    is Branch -> maxOf(depth(t.left) + 1, depth(t.right) + 1)
}

// 3.27
fun <A, B> map(t: Tree<A>, f: (A) -> B): Tree<B> = when (t) {
    is Leaf -> Leaf(f(t.value))
    is Branch -> Branch(map(t.left, f), map(t.right, f))
}

// 3.28
fun <A, B> fold(ta: Tree<A>, l: (A) -> B, b: (B, B) -> B): B = when (ta) {
    is Leaf -> l(ta.value)
    is Branch -> b(fold(ta.left, l, b), fold(ta.right, l, b))
}



fun sizeA(t: Tree<*>): Int = when (t) {
    is Leaf -> 1
    is Branch -> sizeA(t.left) + sizeA(t.right) + 1
}

//fun sizeF1(t: Tree<*>): Int = when (t) {
//    is Leaf -> 1
//    is Branch -> { b1, b2 -> b1 + b2 + 1 } (sizeF1(t.left), sizeF1(t.right))
//}

/**
 *
 * a
 * - a.left: b
 * - a.right: c { 1, 1 -> 1 + 1 + 1 }
 *   - 1
 *   - 1
 *
 */


// I think it makes more sense to keep the lambdas here in the parens.
fun <A> sizeF(ta: Tree<A>): Int = fold(ta, { 1 }, { b1, b2 -> b1 + b2 + 1 })

fun maximumF(ta: Tree<Int>): Int = fold(ta, { it }, { b1, b2 -> maxOf(b1, b2) })

fun <A> depthF(ta: Tree<A>): Int = fold(ta, { 1 }, { b1, b2 -> maxOf(b1, b2) })

fun <A, B> mapF(ta: Tree<A>, f: (A) -> B): Tree<B>
    = fold<A, Tree<B>>(ta, { Leaf(f(it)) }, { b1, b2 -> Branch(b1, b2) })


