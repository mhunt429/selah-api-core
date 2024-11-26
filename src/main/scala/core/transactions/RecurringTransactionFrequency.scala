package core.transactions

sealed trait RecurringTransactionFrequency

object RecurringTransactionFrequency {
  case object OneTime extends RecurringTransactionFrequency

  case object Weekly extends RecurringTransactionFrequency

  case object BiWeekly extends RecurringTransactionFrequency

  case object Monthly extends RecurringTransactionFrequency

  case object Other extends RecurringTransactionFrequency

}
