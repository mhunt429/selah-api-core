package core.models.Http

case class HttpResponse[A](
    statusCode: Int,
    data: Option[A],
    errors: List[String] = List.empty
                          )

case class HttpErrors(
    message: String
                     )