package net.grandcentrix.backend

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

class PlaylistManager(
    private val playlists: MutableList<Playlist> = mutableListOf(),
    private var currentPlaylistIndex: Int = -1,
    private val json: Json = Json
) {
    init {
        loadPlaylists()
    }

    fun createPlaylist(name: String) {
        // Check if the playlist with the given name already exists
        if (playlists.any { it.name == name }) {
            throw IllegalArgumentException("Playlist with name '$name' already exists.")
        }

        // Print the name of the playlist we are attempting to create
        println("Creating playlist: $name")

        // Create a new playlist and add it to the list
        val newPlaylist = Playlist(name, mutableListOf())
        playlists.add(newPlaylist)

        // Print the playlists after adding the new one
        println("Playlists after adding new playlist:")
        playlists.forEach { println(it.name) }

        // Save the new playlist to file immediately
        println("Saving new playlist to file...")
        savePlaylists()

        // Print a message indicating successful creation
        println("Playlist $name created.")
    }

    fun getAllPlaylists(): List<Playlist> {
        return playlists
    }

    fun switchPlaylist(name: String) {
        val index = playlists.indexOfFirst { it.name == name }
        if (index != -1) {
            currentPlaylistIndex = index
            println("Switch to: $name")
        } else {
            throw IllegalArgumentException("Playlist with name '$name' not found.")
        }
    }

    fun renamePlaylist(oldName: String, newName: String) {
        val playlist = playlists.find { it.name == oldName } ?: throw IllegalArgumentException("Playlist with name '$oldName' not found.")
        playlist.name = newName
        savePlaylists()
    }

    fun deletePlaylist(playlistName: String) {
        val playlist = playlists.find { it.name == playlistName }
        if (playlist != null) {
            val playlistFile = File("$playlistName.json")
            if (playlistFile.exists()) {
                // Delete the playlist file from the file system
                playlistFile.delete() // Hier wird die Datei gelöscht
                // Remove the playlist from the in-memory list
                playlists.remove(playlist)
                // Save the updated playlists immediately
                savePlaylists()
                println("Playlist '$playlistName' deleted successfully.")
            } else {
                println("Playlist file '$playlistName.json' not found.")
            }
        } else {
            throw IllegalArgumentException("Playlist '$playlistName' not found.")
        }
    }



    fun getCurrentPlaylist(): Playlist? {
        return if (currentPlaylistIndex == -1 || currentPlaylistIndex >= playlists.size) {
            null
        } else {
            playlists[currentPlaylistIndex]
        }
    }

    private fun savePlaylistToFile(playlist: Playlist) {
        val file = File("${playlist.name}.json")
        val jsonContent = json.encodeToString(playlist)
        file.writeText(jsonContent)
        println("Playlist '${playlist.name}' saved successfully to ${file.name}")
    }


    fun savePlaylists() {
        playlists.forEach { savePlaylistToFile(it) }
    }

    fun loadPlaylists() {
        val playlistFiles = File(".").listFiles { file ->
            file.isFile && file.extension == "json"
        } ?: return

        val loadedPlaylists: MutableList<Playlist> = mutableListOf()
        println("Loading playlists:")
        for (file in playlistFiles) {
            try {
                if (file.exists()) { // Überprüfen, ob die Datei existiert
                    val jsonContent = file.readText()
                    val playlist = json.decodeFromString<Playlist>(jsonContent)
                    loadedPlaylists.add(playlist)
                    println("Playlist '${playlist.name}' loaded successfully from ${file.name}")
                } else {
                    println("Playlist file '${file.name}' does not exist.")
                }
            } catch (e: Exception) {
                println("Failed to load playlist from ${file.name}: ${e.message}")
            }
        }
        playlists.clear()
        playlists.addAll(loadedPlaylists)
    }
}
