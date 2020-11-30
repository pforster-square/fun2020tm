@file:Suppress("UnusedEquals", "UNREACHABLE_CODE")

package r3m.fun2020

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe

class Chapter4Test: FreeSpec({
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
      traverse(listOf(1, 2, 3)) { Some(it - 1) }shouldBe Some(listOf(0, 1, 2))
    }
    "traverse returns None if the operation returns None on any of the elements" {
      traverse(listOf(1, 2, 3)) { if (it < 2) Some(it) else None } shouldBe None
    }
    "traverse returns Some(empty) if the list is empty" {
      traverse(emptyList<Int>()) { Some(it) } shouldBe Some(emptyList())
    }
  }
})