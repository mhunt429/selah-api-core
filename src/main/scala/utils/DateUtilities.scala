package utils

import java.time.Duration
import java.time.Instant.now
import java.util.Date

object DateUtilities {
  def addMinutesToCurrentTime(minutesToAdd: Int): Date =
    Date.from(
      now.plus(
        Duration.ofMinutes(minutesToAdd)
      )
    )

  def addHoursToCurrentTime(hoursToAdd: Int): Date =
    Date.from(
      now.plus(
        Duration.ofHours(hoursToAdd)
      )
    )

  def addDaysToCurrentTime(daysToAdd: Int): Date =
    Date.from(
      now.plus(
        Duration.ofDays(daysToAdd)
      )
    )
}
