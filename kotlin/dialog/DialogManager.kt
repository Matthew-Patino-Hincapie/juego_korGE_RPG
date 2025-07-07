package com.tuempresa.pokemonlike.dialog

import korlibs.korge.view.*
import korlibs.korge.input.*
import korlibs.korge.scene.*
import korlibs.korge.tween.*
import korlibs.image.color.*
import korlibs.korge.ui.*
import korlibs.korge.view.text.*
import korlibs.time.*
import korlibs.math.geom.*
import com.tuempresa.pokemonlike.npc.NPCInteraction

class DialogManager(private val container: Container) {
    private var dialogBox: SolidRect? = null
    private var dialogText: Text? = null
    private var continueText: Text? = null
    private var nameText: Text? = null
    private var isDialogActive = false
    private var onDialogEnd: (() -> Unit)? = null
    private var currentDialog: List<String> = emptyList()
    private var currentDialogIndex = 0
    
    // Mostrar un diálogo simple
    fun showDialog(message: String, onEnd: (() -> Unit)? = null) {
        if (isDialogActive) return
        
        currentDialog = listOf(message)
        currentDialogIndex = 0
        onDialogEnd = onEnd
        displayCurrentDialog()
    }
    
    // Mostrar diálogo de NPC con información completa
    fun showNPCDialog(interaction: NPCInteraction, onEnd: (() -> Unit)? = null) {
        if (isDialogActive) return
        
        val messages = when (interaction.dialogType) {
            "profesor" -> getProfessorDialogs()
            "enfermera" -> getNurseDialogs()
            "vendedor" -> getShopDialogs()
            "entrenador" -> getTrainerDialogs()
            "aldeano" -> getVillagerDialogs()
            else -> listOf(interaction.message)
        }
        
        currentDialog = messages
        currentDialogIndex = 0
        onDialogEnd = onEnd
        displayCurrentDialog(interaction.npcName)
    }
    
    // Mostrar diálogo con múltiples mensajes
    fun showMultiDialog(messages: List<String>, npcName: String = "", onEnd: (() -> Unit)? = null) {
        if (isDialogActive) return
        
        currentDialog = messages
        currentDialogIndex = 0
        onDialogEnd = onEnd
        displayCurrentDialog(npcName)
    }
    
    private fun displayCurrentDialog(npcName: String = "") {
        if (currentDialogIndex >= currentDialog.size) {
            endDialog()
            return
        }
        
        isDialogActive = true
        
        // Crear caja de diálogo
        dialogBox = container.solidRect(container.width - 40, 120, Colors.WHITE) {
            position(20, container.height - 140)
            alpha = 0.9
            stroke = Stroke(color = Colors.BLACK, thickness = 2.0)
        }
        
        // Mostrar nombre del NPC si se proporciona
        if (npcName.isNotEmpty()) {
            nameText = container.text(npcName) {
                position(30, container.height - 155)
                textSize = 14.0
                color = Colors.WHITE
                backgroundColor = Colors.BLUE
                padding = Margin(4.0)
            }
        }
        
        // Texto del diálogo
        dialogText = container.text(currentDialog[currentDialogIndex]) {
            position(40, container.height - 120)
            textSize = 16.0
            color = Colors.BLACK
            maxWidth = container.width - 80
            wordWrap = true
        }
        
        // Indicador de continuación
        val continueMessage = if (currentDialogIndex < currentDialog.size - 1) {
            "Presiona ENTER para continuar"
        } else {
            "Presiona ENTER para cerrar"
        }
        
        continueText = container.text(continueMessage) {
            position(40, container.height - 60)
            textSize = 12.0
            color = Colors.GRAY
        }
        
        // Configurar controles
        setupDialogControls()
    }
    
    private fun setupDialogControls() {
        container.keys {
            down(Key.ENTER) {
                nextDialog()
            }
            down(Key.SPACE) {
                nextDialog()
            }
            down(Key.ESCAPE) {
                endDialog()
            }
        }
    }
    
    private fun nextDialog() {
        // Limpiar diálogo actual
        clearCurrentDialog()
        
        // Avanzar al siguiente mensaje
        currentDialogIndex++
        
        if (currentDialogIndex < currentDialog.size) {
            // Mostrar siguiente mensaje
            displayCurrentDialog(nameText?.text ?: "")
        } else {
            // Terminar diálogo
            endDialog()
        }
    }
    
    private fun clearCurrentDialog() {
        dialogBox?.removeFromParent()
        dialogText?.removeFromParent()
        continueText?.removeFromParent()
        nameText?.removeFromParent()
        
        dialogBox = null
        dialogText = null
        continueText = null
        nameText = null
    }
    
    private fun endDialog() {
        clearCurrentDialog()
        isDialogActive = false
        currentDialog = emptyList()
        currentDialogIndex = 0
        onDialogEnd?.invoke()
    }
    
    // Verificar si hay un diálogo activo
    fun isActive(): Boolean = isDialogActive
    
    // Forzar el cierre del diálogo
    fun forceClose() {
        if (isDialogActive) {
            endDialog()
        }
    }
    
    // Diálogos predefinidos para diferentes tipos de NPCs
    private fun getProfessorDialogs(): List<String> {
        return listOf(
            "¡Hola, joven entrenador! Bienvenido al mundo de los Pokémon.",
            "Mi nombre es Profesor Oak, y soy investigador de estas criaturas.",
            "¿Estás listo para comenzar tu aventura y convertirte en un Maestro Pokémon?"
        )
    }
    
    private fun getNurseDialogs(): List<String> {
        return listOf(
            "¡Bienvenido al Centro Pokémon!",
            "Aquí curamos a tus Pokémon para que estén siempre en perfectas condiciones.",
            "¿Te gustaría que cure a tus Pokémon? ¡Estarán como nuevos!"
        )
    }
    
    private fun getShopDialogs(): List<String> {
        return listOf(
            "¡Bienvenido a la Tienda Pokémon!",
            "Tenemos todo lo que necesitas para tu aventura:",
            "Pokéballs, Pociones, Antídotos y mucho más.",
            "¿Qué te gustaría comprar hoy?"
        )
    }
    
    private fun getTrainerDialogs(): List<String> {
        return listOf(
            "¡Oye, tú! ¡Veo que eres un entrenador Pokémon!",
            "¡No hay nada mejor que una buena batalla para entrenar!",
            "¿Qué dices? ¿Aceptas mi desafío?"
        )
    }
    private fun getMedicoDialogs(): List<String> {
        return listOf(
            "Ah, por fin alguien más. Apenas estamos aguantando aquí… Las heridas no paran de llegar y los suministros escasean. Si no fuera por un poco de ingenio y suerte, ya habríamos perdido a la mitad del grupo.",
            "He hecho lo que he podido con vendas improvisadas y analgésicos caducados. Pero esto no puede durar mucho más. Si ves alcohol, vendas limpias, o incluso una aguja decente, tráemelas. Lo que sea.",
            "Y si te cruzas con algún herido en el camino… no los dejes. Tráelos aquí. No prometo milagros, pero al menos les daré una oportunidad."
        )
    }
    private fun getVillagerDialogs(): List<String> {
        val randomDialogs = arrayOf(
            listOf("He escuchado rumores sobre Pokémon raros en el bosque.", "Dicen que solo aparecen durante la noche..."),
            listOf("Mi Pokémon favorito es Pikachu.", "¡Son tan lindos y poderosos!"),
            listOf("¿Sabías que algunos Pokémon evolucionan?", "¡Es increíble ver cómo cambian!"),
            listOf("El Gimnasio de esta ciudad es muy difícil.", "¡Asegúrate de entrenar bien antes de ir!"),
            listOf("Mi abuelo me contó sobre Pokémon legendarios.", "Dicen que tienen poderes increíbles...")
        )
        
        return randomDialogs.random().toList()
    }
}

// Clase auxiliar para configurar diálogos personalizados
data class DialogConfig(
    val backgroundColor: RGBA = Colors.WHITE,
    val textColor: RGBA = Colors.BLACK,
    val borderColor: RGBA = Colors.BLACK,
    val textSize: Double = 16.0,
    val padding: Double = 20.0,
    val animationDuration: TimeSpan = 0.3.seconds
)
