package library.management.system.com.db

import cats.effect._
import com.zaxxer.hikari.HikariConfig
import doobie.Transactor
import doobie.hikari.HikariTransactor
import library.management.system.com.config.Model.DbConfig
import library.management.system.com.config.SettingsComponent
import library.management.system.com.util.Constants.PostgresDriver
import org.typelevel.log4cats.Logger

trait DbTransactorComponent {
  this: SettingsComponent =>
  val transactor: DbTransactor
  trait DbTransactor {
    def init(database: IO[DbConfig])(implicit logger: Logger[IO]): Resource[IO, Transactor[IO]] =
      for {
        dbConfig <- database.toResource
        hikariConfig <- Resource.pure {
          val config = new HikariConfig()
          config.setDriverClassName(PostgresDriver)
          config.setJdbcUrl(dbConfig.url)
          config.setUsername(dbConfig.user)
          config.setPassword(dbConfig.password)
          config.setMaximumPoolSize(dbConfig.maxPoolSize)
          config
        }
        xa <- HikariTransactor.fromHikariConfig[IO](hikariConfig).attempt.flatMap {
          case Right(value) => Resource.pure(value)
          case Left(error) =>
            logger.error(s"Unexpected error while initializing the transactor: $error")
            Resource.raiseError(error)
        }
      } yield xa
  }
}
