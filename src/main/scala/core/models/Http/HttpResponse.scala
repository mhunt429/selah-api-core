package core.models.Http

case class HttpResponse[A](
    statusCode: Int,
    data: A,
    errors: List[HttpErrors] = List.empty
                          )

case class HttpErrors(
    key: String,
    message: String
                     )