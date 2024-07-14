package digital.fischers.coinsaw.data.util

import digital.fischers.coinsaw.data.database.Bill
import digital.fischers.coinsaw.data.database.CalculatedTransaction
import digital.fischers.coinsaw.ui.utils.roundHalfUp
import java.math.RoundingMode
import java.util.UUID

fun calculateTransactions(transactions: List<Bill>, groupId: String): List<CalculatedTransaction> {
    // Dictionary to store the "net worth" of all users
    val netAmounts = mutableMapOf<String, Double>()

    // Iterate through all bills
    for (bill in transactions) {
        var total = 0.0
        for (split in bill.splittings) {
            total += split.percent
            netAmounts[split.userId] = netAmounts.getOrDefault(split.userId, 0.0) - (bill.amount * split.percent)
        }
        netAmounts[bill.userId] = netAmounts.getOrDefault(bill.userId, 0.0) + bill.amount * total
    }

    val adjustedTransactions = mutableListOf<CalculatedTransaction>()

    // If all netAmounts are positive, return []
    if (netAmounts.values.all { it >= 0 }) {
        return adjustedTransactions
    }

    // Use the dictionary to even out the debts
    val roundValue = 0.01
    while (netAmounts.values.any { kotlin.math.abs(it) >= roundValue }) {
        // Find the largest and smallest value
        val (spender, spenderAmount) = netAmounts.maxByOrNull { if (it.value > 0) it.value else Double.NEGATIVE_INFINITY }!!
        val (owee, oweeAmount) = netAmounts.minByOrNull { if (it.value < 0) it.value else Double.POSITIVE_INFINITY }!!

        val transactionAmount = kotlin.math.min(spenderAmount, -oweeAmount)

        adjustedTransactions.add(CalculatedTransaction(
            id = UUID.randomUUID().toString(),
            groupId = groupId,
            amount = transactionAmount.roundHalfUp(),
            payerId = owee,
            payeeId = spender
        ))
        netAmounts[spender] = spenderAmount - transactionAmount
        netAmounts[owee] = oweeAmount + transactionAmount
    }
    return adjustedTransactions
}