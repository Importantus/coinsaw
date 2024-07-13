package digital.fischers.coinsaw.ui.utils

/**
 * Format a string as a decimal number.
 *
 * @param decimalPlaces The number of decimal places to keep.
 * @return The formatted string.
 */
fun String.formatAsDecimal(
    decimalPlaces: Int = 2,
): String {
    // Check if the value is a valid number: Replace all commas with dots and all non digits (excepts dots) with empty string
    var amount = this.replace(",", ".").replace(Regex("[^\\d.]"), "")
    // If a dot is the first character, add a 0 in front of it
    if (amount.startsWith(".")) {
        amount = "0$amount"
    }
    // If there are multiple dots, remove all but the first one
    while (amount.count { it == '.' } > 1) {
        amount = amount.reversed().replaceFirst(".", "").reversed()
    }
    // If there are more than 1 digits after the dot, remove all but the first 1
    if (amount.contains(".")) {
        val split = amount.split(".")
        if (split[1].length > decimalPlaces) {
            amount = amount.removeRange(amount.indexOf(".") + decimalPlaces + 1, amount.length)
        }
    }

    return amount
}