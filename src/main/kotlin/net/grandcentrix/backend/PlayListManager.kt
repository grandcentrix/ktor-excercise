package net.grandcentrix.backend

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

class PlaylistManager {
    private val playlists = mutableListOf<Playlist>()
    var currentPlaylistIndex: Int = -1
    private val json = Json

    init {
        loadPlaylists()
    }

    fun createPlaylist(name: String) {
        // Check if the playlist with the given name already exists
        if (playlists.any { it.name == name }) {
            throw IllegalArgumentException("Playlist with name '$name' already exists.")
        }


        // Create a new playlist and add it to the list
        val newPlaylist = Playlist(name, mutableListOf())
        playlists.add(newPlaylist)


        playlists.forEach { println(it.name) }

        // Save the new playlist to file immediately
        savePlaylists()
    }





    fun getAllPlaylists(): List<Playlist> {
        return playlists
    }

    fun switchPlaylist(name: String) {
        val index = playlists.indexOfFirst { it.name == name }
        if (index != -1) {
            currentPlaylistIndex = index
        } else {
            throw IllegalArgumentException("Playlist with name '$name' not found.")
        }
    }

    fun renamePlaylist(oldName: String, newName: String) {
        val existingPlaylist = playlists.find { it.name == oldName }
        if (existingPlaylist != null) {
            existingPlaylist.name = newName
            // Umbenennen der Datei auf dem Dateisystem
            val oldFile = File("$oldName.json")
            val newFile = File("$newName.json")
            if (oldFile.exists()) {
                oldFile.renameTo(newFile)
            }
            savePlaylists()
        } else {
            throw IllegalArgumentException("Playlist with name '$oldName' not found.")
        }
    }





    fun deletePlaylist(playlistName: String) {
        val playlist = playlists.find { it.name == playlistName }
        if (playlist == null) {
            throw IllegalArgumentException("Playlist '$playlistName' not found.")
        } else {
            val playlistFile = File("$playlistName.json")
            if (!playlistFile.exists()) {
            } else {
                // Delete the playlist file from the file system
                playlistFile.delete()
                // Remove the playlist from the in-memory list
                playlists.remove(playlist)
                // Save the updated playlists immediately
                savePlaylists()
            }
        }
    }



    fun getCurrentPlaylist(): Playlist? {
        return if (currentPlaylistIndex != -1 && currentPlaylistIndex < playlists.size) {
            playlists[currentPlaylistIndex]
        } else {
            null
        }
    }

    private fun savePlaylistToFile(playlist: Playlist) {
        val file = File("${playlist.name}.json")
        val jsonContent = json.encodeToString(playlist)
        file.writeText(jsonContent)

    }


    fun savePlaylists() {
        playlists.forEach { savePlaylistToFile(it) }
    }

    fun loadPlaylists() {
        val playlistFiles = File(".").listFiles { file ->
            file.isFile && file.extension == "json"
        } ?: return

        val loadedPlaylists: MutableList<Playlist> = mutableListOf()

        for (file in playlistFiles) {
            try {
                if (!file.exists()) {
                } else { // Überprüfen, ob die Datei existiert
                    val jsonContent = file.readText()
                    val playlist = json.decodeFromString<Playlist>(jsonContent)
                    loadedPlaylists.add(playlist)
                }
            } catch (e: Exception) {
            }
        }
        playlists.clear()
        playlists.addAll(loadedPlaylists)
    }
}