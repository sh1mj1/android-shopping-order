package woowacourse.shopping.data.model

import woowacourse.shopping.domain.model.Product

data class ProductData(
    val id: Long,
    val imgUrl: String,
    val name: String,
    val price: Int,
    val category: String = "",
)

fun ProductData.toDomain(quantity: Int = 0): Product =
    Product(
        id,
        imgUrl,
        name,
        price,
        quantity = quantity,
        category = category,
    )
