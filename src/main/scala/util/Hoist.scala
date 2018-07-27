package naturally
package util

trait Hoist[HF[_[_], _]]{
  def hoist[P[_], Q[_]](f: P ~> Q): HF[P, ?] ~> HF[Q, ?]
}

object Hoist{

}
