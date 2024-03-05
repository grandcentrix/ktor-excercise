package net.grandcentrix.backend

import io.ktor.http.*

interface PlayListInterface {
    fun createPlaylist(name: String)
    fun getAllPlaylists(): List<Playlist>
    fun switchPlaylist(name: String)
    fun renamePlaylist(oldName: String, newName: String)
    fun deletePlaylist(playlistName: String)
    fun getCurrentPlaylist(): Playlist?

    fun savePlaylistToFile(playlist: Playlist)
    fun savePlaylists()

    fun loadPlaylists()
}
