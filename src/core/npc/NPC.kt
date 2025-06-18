package com.tuempresa.pokemonlike.npc

import korlibs.korge.view.*
import korlibs.math.geom.*
import korlibs.image.color.*
import korlibs.image.bitmap.*
import korlibs.korge.view.animation.*

// Clase principal para representar un NPC en el juego
class NPC(
    val sprite: SpriteAnimationView,
    val position: Point,
    val name: String,
    val dialogType: String,
    val message: String,
    val id: String = "",
    val canMove: Boolean = false
) {
    private var currentDirection = "down"
    private var isInteracting = false
    
    init {
        sprite.position(position.x, position.y)
    }

    // Método para interactuar con el NPC
    fun interact(): NPCInteraction {
        isInteracting = true
        return NPCInteraction(
            npcName = name,
            dialogType = dialogType,
            message = message,
            npcId = id
        )
    }
    
    // Obtener los límites del NPC para detección de colisiones
    fun getBounds(): Rectangle {
        return Rectangle(sprite.x, sprite.y, 64, 64) // Considerando el scale de 2.0
    }
    
    // Verificar si el jugador está cerca para interactuar
    fun isPlayerNearby(playerBounds: Rectangle): Boolean {
        val npcBounds = getBounds()
        val interactionDistance = 80.0 // Distancia de interacción
        
        return (npcBounds.distanceTo(playerBounds) <= interactionDistance)
    }
    
    // Mover el NPC (para NPCs que patrullan)
    fun moveToDirection(direction: String, speed: Double = 1.0) {
        if (!canMove) return
        
        when (direction) {
            "left" -> sprite.x -= speed
            "right" -> sprite.x += speed
            "up" -> sprite.y -= speed
            "down" -> sprite.y += speed
        }
        currentDirection = direction
    }
    
    // Hacer que el NPC mire hacia el jugador
    fun lookAtPlayer(playerPosition: Point) {
        val dx = playerPosition.x - sprite.x
        val dy = playerPosition.y - sprite.y
        
        currentDirection = when {
            kotlin.math.abs(dx) > kotlin.math.abs(dy) -> {
                if (dx > 0) "right" else "left"
            }
            else -> {
                if (dy > 0) "down" else "up"
            }
        }
    }
    
    // Obtener la posición actual del NPC
    fun getCurrentPosition(): Point = Point(sprite.x, sprite.y)
    
    // Verificar si el NPC está interactuando
    fun isCurrentlyInteracting(): Boolean = isInteracting
    
    // Finalizar la interacción
    fun endInteraction() {
        isInteracting = false
    }
}

// Clase para manejar la información de interacción con NPCs
data class NPCInteraction(
    val npcName: String,
    val dialogType: String,
    val message: String,
    val npcId: String,
    val hasMultipleDialogs: Boolean = false,
    val currentDialogIndex: Int = 0
)

// Función auxiliar para crear sprites de NPCs
fun createNPCSprite(color: RGBA): SpriteAnimationView {
    return sprite(Bitmap32(32, 32, color)) {
        scale = 2.0
    }
}

// Función para crear NPCs con sprites personalizados
suspend fun createNPCWithSprite(
    spritePath: String,
    position: Point,
    name: String,
    dialogType: String,
    message: String,
    id: String = "",
    canMove: Boolean = false
): NPC {
    val sprite = try {
        val spriteMap = resourcesVfs[spritePath].readBitmap()
        sprite(spriteMap) {
            scale = 2.0
        }
    } catch (e: Exception) {
        // Fallback a sprite de color si no se puede cargar la imagen
        val defaultColor = when (dialogType) {
            "profesor" -> Colors.BLUE
            "enfermera" -> Colors.PINK
            "vendedor" -> Colors.GREEN
            "entrenador" -> Colors.RED
            else -> Colors.ORANGE
        }
        createNPCSprite(defaultColor)
    }
    
    return NPC(sprite, position, name, dialogType, message, id, canMove)
}

// Factory para crear NPCs predefinidos
object NPCFactory {
    suspend fun createProfesor(position: Point): NPC {
        return createNPCWithSprite(
            spritePath = "npc/profesor.png",
            position = position,
            name = "Profesor Oak",
            dialogType = "profesor",
            message = "¡Hola, joven entrenador! ¿Estás listo para comenzar tu aventura Pokémon?",
            id = "profesor_oak"
        )
    }
    
    suspend fun createEnfermera(position: Point): NPC {
        return createNPCWithSprite(
            spritePath = "npc/enfermera.png",
            position = position,
            name = "Enfermera Joy",
            dialogType = "enfermera",
            message = "¡Bienvenido al Centro Pokémon! ¿Necesitas curar a tus Pokémon?",
            id = "enfermera_joy"
        )
    }
    
    suspend fun createVendedor(position: Point): NPC {
        return createNPCWithSprite(
            spritePath = "npc/vendedor.png",
            position = position,
            name = "Vendedor",
            dialogType = "vendedor",
            message = "¡Tengo los mejores artículos para entrenadores! ¿Qué necesitas?",
            id = "vendedor_tienda"
        )
    }
    
    suspend fun createEntrenador(position: Point, name: String = "Entrenador"): NPC {
        return createNPCWithSprite(
            spritePath = "npc/entrenador.png",
            position = position,
            name = name,
            dialogType = "entrenador",
            message = "¡Oye! ¡Tengamos una batalla Pokémon!",
            id = "entrenador_${name.lowercase()}",
            canMove = true
        )
    }
    
    suspend fun createAldeano(position: Point, message: String? = null): NPC {
        val defaultMessages = arrayOf(
            "He escuchado que hay Pokémon raros en el bosque cercano...",
            "Mi Pokémon favorito es Pikachu. ¡Son tan lindos!",
            "¿Sabías que algunos Pokémon solo aparecen de noche?",
            "El Gimnasio de esta ciudad es muy difícil. ¡Ten cuidado!",
            "Mi abuelo me contó historias sobre Pokémon legendarios..."
        )
        
        return createNPCWithSprite(
            spritePath = "npc/aldeano.png",
            position = position,
            name = "Aldeano",
            dialogType = "aldeano",
            message = message ?: defaultMessages.random(),
            id = "aldeano_${position.x.toInt()}_${position.y.toInt()}"
        )
    }
}
