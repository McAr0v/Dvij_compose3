package kz.dvij.dvij_compose3.firebase

data class PlacesAdsClass(
    val logo: String? = null,
    val placeName: String? = null,
    val placeDescription: String? = null,
    val phone: String? = null,
    val whatsapp: String? = null,
    val telegram: String? = null,
    val instagram: String? = null,
    val category: String? = null,
    val city: String? = null,
    val address: String? = null,
    val placeKey: String? = null,
    val owner: String? = null,
    val openTime: String? = null,
    val closeTime: String? = null,
)

data class PlacesDialogClass(

    val placeHeadline: String? = null,
    val placeAddress: String? = null,
    val placeKey: String? = null

)
