package fr.sercurio.soulseek.client.shared

import kotlin.properties.Delegates

class ResponseCallback<T> {
    private var response: T? by
        Delegates.observable(null) { _, _, newValue ->
            onUpdateListeners.forEach { newValue?.let(it) }
        }

    fun update(message: T) {
        response = message
    }

    private val onUpdateListeners = mutableListOf<(T) -> Unit>()

    fun subscribe(listener: (T) -> Unit) {
        onUpdateListeners.add(listener)
    }
}