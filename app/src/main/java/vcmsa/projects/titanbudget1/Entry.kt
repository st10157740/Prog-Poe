package vcmsa.projects.titanbudget1

import kotlinx.serialization.Serializable

@Serializable
data class Entry(
    val Name: String,
    val Description: String,
    val PhotoUrl: String,
    val Amount: Double,
    val Date: String,
    val Category: String,
    val IconUrl: String,
    val UserID: String,
    val Type: String,
    val GoalID: Int? = null
)