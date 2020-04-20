package com.sovathna.khmerdictionary.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "dict")
data class WordEntity(
  @ColumnInfo(name = "word")
  val word: String,
  @ColumnInfo(name = "definition")
  val definition: String,
  @PrimaryKey(autoGenerate = true)
  @ColumnInfo(name = "_id")
  val id: Int = 0
)