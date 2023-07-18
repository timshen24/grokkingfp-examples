import cats.effect.IO
import fs2.{Stream, *}

object ch10_myCheckIns {
  object model {
    opaque type City = String

    object City {
      def apply(name: String): City = name

      extension (city: City) def name: String = city
    }

    case class CityStats(city: City, checkIns: Int)
  }

  import model._

  val checkIns: Stream[Pure, IO[City]] = Stream(
    IO.pure(City("Sydney")), IO.pure(City("Dublin")), IO.pure(City("Cape Town")), IO.pure(City("Lima")), IO.pure(City("Singapore"))
  )

  Stream(City("Sydney"), City("Dublin"), City("Cape Town"), City("Lima"), City("Singapore")).covary[IO]
}
