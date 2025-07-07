package com.tuempresa.pokemonlike.map

import korlibs.korge.view.*
import korlibs.korge.ldtk.*
import korlibs.korge.scene.*
import korlibs.math.geom.*
import korlibs.image.color.*

class MapManager {
    private var currentMapView: View? = null
    private var currentMapPath: String = ""
    private var currentLevelIndex: Int = 0
    private var ldtkMap: LdtkMap? = null
    
    // Cargar un mapa LDtk desde la ruta especificada
    suspend fun loadMap(path: String): View {
        try {
            // Carga el mapa LDtk desde los recursos
            ldtkMap = resourcesVfs[path].readLDtkMap()
            val level = ldtkMap!!.levels.first() // Carga el primer nivel
            val mapView = level.createView()
            
            currentMapView = mapView
            currentMapPath = path
            currentLevelIndex = 0
            
            return mapView
        } catch (e: Exception) {
            // Si no se puede cargar el mapa LDtk, crear un mapa básico de prueba
            return createFallbackMap()
        }
    }
    
    // Crear un mapa básico de prueba si no se puede cargar LDtk
    private fun createFallbackMap(): View {
        val container = Container()
        
        // Fondo verde como césped
        val background = solidRect(800, 600, Colors.GREEN)
        container.addChild(background)
        
        // Agregar algunos elementos visuales básicos
        val tree1 = solidRect(64, 64, Colors.BROWN).apply { position(200, 200) }
        val tree2 = solidRect(64, 64, Colors.BROWN).apply { position(400, 300) }
        val water = solidRect(128, 128, Colors.BLUE).apply { position(600, 150) }
        val house = solidRect(96, 96, Colors.RED).apply { position(100, 400) }
        
        container.addChild(tree1)
        container.addChild(tree2)
        container.addChild(water)
        container.addChild(house)
        
        currentMapView = container
        return container
    }
    
    // Obtener la vista del mapa actual
    fun getCurrentMapView(): View? = currentMapView
    
    // Obtener la ruta del mapa actual
    fun getCurrentMapPath(): String = currentMapPath
    
    // Obtener el índice del nivel actual
    fun getCurrentLevelIndex(): Int = currentLevelIndex
    
    // Cambiar de nivel si el mapa tiene varios niveles
    suspend fun changeLevel(levelIndex: Int): View? {
        val map = ldtkMap ?: return null
        
        if (levelIndex < 0 || levelIndex >= map.levels.size) {
            return null
        }
        
        // Remover el mapa actual de su padre si existe
        currentMapView?.removeFromParent()
        
        // Cargar el nuevo nivel
        val newLevel = map.levels[levelIndex]
        val newMapView = newLevel.createView()
        
        currentMapView = newMapView
        currentLevelIndex = levelIndex
        
        return newMapView
    }
    
    // Cambiar al siguiente nivel
    suspend fun nextLevel(): View? {
        return changeLevel(currentLevelIndex + 1)
    }
    
    // Cambiar al nivel anterior
    suspend fun previousLevel(): View? {
        return changeLevel(currentLevelIndex - 1)
    }
    
    // Obtener el número total de niveles en el mapa actual
    fun getTotalLevels(): Int {
        return ldtkMap?.levels?.size ?: 1
    }
    
    // Verificar si hay más niveles disponibles
    fun hasNextLevel(): Boolean {
        return currentLevelIndex < getTotalLevels() - 1
    }
    
    fun hasPreviousLevel(): Boolean {
        return currentLevelIndex > 0
    }
    
    // Obtener información del nivel actual
    fun getCurrentLevelInfo(): LevelInfo? {
        val map = ldtkMap ?: return null
        if (currentLevelIndex >= map.levels.size) return null
        
        val level = map.levels[currentLevelIndex]
        return LevelInfo(
            name = level.identifier,
            width = level.pxWid,
            height = level.pxHei,
            index = currentLevelIndex,
            totalLevels = map.levels.size
        )
    }
    
    // Verificar colisiones en una posición específica (para futuras implementaciones)
    fun checkCollision(x: Double, y: Double): Boolean {
        // Aquí puedes implementar lógica de colisiones basada en las capas del mapa LDtk
        // Por ahora retorna false (sin colisiones)
        return false
    }
    
    // Obtener entidades del mapa (NPCs, objetos, etc.)
    fun getMapEntities(): List<MapEntity> {
        val entities = mutableListOf<MapEntity>()
        
        ldtkMap?.levels?.get(currentLevelIndex)?.let { level ->
            // Aquí puedes extraer entidades específicas del nivel LDtk
            // Por ejemplo, posiciones de NPCs, objetos interactuables, etc.
        }
        
        return entities
    }
    
    // Limpiar recursos del mapa
    fun cleanup() {
        currentMapView?.removeFromParent()
        currentMapView = null
        ldtkMap = null
        currentMapPath = ""
        currentLevelIndex = 0
    }
}

// Clase de datos para información del nivel
data class LevelInfo(
    val name: String,
    val width: Int,
    val height: Int,
    val index: Int,
    val totalLevels: Int
)

// Clase para representar entidades del mapa
data class MapEntity(
    val id: String,
    val type: String,
    val position: Point,
    val properties: Map<String, Any> = emptyMap()
)
