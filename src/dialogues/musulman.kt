private fun createMuslimDialog(): Dialog {
    val messages = arrayOf(
        "¡En el nombre de Alá! ¡Paren de invocar demonios mientras preparo el arroz!",
        "Anoche vi a un djinn atrapado en una botella de aguardiente. ¡No era Halal!"
    )
    val randomMessage = messages.random()

    return AlertDialog.Builder(requireContext())
        .setTitle("Ali, el Musulmán")
        .setMessage(randomMessage)
        .setPositiveButton("Lo siento, hermano") { _, _ ->
            dismiss()
        }
        .setNegativeButton("¿Djinn dices?") { _, _ ->
            dismiss()
        }
        .create()
}
