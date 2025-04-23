package com.photobuddy.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "capsules")
data class MemoryCapsule(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val name: String,
    val description: String,
    val unlockDate: Long,
    val isShared: Boolean = false
)
