package infrastructure.repository

import cats.effect.IO
import core.models.AccountConnector.sql.AccountConnectorSqlInsert
import doobie.*
import doobie.implicits.*
import doobie.implicits.javasql.TimestampMeta

import java.sql.Timestamp
import java.time.Instant

trait AccountConnectorRepository {
  def insertAccountConnector(accountConnectorSql: AccountConnectorSqlInsert): IO[Long]
}

class AccountConnectorRepositoryImpl(xa: Transactor[IO])
    extends AccountConnectorRepository {
  implicit val instantMeta: Meta[Instant] =
    Meta[Timestamp].imap(_.toInstant)(Timestamp.from)

  def insertAccountConnector(
      accountConnectorSql: AccountConnectorSqlInsert
  ): IO[Long] = {
    BaseRepository.insertWithIdAutoCommit(
      xa,
      accountConnectorInsertSql(accountConnectorSql)
    )
  }

  private def accountConnectorInsertSql(
      accountConnector: AccountConnectorSqlInsert
  ) = {
    sql"""
         INSERT INTO account_connector
         (
          app_last_changed_by,
          original_insert,
          last_update,
          user_id,
          institution_id,
          institution_name,
          date_connected,
          encrypted_access_token
         )
         VALUES(
            ${accountConnector.userId},
            ${Instant.now()},
            ${Instant.now()},
            ${accountConnector.userId},
            ${accountConnector.institutionId},
            ${accountConnector.institutionName},
            ${Instant.now()}
            ${accountConnector.encryptedAccessToken}
         )
       """
  }
}
