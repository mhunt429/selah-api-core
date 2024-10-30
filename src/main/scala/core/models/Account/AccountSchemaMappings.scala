package core.models.Account

import doobie.postgres.implicits._
import doobie.{Meta, Read}
import java.time.Instant

// Implicit type to serialize SQL Doobie queries to Account Data Access Object
object AccountSchemaMappings {

  implicit val accountRead: Read[Account] =
    Read[
      (
        Instant,
          Instant,
          Long,
          Long,
          Instant,
          Option[String]
        )
    ].map {
      case (
        originalInsert,
        lastUpdate,
        appLastChangedBy,
        id,
        dateCreated,
        accountName
        ) =>
        Account(
          originalInsert,
          lastUpdate,
          appLastChangedBy,
          id,
          dateCreated,
          accountName
        )
    }
}
