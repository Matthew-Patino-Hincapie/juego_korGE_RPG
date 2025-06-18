private fun createVictimDialog(): Dialog {
    val messages = arrayOf(
        "¿Yo? ¿Dramática? ¡Hermano, me cayó un rayo, me poseyó un espíritu y me meó un mono!",
        "Tenía cita con mi ex... y terminé poseída por un alma en pena que todavía lo ama."
    )
    val randomMessage = messages.random()

    return AlertDialog.Builder(requireContext())
        .setTitle("Lucía, la Víctima")
        .setMessage(randomMessage)
        .setPositiveButton("Eso suena paranormal") { _, _ ->
            dismiss()
        }
        .setNegativeButton("¿Estás bien ahora?") { _, _ ->
            dismiss()
        }
        .create()
}
