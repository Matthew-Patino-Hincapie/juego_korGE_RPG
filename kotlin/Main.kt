package com.tuempresa.pokemonlike

import korlibs.korge.*
import korlibs.korge.scene.*
import korlibs.korge.view.*
import korlibs.korge.input.*
import korlibs.image.color.*
import korlibs.math.geom.*
import korlibs.korge.ldtk.*
import korlibs.image.bitmap.*
import korlibs.korge.view.animation.*
import korlibs.korge.tween.*
import korlibs.time.*

suspend fun main() = Korge(
    width = 640,
    height = 360,
    bgcolor = Colors["#2b2b2b"],
    title = "PokemonLike - Aventura en Kotlin"
) {
    sceneContainer().changeTo({ MainGameScene() })
}

class MainGameScene : Scene() {
    private lateinit var player: SpriteAnimationView
    private lateinit var gameMap: View
    private val npcs = mutableListOf<NPC>()
    private var isDialogActive = false
    
    override suspend fun SContainer.sceneInit() {
        // Cargar y mostrar el mapa
        loadGameMap()
        
        // Crear y configurar el jugador
        createPlayer()
        
        // Crear NPCs
        createNPCs()
        
        // Configurar controles
        setupControls()
        
        // Configurar cámara para seguir al jugador
        setupCamera()
    }
    
    private suspend fun SContainer.loadGameMap() {
        try {
            // Cargar mapa desde LDtk
            val ldtkMap = resourcesVfs["maps/primer_mapa.ldtk"].readLDtkMap()
            val level = ldtkMap.levels.first()
            gameMap = level.createView()
            addChild(gameMap)
        } catch (e: Exception) {
            // Si no hay mapa LDtk, crear un mapa básico de prueba
            gameMap = solidRect(800, 600, Colors.GREEN) {
                position(0, 0)
            }
            addChild(gameMap)
            
            // Agregar algunos elementos visuales básicos
            solidRect(64, 64, Colors.BROWN) { position(200, 200) }
            solidRect(64, 64, Colors.BROWN) { position(400, 300) }
            solidRect(64, 64, Colors.BLUE) { position(600, 150) }
        }
    }
    
    private suspend fun SContainer.createPlayer() {
        try {
            // Cargar spritesheet del jugador
            val playerSpriteMap = resourcesVfs["player/player_walk.png"].readBitmap()
            
            // Definir animaciones para cada dirección
            val walkDown = SpriteAnimation(
                spriteMap = playerSpriteMap,
                spriteWidth = 32,
                spriteHeight = 32,
                columns = 3,
                rows = 4,
                offset = 0
            )
            val walkLeft = SpriteAnimation(
                spriteMap = playerSpriteMap,
                spriteWidth = 32,
                spriteHeight = 32,
                columns = 3,
                rows = 4,
                offset = 3
            )
            val walkRight = SpriteAnimation(
                spriteMap = playerSpriteMap,
                spriteWidth = 32,
                spriteHeight = 32,
                columns = 3,
                rows = 4,
                offset = 6
            )
            val walkUp = SpriteAnimation(
                spriteMap = playerSpriteMap,
                spriteWidth = 32,
                spriteHeight = 32,
                columns = 3,
                rows = 4,
                offset = 9
            )
            
            player = sprite(walkDown) {
                position(320, 180) // Centro de la pantalla
                scale = 2.0 // Hacer el sprite más grande
            }
            addChild(player)
            
            // Guardar las animaciones como propiedades del sprite
            player.setExtra("walkDown", walkDown)
            player.setExtra("walkLeft", walkLeft)
            player.setExtra("walkRight", walkRight)
            player.setExtra("walkUp", walkUp)
            player.setExtra("currentDirection", "down")
            
        } catch (e: Exception) {
            // Si no hay sprite, crear un rectángulo simple
            player = sprite(Bitmap32(32, 32, Colors.YELLOW)) {
                position(320, 180)
                scale = 2.0
            }
            addChild(player)
        }
    }
    
    private suspend fun SContainer.createNPCs() {
        // NPC Profesor
        val profesor = NPC(
            sprite = createNPCSprite(Colors.BLUE),
            position = Point(150, 100),
            name = "Profesor Oak",
            dialogType = "profesor",
            message = "¡Hola, joven entrenador! ¿Estás listo para comenzar tu aventura Pokémon?"
        )
        npcs.add(profesor)
        addChild(profesor.sprite)
        
        // NPC Enfermera
        val enfermera = NPC(
            sprite = createNPCSprite(Colors.PINK),
            position = Point(500, 200),
            name = "Enfermera Joy",
            dialogType = "enfermera",
            message = "¡Bienvenido al Centro Pokémon! ¿Necesitas curar a tus Pokémon?"
        )
        npcs.add(enfermera)
        addChild(enfermera.sprite)
        
        // NPC Aldeano
        val aldeano = NPC(
            sprite = createNPCSprite(Colors.ORANGE),
            position = Point(300, 400),
            name = "Aldeano",
            dialogType = "aldeano",
            message = "He escuchado que hay Pokémon raros en el bosque cercano..."
        )
        npcs.add(aldeano)
        addChild(aldeano.sprite)
    }
    
    private fun createNPCSprite(color: RGBA): SpriteAnimationView {
        return sprite(Bitmap32(32, 32, color)) {
            scale = 2.0
        }
    }
    
    private fun SContainer.setupControls() {
        val moveSpeed = 3.0
        
        addUpdater {
            if (isDialogActive) return@addUpdater
            
            var moving = false
            var newDirection = player.getExtra("currentDirection") as String
            
            when {
                input.keys.pressing(Key.LEFT) -> {
                    player.x -= moveSpeed
                    newDirection = "left"
                    moving = true
                }
                input.keys.pressing(Key.RIGHT) -> {
                    player.x += moveSpeed
                    newDirection = "right"
                    moving = true
                }
                input.keys.pressing(Key.UP) -> {
                    player.y -= moveSpeed
                    newDirection = "up"
                    moving = true
                }
                input.keys.pressing(Key.DOWN) -> {
                    player.y += moveSpeed
                    newDirection = "down"
                    moving = true
                }
            }
            
            // Actualizar animación si cambió la dirección o el estado de movimiento
            if (moving) {
                if (newDirection != player.getExtra("currentDirection")) {
                    player.setExtra("currentDirection", newDirection)
                    val animation = when (newDirection) {
                        "left" -> player.getExtra("walkLeft") as? SpriteAnimation
                        "right" -> player.getExtra("walkRight") as? SpriteAnimation
                        "up" -> player.getExtra("walkUp") as? SpriteAnimation
                        else -> player.getExtra("walkDown") as? SpriteAnimation
                    }
                    animation?.let { player.playAnimationLooped(it) }
                }
            } else {
                player.stopAnimation()
            }
            
            // Verificar interacción con NPCs
            if (input.keys.justPressed(Key.SPACE)) {
                checkNPCInteraction()
            }
        }
    }
    
    private fun setupCamera() {
        // Configurar cámara para seguir al jugador (opcional)
        addUpdater {
            // Centrar cámara en el jugador
            val targetX = -player.x + (width / 2)
            val targetY = -player.y + (height / 2)
            
            // Suavizar movimiento de cámara
            gameMap.x += (targetX - gameMap.x) * 0.1
            gameMap.y += (targetY - gameMap.y) * 0.1
        }
    }
    
    private fun checkNPCInteraction() {
        val playerBounds = Rectangle(player.x, player.y, 64, 64)
        
        for (npc in npcs) {
            val npcBounds = Rectangle(npc.position.x, npc.position.y, 64, 64)
            
            // Verificar si el jugador está cerca del NPC
            if (playerBounds.intersects(npcBounds)) {
                showDialog
