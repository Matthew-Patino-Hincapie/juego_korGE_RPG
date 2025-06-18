package com.tuempresa.pokemonlike.player

import korlibs.korge.view.*
import korlibs.image.bitmap.*
import korlibs.korge.view.animation.*
import korlibs.korge.input.*
import korlibs.math.geom.*
import korlibs.image.color.*

class Player : Container() {
    private lateinit var sprite: SpriteAnimationView
    private lateinit var walkDown: SpriteAnimation
    private lateinit var walkLeft: SpriteAnimation
    private lateinit var walkRight: SpriteAnimation
    private lateinit var walkUp: SpriteAnimation
    private var currentDirection: String = "down"
    private val moveSpeed = 3.0
    private var isMoving = false

    suspend fun initPlayer() {
        try {
            // Cargar spritesheet del jugador desde recursos
            val spriteMap = resourcesVfs["player/player_walk.png"].readBitmap()
            
            // Definir animaciones para cada dirección (32x32 píxeles por frame)
            walkDown = SpriteAnimation(
                spriteMap = spriteMap,
                spriteWidth = 32,
                spriteHeight = 32,
                columns = 3,
                rows = 4,
                offset = 0
            )
            walkLeft = SpriteAnimation(
                spriteMap = spriteMap,
                spriteWidth = 32,
                spriteHeight = 32,
                columns = 3,
                rows = 4,
                offset = 3
            )
            walkRight = SpriteAnimation
