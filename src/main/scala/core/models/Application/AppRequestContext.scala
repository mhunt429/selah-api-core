package core.models.Application

import java.util.UUID

// case class to record the authenticated user making the API request

case class AppRequestContext(
    userId: String, //Decoded JWT sub claim
    // ipAddress should come from the x-forwarded-for header but this can get weird locally so it's optional
    ipAddress: Option[String],
    requestId: UUID
)
