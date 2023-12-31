package fpinscala.exercises.datastructures

import scala.annotation.tailrec

/** `List` data type, parameterized on a type, `A`. */
enum List[+A]:
  /** A `List` data constructor representing the empty list. */
  case Nil
  /** Another data constructor, representing nonempty lists. Note that `tail` is another `List[A]`,
    which may be `Nil` or another `Cons`.
   */
  case Cons(head: A, tail: List[A])

object List: // `List` companion object. Contains functions for creating and working with lists.
  def sum(ints: List[Int]): Int = ints match // A function that uses pattern matching to add up a list of integers
    case Nil => 0 // The sum of the empty list is 0.
    case Cons(x,xs) => x + sum(xs) // The sum of a list starting with `x` is `x` plus the sum of the rest of the list.

  def product(doubles: List[Double]): Double = doubles match
    case Nil => 1.0
    case Cons(0.0, _) => 0.0
    case Cons(x,xs) => x * product(xs)

  def apply[A](as: A*): List[A] = // Variadic function syntax
    if as.isEmpty then Nil
    else Cons(as.head, apply(as.tail*))

  @annotation.nowarn // Scala gives a hint here via a warning, so let's disable that
  val result = List(1,2,3,4,5) match
    case Cons(x, Cons(2, Cons(4, _))) => x
    case Nil => 42
    case Cons(x, Cons(y, Cons(3, Cons(4, _)))) => x + y
    case Cons(h, t) => h + sum(t)
    case _ => 101

  def append[A](a1: List[A], a2: List[A]): List[A] =
    a1 match
      case Nil => a2
      case Cons(h,t) => Cons(h, append(t, a2))

  def foldRight[A,B](as: List[A], acc: B, f: (A, B) => B): B = // Utility functions
    as match
      case Nil => acc
      case Cons(x, xs) => f(x, foldRight(xs, acc, f))

  def sumViaFoldRight(ns: List[Int]): Int =
    foldRight(ns, 0, (x,y) => x + y)

  def productViaFoldRight(ns: List[Double]): Double =
    foldRight(ns, 1.0, _ * _) // `_ * _` is more concise notation for `(x,y) => x * y`; see sidebar

  def tail[A](l: List[A]): List[A] =
    l match
      case Nil => sys.error("the list is nil")
      case Cons(x, xs) => xs

  def setHead[A](l: List[A], h: A): List[A] =
    l match
      case Nil => sys.error("the list is nil")
      case Cons(x, xs) => List.Cons(h, xs)

  @tailrec
  def drop[A](l: List[A], n: Int): List[A] =
    l match
      case Nil => List()
      case Cons(x, xs) => if ( n <= 0 ) l else drop(tail(l), n-1)

  @tailrec
  def dropWhile[A](l: List[A], f: A => Boolean): List[A] =
    l match
      case Nil => List()
      case Cons(x, xs) => if (f(x)) dropWhile(xs, f) else l

//  @tailrec
  def init[A](l: List[A]): List[A] =
    l match
      case Nil => sys.error("the list is nil")
      case Cons(x, Nil) => List()
      case Cons(x, Cons(_, Nil)) => List.Cons(x, Nil) // Remove the last entry
      case Cons(x, xs) => List.Cons(x, List.init(xs))

  def length[A](l: List[A]): Int =
    l match
      case Nil => 0
      case Cons(x, xs) => List.foldRight(l, 0, (_, acc) => acc + 1)

  @tailrec
  def last[A](l: List[A]): A =
    l match
      case Nil => sys.error("the list is nil")
      case Cons(x, Nil) => x
      case Cons(x, xs) => last(xs)

  def head[A](l: List[A]): A =
    l match
      case Nil => sys.error("the list is nil")
      case Cons(x, Nil) => x
      case Cons(x, xs) => x

// Non tail rec answer :-(
//  @tailrec
//  def foldLeft[A,B](l: List[A], acc: B, f: (B, A) => B): B =
//    l match
//      case Nil => acc
//      case Cons (x, xs) =>
//        val lastOne = last(l)
//        val firstOnes = init(l)
//        f(foldLeft(firstOnes, acc, f), lastOne)

// Seems ok but test is failing!?!?
//  @tailrec
//  def foldLeft[A, B](l: List[A], acc: B, f: (B, A) => B): B =
//    l match
//      case Nil => acc
//      case Cons(x, xs) =>
//        val lastOne = last(l)
//        val firstOnes = init(l)
//        foldLeft(firstOnes, f(acc, lastOne), f)
//
//  println("ADDITION: " + foldLeft(List(1,2,3,4,5), 0, _ + _))
//  println("MULTIPLICATION: " + foldLeft(List(1,2,3,4,5), 1, _ * _))
//
// Because the order does not matter you can flip the head annd the tail
// @tailrec
  def foldLeft[A, B](l: List[A], acc: B, f: (B, A) => B): B =
    l match
      case Nil => acc
      case Cons(head, tail) =>
        foldLeft(tail, f(acc, head), f)

  def sumViaFoldLeft(ns: List[Int]): Int =
    foldLeft(ns, 0, _ + _)

  def productViaFoldLeft(ns: List[Double]): Double =
    foldLeft(ns, 1, _ * _)

  def lengthViaFoldLeft[A](l: List[A]): Int = l match
    case Nil => 0
    case Cons(x, xs) => foldLeft(l, 0, (acc, _) => acc + 1)

// First attempt without fold
//  def reverse[A](l: List[A]): List[A] =
//    l match
//      case Nil => List()
//      case Cons(head, tail) =>
//        val lastOne = last(l)
//        val firstOnes = init(l)
//        List.Cons(lastOne, reverse(firstOnes))

// My failing attempt
//  def reverse[A](l: List[A]): List[A] =
//    l match
//      case Nil => List()
//      case Cons(first, Cons(second, Nil)) => Cons(second, Cons(first, Nil)) // Switch entries
//      case Cons(head, tail) =>
//        List.Cons(
//          foldLeft(tail, Nil: List[A], (first, second) =>  Cons(second, first)),
//          List(head)
//        )

// After looking at the answer
  def reverse[A] (as: List[A]): List[A] = foldLeft(as, Nil: List[A], (acc, a) => Cons(a, acc))

  def appendViaFoldRight[A](l: List[A], r: List[A]): List[A] = foldRight(l, r, (first, second) => Cons(first, second))

  def concat[A](l: List[List[A]]): List[A] = foldRight(l, Nil: List[A], append)

  def incrementEach(l: List[Int]): List[Int] = foldRight(l, Nil: List[Int], (x,y) => Cons(x + 1, y))

  def doubleToString(l: List[Double]): List[String] = foldRight(l, Nil: List[String], (x,y) => Cons(x.toString, y))

  def map[A,B](l: List[A], f: A => B): List[B] = foldRight(l, Nil: List[B], (x,y) => Cons(f(x), y))

  def filter[A](as: List[A], f: A => Boolean): List[A] = foldRight(as, Nil: List[A], (x,y) => if f(x) then Cons(x,y) else y)

  println("MAP: " + map(List(1,2,3,4,5), i => List(i,i)))
  def flatMap[A,B](as: List[A], f: A => List[B]): List[B] = foldRight(as, Nil: List[B], (x, y) => append(f(x), y))

  println("FLATMAP: " + flatMap(List(1, 2, 3, 4, 5), i => List(i, i)))

  def filterViaFlatMap[A](as: List[A], f: A => Boolean): List[A] = flatMap(as, a => if f(a) then List(a) else List())

// My attempt
//  def addPairwise(a: List[Int], b: List[Int]): List[Int] = a match
//    case Nil => List()
//    case Cons(hda, Nil) => b match
//      case Nil => List()
//      case Cons(hdb, Nil) => List(hda, hdb)
//      case Cons(hdb, tlb) => List(hda, hdb)
//    case Cons(hda, tla) => b match
//      case Nil => List()
//      case Cons(hdb, Nil) => List(hda, hdb)
//      case Cons(hdb, tlb) => flatMap(a, first => append(List(first + head(b)), addPairwise(tail(a), tail(b))))

  def addPairwise(a: List[Int], b: List[Int]): List[Int] = (a, b) match
    case (Nil, _) => Nil
    case (_, Nil) => Nil
    case (Cons(h1, t1), Cons(h2, t2)) => Cons(h1 + h2, addPairwise(t1, t2))

//  def zipWith[A, B, C](a: List[A], b: List[B], f: (A, B) => C): List[C] = (a, b) match
//    case (Nil, _) => Nil
//    case (_, Nil) => Nil
//    case (Cons(h1, t1), Cons(h2, t2)) => Cons(f(h1, h2), zipWith(t1, t2, f))

  def zipWith[A, B, C](a: List[A], b: List[B], f: (A, B) => C): List[C] =
    @annotation.tailrec
    def loop(a: List[A], b: List[B], acc: List[C]): List[C] = (a, b) match
      case (Nil, _) => acc
      case (_, Nil) => acc
      case (Cons(h1, t1), Cons(h2, t2)) => loop(t1, t2, Cons(f(h1, h2), acc))
    reverse(loop(a, b, Nil))

  @annotation.tailrec
  def startsWith[A](l: List[A], prefix: List[A]): Boolean = (l, prefix) match
    case (_, Nil) => true
    case (Cons(h, t), Cons(h2, t2)) if h == h2 => startsWith(t, t2)
    case _ => false

  def hasSubsequence[A](sup: List[A], sub: List[A]): Boolean = sup match
    case Nil => sub == Nil
    case _ if startsWith(sup, sub) => true
    case Cons(h, t) => hasSubsequence(t, sub)
