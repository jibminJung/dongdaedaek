package com.jimmy.dongdaedaek.data.api

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.toObject
import com.jimmy.dongdaedaek.domain.model.Review
import com.jimmy.dongdaedaek.domain.model.Store
import kotlinx.coroutines.tasks.await
import android.util.Log
import com.google.firebase.firestore.Query

class ReviewApiImpl(
    val firebaseFirestore: FirebaseFirestore
):ReviewApi {
    override suspend fun getStoreReview(storeId: String): List<Review> =
        firebaseFirestore.collection("reviews")
            .whereEqualTo("storeId",storeId)
            .orderBy("createdAt",Query.Direction.DESCENDING)
            .get()
            .await()
            .map { it.toObject<Review>() }

    override suspend fun uploadReview(review: Review): Review {
        val newReviewRef = firebaseFirestore.collection("reviews").document()
        val storeRef = firebaseFirestore.collection("stores").document(review.storeId!!)
        Log.d("uploading","api에서 업로드 중...")

        firebaseFirestore.runTransaction { transaction->
            val store = transaction.get(storeRef).toObject<Store>()

            val beforeRating = (store?.rating?:"0").toFloat()
            val beforeNumberOfReview = store?.numberOfReview?:0

            val newRating = (beforeRating * beforeNumberOfReview + review.rating!!) / (beforeNumberOfReview+1)

            transaction.set(
                storeRef,
                store!!.copy(
                    rating = newRating.toString(),
                    numberOfReview = beforeNumberOfReview+1
                )
            )

            transaction.set(
                newReviewRef,
                review,
                SetOptions.merge()
            )

        }.await()

        return newReviewRef.get().await().toObject<Review>()!!
    }

}