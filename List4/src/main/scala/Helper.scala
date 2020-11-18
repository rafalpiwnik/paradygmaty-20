object Helper {
  def treeDepth[A](t: BTree[A]): Int = t match {
    case Empty => 0
    case Vertex(_, l, r) => 1 + (treeDepth(l) max treeDepth(r))
  }

  def splitListPowers[A](xs: List[A], base: Int): List[List[A]] = {
    if (base <= 0) Nil

    xs match {
      case h :: t => List(h) :: splitListPowers(t, base)
    }
  }

  def formTree[Int](upL: List[Int], upR: List[Int])(lowL: List[BTree[Int]], lowR: List[BTree[Int]])(accL: List[BTree[Any]], accR: List[BTree[Any]]): (List[BTree[Any]], List[BTree[Any]]) = {
    (upL, upR) match {
      case (t1 :: tailL, t2 :: tailR) => {
        val List(leftSub1, rightSub1) = lowL.take(2)
        val List(leftSub2, rightSub2) = lowR.take(2)

        if (t1 == t2) {
          (leftSub1, rightSub1, leftSub2, rightSub2) match {
            case (Empty, Empty, Empty, Empty) => formTree(tailL, tailR)(lowL.tail.tail, lowR.tail.tail)(Empty :: accL, Empty :: accR)
            case (l1, Empty, l2, Empty) =>  formTree(tailL, tailR)(lowL.tail.tail, lowR.tail.tail)(Vertex(-1, l1, Empty) :: accL, Vertex(-1, l2, Empty) :: accR)
            case (Empty, r1, Empty, r2) => formTree(tailL, tailR)(lowL.tail.tail, lowR.tail.tail)(Vertex(-1, Empty, r1) :: accL, Vertex(-1, Empty, r2) :: accR)
            case _ => formTree(tailL, tailR)(lowL.tail.tail, lowR.tail.tail)(Vertex(t1, leftSub1, rightSub1) :: accL, Vertex(t2, leftSub2, rightSub2) :: accR)
          }
        }
        else {
          formTree(tailL, tailR)(lowL.tail.tail, lowR.tail.tail)(Vertex(t1, leftSub1, rightSub1) :: accL, Vertex(t2, leftSub2, rightSub2) :: accR)
        }

      }
      case (Nil, Nil) => (accL.reverse, accR.reverse)
      case _ => throw new IllegalArgumentException("Tree depth error")
    }
  }
}
