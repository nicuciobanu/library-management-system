package library.management.system.com.db

import cats.effect._
import cats.effect.implicits.effectResourceOps
import com.zaxxer.hikari.HikariConfig
import doobie.Transactor
import doobie.hikari.HikariTransactor
import library.management.system.com.config.Model.Database
import library.management.system.com.config.SettingsComponent
import library.management.system.com.util.Constants.PostgresDriver

trait DbTransactorComponent {
  this: SettingsComponent =>
  val transactor: DbTransactor

  trait DbTransactor {
    def init(database: IO[Database]): Resource[IO, Transactor[IO]] =
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
        xa <- HikariTransactor.fromHikariConfig[IO](hikariConfig)
      } yield xa
  }
}
