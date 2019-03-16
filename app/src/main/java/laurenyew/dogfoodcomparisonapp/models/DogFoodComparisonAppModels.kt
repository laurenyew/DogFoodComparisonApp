package laurenyew.dogfoodcomparisonapp.models

/**
 * Food reference model
 */
class FoodRef(
    val id: String,
    val brandName: String,
    val nutrition: String
)

/**
 * Company model for a given food
 */
class Company(
    val id: String,
    val companyName: String,
    val price: String
)