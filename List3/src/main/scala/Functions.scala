import scala.annotation.tailrec
import Auxiliary._

import scala.collection.mutable.HashMap

object Functions {
  // Task 1
  val split: List[Int] => (List[Int], List[Int]) = xs => {
    val (left, right) = fold_left(xs, (List[Int](), List[Int]()))((x, accu) => {
      if (x < 0) {
        if (isOdd(x)) (x :: accu._1, x :: accu._2)
        else (x :: accu._1, accu._2)
      } else accu
    })
    (reverse(left), reverse(right))
  }

  // Task 2
  def length[A](xs: List[A]): Int = {
    fold_left(xs, 0)((_, sum) => sum + 1)
  }

  // Task 3
  def interlace[A](xs: List[A], ys: List[A]): List[A] = {
    (xs, ys) match {
      case (hx :: tx, hy :: ty) => hx :: hy :: interlace(tx, ty)
      case (Nil, lst) => lst
      case (lst, Nil) => lst
    }
  }

  // Task 4
  private def add(u: Int, a: Int, c: Char): Int = {
    (u * a) + c.toInt
  }

  private def remove(u: Int, removeTerm: Int, c: Char): Int = {
    u - c.toInt * removeTerm
  }

  def rollingHash(s: String, base: Int): Int = {
    s.foldLeft(0)((rh, c) => add(rh, base, c))
  }

  private def nextHash(rh: Int, base: Int, removeTerm: Int, first: Char, next: Char): Int = {
    val removed = remove(rh, removeTerm, first)
    add(removed, base, next)
  }

  @tailrec
  private def isMatch(pattern: String, offsetS: String): Boolean = {
    (pattern, offsetS) match {
      case ("", _) => true
      case _ => if (pattern.head == offsetS.head) isMatch(pattern.tail, offsetS.tail) else false
    }
  }

  // Karp-Rabin
  def isSubstring(pattern: String, s: String): Boolean = {
    val SYSTEM_BASE = 257

    val len = strlen(pattern)
    val removeTerm = power(SYSTEM_BASE, len - 1)
    val hp = rollingHash(pattern, SYSTEM_BASE)

    @tailrec
    def auxSubstring(s: String, offsetS: String, rh: Int, i: Int): Boolean = {
      if (i >= len) {
        if (rh == hp) {
          if (isMatch(pattern, offsetS)) return true
        }
        (s, offsetS) match {
          case ("", _) => false
          case _ => auxSubstring(s.tail, offsetS.tail, nextHash(rh, SYSTEM_BASE, removeTerm, offsetS.head, s.head), i)
        }
      } else {
        s match {
          case "" => false
          case _ => auxSubstring(s.tail, offsetS, add(rh, SYSTEM_BASE, s.head), i + 1)
        }
      }
    }

    auxSubstring(s, s, 0, 0)
  }

  // Karp-Rabin multiple patterns
  def anySubstringMatch(sameLengthPatterns: List[String], s: String): Boolean = {
    val SYSTEM_BASE = 257

    val len = strlen(sameLengthPatterns.head)
    val removeTerm = power(SYSTEM_BASE, len - 1)
    val patternHashes = new HashMap[Int, String]

    def populateHashMap(values: List[String]): Unit = {
      values match {
        case Nil => Unit
        case h::t => {
          patternHashes.put(rollingHash(h, SYSTEM_BASE), h)
          populateHashMap(t)
        }
      }
    }
    populateHashMap(sameLengthPatterns)

    @tailrec
    def auxSubstring(s: String, offsetS: String, rh: Int, i: Int): Boolean = {
      if (i >= len) {
        if (patternHashes.contains(rh)) {
          if (isMatch(patternHashes.get(rh).toString, offsetS)) return true
        }
        (s, offsetS) match {
          case ("", _) => false
          case _ => auxSubstring(s.tail, offsetS.tail, nextHash(rh, SYSTEM_BASE, removeTerm, offsetS.head, s.head), i)
        }
      } else {
        s match {
          case "" => false
          case _ => auxSubstring(s.tail, offsetS, add(rh, SYSTEM_BASE, s.head), i + 1)
        }
      }
    }

    auxSubstring(s, s, 0, 0)
  }

  // Single pattern: O(n + m) time avg case, O(n*m) worst case, with hash collisions
  def find(pattern: String, xs: List[String]): List[String] = {
    filter(xs)(x => isSubstring(pattern, x))
  }

  // Multiple patterns
  def find(patterns: List[String], xs: List[String]): List[String] = {
    def auxFind(xs: List[String], accu: List[String]): List[String] = {
      xs match {
        case Nil => accu
        case h::t => {
          if (anyMatchArguments(h, patterns)((pattern, s) => isSubstring(pattern, s))) auxFind(t, h :: accu)
          else auxFind(t, accu)
        }
      }
    }
    auxFind(xs, List())
  }

  def findMultiple(sameLengthPatterns: List[String], xs: List[String]): List[String] = {
    anyMatchArguments()
  }

  // Task 5: O(n + m) time; O(max(n, m)) space, where len(zs), len(ys) = n, m
  def joinLists[A](xs: List[A], ys: List[A], zs: List[A]): List[A] = {
    fold_left(List(ys, xs), zs)((xs, accu) => concatLists(xs, accu))
  }
}
