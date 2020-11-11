package r3m.fun2020

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import r3m.fun2020.List.Cons
import r3m.fun2020.List.Nil

class Chapter3Test : FreeSpec({
    "3.1" - {
        "tails a Cons" {
            tail(Cons(1, Cons(2, Nil))) shouldBe Cons(2, Nil)
            tail(Cons(1, Nil)) shouldBe Nil
        }
        "tails a Nil" {
            tail(Nil) shouldBe Nil
        }
    }
    "3.2" - {
        "sets a list's head" {
            setHead(Nil, Nil) shouldBe Cons(Nil, Nil) // it's a List<List<?>> !?
            setHead(Nil, 1) shouldBe Cons(1, Nil)
        }
    }
    "3.3" - {
        "drops nothing from a Nil" {
            drop(Nil, 1) shouldBe Nil
            drop(Nil, 99) shouldBe Nil
        }
        "drops n items from the list starting from the head" {
            drop(Cons(1, Cons(2, Nil)), 1) shouldBe Cons(2, Nil)
            drop(Cons(1, Cons(2, Cons(3, Nil))), 1) shouldBe
                    Cons(2, Cons(3, Nil))
        }
        "drops to Nil if removing too many items" {
            drop(Cons(1, Cons(2, Nil)), 3) shouldBe Nil
            drop(Cons(1, Cons(2, Cons(3, Nil))), 3) shouldBe Nil
        }
    }
    "3.4" - {
        "drops nothing from a Nil" {
            dropWhile(Nil) { a: String -> a == "" } shouldBe Nil
        }
        "drops items if they match the predicate" {
            dropWhile(Cons(1, Cons(1, Cons(2, Nil)))) { it <= 1 } shouldBe
                    Cons(2, Nil)
        }
    }
    "3.5" - {
        "init Nil to Nil" {
            init(Nil) shouldBe Nil
        }
        "init Cons(_, Nil) to Nil" {
            init(Cons(1, Nil)) shouldBe Nil
        }
        "init takes all but the last element in the list" {
            init(Cons(1, Cons(2, Cons(3, Nil)))) shouldBe Cons(1, Cons(2, Nil))
        }
    }
    "3.8" - {
        "computes the length of a list" {
            length(Nil) shouldBe 0
            length(Cons(1, Nil)) shouldBe 1
            length(Cons(1, Cons(2, Nil))) shouldBe 2
        }
    }
    "3.9, 3.10" - {
        "sum works with foldLeft" {
            sum(List.of(1.0, 2.0)) shouldBe 3.0
        }
        "product works with foldLeft" {
            product(List.of(1.0, 2.0)) shouldBe 2.0
        }
        "length2 works with foldLeft" {
            length2(Nil) shouldBe 0
            length2(Cons(1, Nil)) shouldBe 1
            length2(Cons(1, Cons(2, Nil))) shouldBe 2
        }
    }
    "3.11" - {
        "reverses a list" {
            reverse(List.of(1, 2, 3)) shouldBe List.of(3, 2, 1)
        }
    }
    "3.12" - {
        "can foldRight in terms of foldLeft" {
            // 1 - foldRight([2,3], 0)
            // 1 - (2 - foldRight([3], 0))
            // 1 - (2 - (3 - foldRight([], 0)))
            // 1 - (2 - (3 - 0))
            foldRight(List.of(1, 2, 3), 0) { a, b -> a - b } shouldBe 2
            foldRight2(List.of(1, 2, 3), 0) { a, b -> a - b } shouldBe 2
            foldRight(List.of(1, 2, 3), "") { i: Int, s: String -> s + i.toString() } shouldBe "321"
        }
        "can foldLeft in terms of foldRight" {
            // foldLeft([2,3], 0 - 1)
            // foldLeft([3], (0 - 1) - 2)
            // foldLeft([], ((0 - 1) - 2) - 3)
            // ((0 - 1) - 2) - 3)
            foldLeft(List.of(1, 2, 3), 0) { a, b -> a - b } shouldBe -6
            foldLeft2(List.of(1, 2, 3), 0) { a, b -> a - b } shouldBe -6
        }
    }
    "3.13" - {
        "appends a list to another" {
            append(List.of(1, 2), List.of(3)) shouldBe List.of(1, 2, 3)
            append(List.of(1, 2), List.of(3, 4)) shouldBe List.of(1, 2, 3, 4)
        }
    }
    "3.14" - {
        "special-cases Nil" {
            concat(List.empty<List<Int>>()) shouldBe Nil
        }
        "concatenates lists" {
            concat(List.of(
                List.of(1, 2),
                List.of(3, 4),
                List.of(5, 6)
            )) shouldBe List.of(1, 2, 3, 4, 5, 6)
        }
        "degrades to unwrap a list if it's a single element" {
            concat(List.of(List.of(1, 2))) shouldBe List.of(1, 2)
        }
    }
    "3.15 - 3.17" - {
        "adds one to each element" {
            addOne(List.of(1.0, 2.0)) shouldBe List.of(2.0, 3.0)
        }
        "turns doubles into strings" {
            doublesToStrings(List.of(1.0, 2.0)) shouldBe List.of("1.0", "2.0")
        }
        "i've been using map this entire time" {
            map(List.of(1.0, 2.0)) { it > 1.0 } shouldBe List.of(false, true)
        }
    }
    "3.18, 3.20" - {
        "can filter out elements" {
            filter(List.of(1, 2, 3)) { it % 2 == 0 } shouldBe List.of(2)
            filter2(List.of(1, 2, 3)) { it % 2 == 0 } shouldBe List.of(2)
        }
    }
    "3.19" - {
        "can flatMap" {
            flatMap(List.of(1, 2, 3)) { List.of(it, it) } shouldBe
                    List.of(1, 1, 2, 2, 3, 3)
        }
    }
    "3.21" - {
        "can add each element of two lists together" {
            addEach(List.of(1, 2, 3), List.of(4, 5, 6)) shouldBe
                    List.of(5, 7, 9)
        }
    }
    "3.22" - {
        "can zipWith to do addEach" {
            zipWith(List.of(1, 2, 3), List.of(4, 5, 6)) { a, b -> a + b } shouldBe
                    List.of(5, 7, 9)
        }
    }
    "3.23" - {
        "checks if a list contains a subsequence" {
            hasSubsequence(List.of(1, 2, 3), List.of(1, 2)) shouldBe true
            hasSubsequence(List.of(1, 2, 3), List.of(2, 3)) shouldBe true
            hasSubsequence(List.of(1, 2, 3), List.of(1)) shouldBe true
            hasSubsequence(List.of(1, 2, 3), List.of(4)) shouldBe false
            hasSubsequence(List.of(1, 2, 3), List.of(2,  4)) shouldBe false
        }
        "the entire list is a subsequence of a list" {
            hasSubsequence(List.of(1, 2, 3), List.of(1, 2, 3)) shouldBe true
        }
        "Nil is a subsequence of any list" {
            hasSubsequence(List.of(1, 2, 3), Nil) shouldBe true
            hasSubsequence(Nil, Nil) shouldBe true
        }
    }
    "3.24" - {
        "tree has size 1 if it's a leaf" {
            size(Leaf("")) shouldBe 1
        }
        "tree has size based on all its nodes" {
            size(Branch(
                Leaf(""),
                Leaf("")
            )) shouldBe 3
            size(Branch(
                Branch(
                    Leaf(""),
                    Leaf("")
                ),
                Leaf("")
            )) shouldBe 5
        }
    }
    "3.25" - {
        "can compute maximum element in tree" {
            fun max (t: Tree<Int>) = maximum(t) { a, b -> a > b }

            max(Leaf(1)) shouldBe 1
            max(Branch(
                Leaf(1),
                Leaf(2)
            )) shouldBe 2

            max(Branch(
                Branch(
                    Leaf(3),
                    Leaf(1)
                ),
                Leaf(2)
            )) shouldBe 3
        }
    }
    "3.26" - {
        "tree has depth 1 if it's a leaf" {
            depth(Leaf("")) shouldBe 1
        }
        "tree has depth based on the maximum path to the node" {
            depth(Branch(
                Leaf(""),
                Leaf("")
            )) shouldBe 2
            depth(Branch(
                Branch(
                    Leaf(""),
                    Leaf("")
                ),
                Leaf("")
            )) shouldBe 3
        }
    }
})
