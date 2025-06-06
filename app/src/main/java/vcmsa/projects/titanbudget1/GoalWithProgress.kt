package vcmsa.projects.titanbudget1

data class GoalWithProgress(
    val goalID: Int,
    val goalTitle: String,
    val Category: String,
    val IconUrl: String,
    val MinimumAmount: Int,
    val MaximumAmount: Int,
    val totalAmount: Double
)

