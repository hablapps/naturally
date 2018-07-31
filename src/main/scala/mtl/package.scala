package naturally

import cats._, cats.data._

package object mtl
  extends MonadReaderInstances
  with MonadStateInstances
