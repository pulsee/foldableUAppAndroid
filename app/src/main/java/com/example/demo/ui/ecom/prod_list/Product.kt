package com.example.demo.ui.ecom.prod_list

data class Product(
    val id: Int,
    val name: String,
    val price: Double,
    val imageUrl: String,
    val rating: Float,
    val description: String,
    val images: List<String>,
    val reviews: List<Review>
)
data class Review(
    val userName: String,
    val comment: String,
    val rating: Int
)