package vcmsa.projects.titanbudget1

import kotlinx.serialization.Serializable

@Serializable
data class UserGoal(
    val GoalID: Int? = null,
    val GoalTitle: String,
    val Category: String,
    val IconUrl: String,
    val MinimumAmount: Int,
    val MaximumAmount: Int,
    val UserID: String
)

/*
   Code found in Supabase
   Author: Supabase
   Link: https://supabase.com/docs/guides/getting-started/quickstarts/kotlin
   Accessed: 28 April 2025

   @Serializable
data class Instrument(
    val id: Int,
    val name: String,
)
*/
