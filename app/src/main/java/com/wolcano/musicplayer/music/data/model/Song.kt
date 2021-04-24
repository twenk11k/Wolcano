package com.wolcano.musicplayer.music.data.model

import android.os.Parcelable
import android.text.TextUtils
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "song")
@Parcelize
class Song(
    @ColumnInfo(name = "song_id") var songId: Long = 0,
    @ColumnInfo(name = "title") var title: String = "",
    @ColumnInfo(name = "artist") var artist: String = "",
    @ColumnInfo(name = "cover_path") val covPath: String = "",
    @ColumnInfo(name = "type") var type: Int = 0,
    @ColumnInfo(name = "duration") var duration: Long = 0,
    @ColumnInfo(name = "path") var path: String = "",
    @ColumnInfo(name = "fileName") var dosName: String? = null,
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") val id: Long? = null,
    @ColumnInfo(name = "fileSize") var dosSize: Long = 0,
    @ColumnInfo(name = "album") var album: String = "",
    @ColumnInfo(name = "albumId") var albumId: Long = 0
) : Parcelable {

    interface Tip {
        companion object {
            const val MODEL0 = 0
            const val MODEL1 = 1
        }
    }

    override fun equals(o: Any?): Boolean {
        if (o !is Song) {
            return false
        }
        if (o.songId > 0 && o.songId == songId) {
            return true
        }
        return (TextUtils.equals(o.title, title)
                && TextUtils.equals(o.artist, artist)
                && TextUtils.equals(o.album, album)
                && o.duration == duration)
    }

    override fun hashCode(): Int {
        var result = songId.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + artist.hashCode()
        result = 31 * result + covPath.hashCode()
        result = 31 * result + type
        result = 31 * result + duration.hashCode()
        result = 31 * result + path.hashCode()
        result = 31 * result + (dosName?.hashCode() ?: 0)
        result = 31 * result + (id?.hashCode() ?: 0)
        result = 31 * result + dosSize.hashCode()
        result = 31 * result + album.hashCode()
        result = 31 * result + albumId.hashCode()
        return result
    }

}