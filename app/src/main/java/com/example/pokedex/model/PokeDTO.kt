package com.example.pokedex.model

import com.google.gson.annotations.SerializedName

data class PokeDTO(
    val id: Int,
    val name: String,
    val height: Int,
    val weight: Int,
    val types: List<Type>,
    val abilities: List<Ability>,
    val stats: List<Stat>,
    val moves: List<Move>,
    val forms: List<Form>,
    @SerializedName("held_items")
    val heldItems: List<HeldItem>
)

data class Type(
    @SerializedName("type") val type: TypeDetail
)

data class TypeDetail(
    val name: String
)

data class AbilityDetail(
    val name: String,
    val url: String
)

data class Ability(
    @SerializedName("ability") val ability: AbilityDetail,
    @SerializedName("is_hidden") val isHidden: Boolean,
    val slot: Int
)


data class Stat(
    @SerializedName("stat") val stat: StatDetail,
    @SerializedName("base_stat") val baseStat: Int
)

data class StatDetail(
    val name: String
)

data class Move(
    @SerializedName("move") val move: MoveDetail
)

data class MoveDetail(
    val name: String
)

data class Form(
    val name: String
)

data class HeldItem(
    @SerializedName("item") val item: ItemDetail
)

data class ItemDetail(
    val name: String
)

