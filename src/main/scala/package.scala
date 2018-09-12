import scalaz._

package object naturally
    extends SurfaceGetter.Syntax
    with SurfaceGetterK.Syntax
    with SurfaceLens.Syntax
    with naturally.monocle.Getter.Implicits {

  type Lens[S, A] = State[A, ?] ~> State[S, ?]

  implicit class LensSyntax[S, A](ln: Lens[S, A]) {
    def get(s: S): A = ln(State.get).eval(s)
    def set(a: A)(s: S): S = ln(State.put(a)).exec(s)
    def modify(f: A => A)(s: S): S = ln(State.modify(f)).exec(s)
  }
}

