package library.management.system.com

import cats.data.Kleisli
import cats.effect.{ExitCode, IO, IOApp}
import library.management.system.com.config.SettingsComponent
import library.management.system.com.db.{DbMigrationComponent, DbTransactorComponent}
import library.management.system.com.db.components._
import library.management.system.com.http.BookRoutesComponent
import org.http4s.{Request, Response}
import org.http4s.blaze.server.BlazeServerBuilder
import org.http4s.server.Router

object Main
    extends IOApp
     with SettingsComponent
     with DbMigrationComponent
     with DbTransactorComponent
     with BookRepositoryComponent
     with ItemRepositoryComponent
     with BookServiceComponent
     with BookRoutesComponent {

  override val config: Settings = new Settings {}
  override val migration: Migration = new Migration {}
  override val transactor: DbTransactor = new DbTransactor {}
  private val dbTransactor = transactor.init(config.appConfig.load[IO].map(_.database))
  override val bookRepository: BookRepository = new BookRepository(dbTransactor)
  override val itemRepository: ItemRepository = new ItemRepository(dbTransactor)
  override val bookService: BookService = new BookService {}
  override val bookRoutes: BookRoutes = new BookRoutes {}

  override def run(args: List[String]): IO[ExitCode] =
    for {
      appConfig <- config.appConfig.load[IO]
      api = appConfig.api
      db = appConfig.database
      _ <- migration.migrate(db.url, db.user, db.password)
      routes = bookRoutes.routes
      application: Kleisli[IO, Request[IO], Response[IO]] = Router(
        "/api/v1" -> routes,
      ).orNotFound
      server <- BlazeServerBuilder[IO]
        .bindHttp(api.port, api.host)
        .withHttpApp(application)
        .serve
        .compile
        .drain
        .as(ExitCode.Success)
    } yield server
}