package utils

import doobie.Meta

//Implicit metas for mapping Scala datatypes to SQL types
object BaseSqlCodecs {
  implicit val bigIntMeta: Meta[BigInt] =
    Meta[BigDecimal].imap(_.toBigInt)(BigDecimal(_))
}
