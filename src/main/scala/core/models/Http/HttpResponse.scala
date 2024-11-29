package core.models.Http

case class HttpResponse[A](
    statusCode: Int,
    data: Option[A],
    errors: List[String] = List.empty
)

case class HttpErrors(
    message: String
)

/*
Most API commands can simply return the newly created or updated Id  so this
provides a common interface of accomplishing that
 */
case class BaseCommandResponse(
    id: String
)
