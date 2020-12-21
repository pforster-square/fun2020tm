@file:Suppress("UnusedEquals", "UNREACHABLE_CODE", "UNUSED_ANONYMOUS_PARAMETER", "RemoveExplicitTypeArguments")

package r3m.fun2020

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

class Chapter4Test : FreeSpec({
    "4.1" - {
        "map" {
            Some(1).map { it + 1 } shouldBe Some(2)
            None.map { "string: $it" } shouldBe None
        }
        "flatMap" {
            Some(1).flatMap { Some(it + 1) } shouldBe Some(2)
            Some(1).flatMap { None } shouldBe None
            None.flatMap { Some("") } shouldBe None
        }
        "getOrElse" {
            Some(1).getOrElse { 2 } shouldBe 1
            None.getOrElse { 1 } shouldBe 1
        }
        "orElse" {
            Some(1).orElse { Some(2) } shouldBe Some(1)
            None.orElse { Some(1) } shouldBe Some(1)
        }
        "filter" {
            Some(1).filter { it == 1 } shouldBe Some(1)
            Some(1).filter { it > 1 } shouldBe None
            None.filter { it == 1 } shouldBe None
        }
    }
    "4.2" - {
        "variance of nothing is none" {
            variance(emptyList<Double>()) shouldBe None
        }
        "variance of a list is a number" {
            variance(listOf(1.0, 2.0)) shouldBe Some(0.25)
        }
    }
    "4.3" - {
        "map2 is None if either input is None" {
            map2(None, Some(1)) { a, b -> "$a$b" } shouldBe None
            map2(Some(1), None) { a, b -> "$a$b" } shouldBe None
            map2(None, None) { a, b -> "$a$b" } shouldBe None
        }
        "map2 applies f if both inputs are Some" {
            map2(Some(1), Some(2)) { a, b -> a + b } shouldBe Some(3)
        }
    }
    "4.4" - {
        "sequence returns None if any items in the list are None" {
            sequence(listOf(None)) shouldBe None
            sequence(listOf(Some(1), Some(2), None)) shouldBe None
            sequence(listOf(Some(1), None, None, Some(4))) shouldBe None
        }
        "sequence returns an option of a list if all items in the list are Some" {
            sequence(listOf(Some(1))) shouldBe Some(listOf(1))
            sequence(listOf(Some(1), Some(2), Some(3))) shouldBe Some(listOf(1, 2, 3))
        }
        "sequence returns Some(empty) if the list is empty" {
            sequence(emptyList<Option<Double>>()) shouldBe Some(emptyList<Double>())
        }
    }
    "4.5" - {
        "traverse returns an option of a transformed list if the operation always returns Some" {
            traverse(listOf(1)) { Some(it + 1) } shouldBe Some(listOf(2))
            traverse(listOf(1, 2, 3)) { Some(it - 1) } shouldBe Some(listOf(0, 1, 2))
        }
        "traverse returns None if the operation returns None on any of the elements" {
            traverse(listOf(1, 2, 3)) { if (it < 2) Some(it) else None } shouldBe None
        }
        "traverse returns Some(empty) if the list is empty" {
            traverse(emptyList<Int>()) { Some(it) } shouldBe Some(emptyList())
        }
    }
    "4.6" - {
        "map" {
            Left("error").map { "oh $it" } shouldBe Left("error")
            Right("pass").map { "oh $it" } shouldBe Right("oh pass")
        }
        "flatMap" {
            Left("error").flatMap { Left("oh $it") } shouldBe Left("error")
            Left("error").flatMap { Right("oh $it") } shouldBe Left("error")
            Right("pass").flatMap { Left("error") } shouldBe Left("error")
            Right("pass").flatMap { Right("mountain $it") } shouldBe Right("mountain pass")
        }
        "orElse" {
            Left("error").orElse { Left("more errors") } shouldBe Left("more errors")
            Left("error").orElse { Right("backup") } shouldBe Right("backup")
            Right("pass").orElse { Right("backup") } shouldBe Right("pass")
            Right("pass").orElse { Left("error") } shouldBe Right("pass")
        }
        "map2 applies f if both inputs are Right" {
            map2(Right("a"), Right("b")) { a, b -> "$a$b" } shouldBe Right("ab")
        }
        "map2 returns a Left if any of the inputs are Left" {
            // Well Actually, map2 could do anything it wants, as long as it's a Left.
            map2(Left("a"), Right("b")) { a, b -> "$a$b" } shouldBe Left("a")
            map2(Right("a"), Left("b")) { a, b -> "$a$b" } shouldBe Left("b")
            map2(Left("a"), Left("b")) { a, b -> "$a$b" } shouldBe Left("a")
        }
    }
    "4.7" - {
        "sequence returns Left if any of the items in the list are left" {
            sequence(listOf(Left("err"))) shouldBe Left("err")
            sequence(listOf(
                Right(1), Right(2), Left("err")
            )) shouldBe Left("err")
            sequence(listOf(
                Right(1), Left("err"), Left("err2"), Right(4)
            )) shouldBe Left("err")
        }
        "sequence returns Right<List> if all of the items in the list are right" {
            sequence(listOf(Right(1))) shouldBe Right(listOf(1))
            sequence(listOf(Right(1), Right(2), Right(3))) shouldBe Right(listOf(1, 2, 3))
        }
        "sequence returns Right(empty) if the list is empty" {
            sequence(emptyList<Either<String, Double>>()) shouldBe Right(emptyList<Double>())
        }
        // kotlin is unhappy about the two traverses, so i'm calling this one traverse2
        "traverse returns an Right of a transformed list if the operation always returns Right" {
            traverse2(listOf(1)) { Right(it + 1) } shouldBe Right(listOf(2))
            traverse2(listOf(1, 2, 3)) { Right(it - 1) } shouldBe Right(listOf(0, 1, 2))
        }
        "traverse returns Left if the operation returns Left on any of the elements" {
            traverse2(listOf(1, 2, 3)) { if (it < 2) Right(it) else Left("err") } shouldBe Left("err")
        }
        "traverse returns Right(empty) if the list is empty" {
            traverse2(emptyList<Int>()) { Right(it) } shouldBe Right(emptyList())
        }
    }
})