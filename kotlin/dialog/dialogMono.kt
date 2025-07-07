package com.tuempresa.pokemonlike.dialog

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.appcompat.app.AlertDialog

class MonoDialog : DialogFragment() {

    private var npcType: String = ""
    private var dialogStep: Int = 0

    companion object {
        fun newInstance(npcType: String): MonoDialog {
            val dialog = MonoDialog()
            val args = Bundle()
            args.putString("npcType", npcType)
            dialog.arguments = args
            return dialog
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            npcType = it.getString("npcType", "")
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return when (npcType) {
            "mago_hombre" -> createDialog(magoHombreDialog)
            "maga_mujer" -> createDialog(magaMujerDialog)
            "mono_mago" -> createDialog(monoMagoDialog)
            else -> createDialog(defaultDialog)
        }
    }

    private fun createDialog(dialogLines: List<Pair<String, String>>): Dialog {
        val message = dialogLines.joinToString("\n\n") { "${it.first}: ${it.second}" }

        return AlertDialog.Builder(requireContext())
            .setTitle("Encuentro Mágico")
            .setMessage(message)
            .setPositiveButton("Continuar") { _, _ -> dismiss() }
            .create()
    }

    private val magoHombreDialog = listOf(
        "Mono" to "¡Uuh uuh! Oye, tú con la barba brillante… ¿por qué tu bastón lanza luces y el mío solo suena al romperse?",
        "Mago Hombre" to "Porque el mío es un canal de magia, no un palo de escoba robado del suelo.",
        "Mono" to "¿Y puedo tener uno así? Quiero hacer que los cocos se abran solos.",
        "Mago Hombre" to "Eso requiere años de estudio, concentración… y no trepar árboles mientras se aprende.",
        "Mono" to "Mmm… ¿y si estudio desde un árbol?",
        "Mago Hombre" to "Eres persistente. Quizás demasiado para alguien con bananas por meta."
    )

    private val magaMujerDialog = listOf(
        "Mono" to "¡Oye tú, la de la capa que huele a flores! ¿Tú haces hechizos de fruta?",
        "Maga Mujer" to "Solo si la fruta no me salta en la cara como tú acabas de hacer.",
        "Mono" to "Quiero hacer que las bananas vuelen y persigan a los ladrones.",
        "Maga Mujer" to "Interesante concepto. Pero necesitas más que imaginación. ¿Sabes qué es concentración?",
        "Mono" to "Sí, es cuando miro una banana sin parpadear hasta que se me va la baba.",
        "Maga Mujer" to "… Eso no es concentración, eso es hambre."
    )

    private val monoMagoDialog = listOf(
        "Mono" to "¡Woooah! ¡Eres como yo pero… brillas! ¿Eres un mono disfrazado de estrella?",
        "Mono Mago" to "Soy un mono que aprendió magia. Y sí, brillar es parte del estilo.",
        "Mono" to "¿Entonces sí puedo ser mago? ¡Dime cómo! ¿Necesito comer relámpagos?",
        "Mono Mago" to "Necesitas paciencia, mente despierta… y no comerte los libros mágicos.",
        "Mono" to "¿Y si solo me como las esquinas? Tienen menos letras.",
        "Mono Mago" to "Tienes futuro… peligroso, pero futuro."
    )

        "Mono" to "¡Hola! ¿Tú también sabes magia?",
        "Desconocido" to "Solo un poco, amiguito."
    private val defaultDialog = listOf(
    )
}
