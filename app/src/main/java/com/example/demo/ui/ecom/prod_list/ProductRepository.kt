package com.example.demo.ui.ecom.prod_list

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL
import kotlin.random.Random

class ProductRepository {
    fun getProductPager() = Pager(
        config = PagingConfig(
            pageSize = 10,
            enablePlaceholders = false,
            initialLoadSize = 10
        ),
        pagingSourceFactory = { ProductPagingSource(this) }
    ).flow

    suspend fun fetchProducts(page: Int, itemsPerPage: Int = 10): List<Product> = withContext(Dispatchers.IO) {
        try {
            (1..itemsPerPage).map { i ->
                val id = (page - 1) * itemsPerPage + i
                val imageUrl = fetchDogImageUrl()
                Product(
                    id = id,
                    name = "Dog Product $id",
                    price = 10.0 + (i * 1.5),
                    imageUrl = imageUrl,
                    rating = 3 + Random.nextFloat() * 2,  // Random rating between 3 and 5
                    description = "Description for Product $id",
                    images = List(3) { fetchDogImageUrl() },
                    reviews = listOf(
                        Review("User1", "Great product!", 5),
                        Review("User2", "Good value for money", 4)
                    )
                )
            }
        } catch (e: Exception) {
            throw Exception("Error fetching products: ${e.message}")
        }
    }

    suspend fun getProductDetails(productId: Int): Product = withContext(Dispatchers.IO) {
        val imageUrl = fetchDogImageUrl()
        Product(
            id = productId,
            name = "Dog Product $productId",
            price = 10.0 + (productId * 1.5),
            imageUrl = imageUrl,
            rating = 3 + Random.nextFloat() * 2,
            description = "Detailed description for Dog Product $productId. This product is amazing and has many great features.",
            images = List(3) { fetchDogImageUrl() },
            reviews = listOf(
                Review("User1", "Great product! I love it.", 5),
                Review("User2", "Good value for money, but could be better.", 4),
                Review("User3", "Decent product, does the job.", 3)
            )
        )
    }

    private suspend fun fetchDogImageUrl(): String = withContext(Dispatchers.IO) {
        try {
            Log.i("ProductRepository", "Fetching dog image URL...")
            val response = URL("https://dog.ceo/api/breeds/image/random").readText()
            val jsonObject = JSONObject(response)
            jsonObject.getString("message")
        } catch (e: Exception) {
            throw Exception("Error fetching dog image: ${e.message}")
        }
    }
}
class ProductPagingSource(
    private val repository: ProductRepository
) : PagingSource<Int, Product>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Product> {
        return try {
            val page = params.key ?: 1
            val products = repository.fetchProducts(page, params.loadSize)

            LoadResult.Page(
                data = products,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (products.isEmpty()) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Product>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}