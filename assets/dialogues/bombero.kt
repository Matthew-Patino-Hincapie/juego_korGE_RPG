private fun createFirefighterDialog(): Dialog {
    val messages = arrayOf(
        "¡Mágico o no, si explota, me toca apagarlo!",
        "Un duende me mordió la nalga una vez... desde entonces, uso licra antifuego."
    )
    val randomMessage = messages.random()

    return AlertDialog.Builder(requireContext())
        .setTitle("Ramiro, el Bombero")
        .setMessage(randomMessage)
        .setPositiveButton("Qué valiente") { _, _ ->
            dismiss()
        }
        .setNegativeButton("Eso suena doloroso") { _, _ ->
            dismiss()
        }
        .create()
}
