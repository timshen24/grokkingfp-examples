import cats.effect.IO
import cats.effect.unsafe.implicits.global
import cats.implicits._

val ioA: IO[Int] = IO.pure(3)
val ioB: IO[Int] = IO.pure(5)

for {
  a <- ioA
  b <- ioB
} yield a + b