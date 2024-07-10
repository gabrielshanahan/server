package io.github.gabrielshanahan.server

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class LinkedListTest :
    StringSpec({
        "constructor should initialize with size 0" {
            val list = LinkedList()
            list.size() shouldBe 0
        }

        "add should increase the size and add elements correctly" {
            val list = LinkedList()

            list.add("one")
            list.size() shouldBe 1
            list.get(0) shouldBe "one"

            list.add("two")
            list.size() shouldBe 2
            list.get(1) shouldBe "two"
        }

        "remove should decrease the size and remove elements correctly" {
            val list = LinkedList()

            list.add("one")
            list.add("two")
            list.remove("one") shouldBe true

            list.size() shouldBe 1
            list.get(0) shouldBe "two"

            list.remove("two") shouldBe true
            list.size() shouldBe 0
        }

        "remove missing element should not alter the list" {
            val list = LinkedList()

            list.add("one")
            list.add("two")
            list.remove("three") shouldBe false
            list.size() shouldBe 2
        }
    })
