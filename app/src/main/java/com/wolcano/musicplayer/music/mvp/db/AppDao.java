package com.wolcano.musicplayer.music.mvp.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import com.wolcano.musicplayer.music.mvp.models.Song;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;
import org.greenrobot.greendao.internal.DaoConfig;


// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * DAO for table "AppWolcanoTable".
*/
public class AppDao extends AbstractDao<Song, Long> {

    public static final String TABLENAME = "AppWolcanoTable";

    /**
     * Properties of entity Song.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "id");
        public final static Property Type = new Property(1, int.class, "type", false, "type");
        public final static Property SongId = new Property(2, long.class, "song_id", false, "song_id");
        public final static Property Title = new Property(3, String.class, "title", false, "title");
        public final static Property Artist = new Property(4, String.class, "artist", false, "artist");
        public final static Property Album = new Property(5, String.class, "album", false, "album");
        public final static Property AlbumId = new Property(6, long.class, "album_id", false, "album_id");
        public final static Property CoverPath = new Property(7, String.class, "cover_path", false, "cover_path");
        public final static Property Duration = new Property(8, long.class, "duration", false, "duration");
        public final static Property Path = new Property(9, String.class, "path", false, "path");
        public final static Property FileName = new Property(10, String.class, "file_name", false, "file_name");
        public final static Property FileSize = new Property(11, long.class, "file_size", false, "file_size");
    }


    public AppDao(DaoConfig config, GreenDaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"AppWolcanoTable\" (" + //
                "\"id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "\"type\" INTEGER NOT NULL ," + // 1: type
                "\"song_id\" INTEGER NOT NULL ," + // 2: songId
                "\"title\" TEXT," + // 3: title
                "\"artist\" TEXT," + // 4: artist
                "\"album\" TEXT," + // 5: album
                "\"album_id\" INTEGER NOT NULL ," + // 6: albumId
                "\"cover_path\" TEXT," + // 7: coverPath
                "\"duration\" INTEGER NOT NULL ," + // 8: duration
                "\"path\" TEXT NOT NULL ," + // 9: path
                "\"file_name\" TEXT," + // 10: fileName
                "\"file_size\" INTEGER NOT NULL );"); // 11: fileSize
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"AppWolcanoTable\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, Song entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindLong(2, entity.getType());
        stmt.bindLong(3, entity.getSongId());
 
        String title = entity.getTitle();
        if (title != null) {
            stmt.bindString(4, title);
        }
 
        String artist = entity.getArtist();
        if (artist != null) {
            stmt.bindString(5, artist);
        }
 
        String album = entity.getAlbum();
        if (album != null) {
            stmt.bindString(6, album);
        }
        stmt.bindLong(7, entity.getAlbumId());
 
        String coverPath = entity.getCovPath();
        if (coverPath != null) {
            stmt.bindString(8, coverPath);
        }
        stmt.bindLong(9, entity.getDuration());
        stmt.bindString(10, entity.getPath());
 
        String fileName = entity.getDosName();
        if (fileName != null) {
            stmt.bindString(11, fileName);
        }
        stmt.bindLong(12, entity.getDosSize());
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, Song entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindLong(2, entity.getType());
        stmt.bindLong(3, entity.getSongId());
 
        String title = entity.getTitle();
        if (title != null) {
            stmt.bindString(4, title);
        }
 
        String artist = entity.getArtist();
        if (artist != null) {
            stmt.bindString(5, artist);
        }
 
        String album = entity.getAlbum();
        if (album != null) {
            stmt.bindString(6, album);
        }
        stmt.bindLong(7, entity.getAlbumId());
 
        String coverPath = entity.getCovPath();
        if (coverPath != null) {
            stmt.bindString(8, coverPath);
        }
        stmt.bindLong(9, entity.getDuration());
        stmt.bindString(10, entity.getPath());
 
        String fileName = entity.getDosName();
        if (fileName != null) {
            stmt.bindString(11, fileName);
        }
        stmt.bindLong(12, entity.getDosSize());
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public Song readEntity(Cursor cursor, int offset) {
        Song entity = new Song( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.getInt(offset + 1), // type
            cursor.getLong(offset + 2), // songId
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // title
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // artist
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // album
            cursor.getLong(offset + 6), // albumId
            cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7), // coverPath
            cursor.getLong(offset + 8), // duration
            cursor.getString(offset + 9), // path
            cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10), // fileName
            cursor.getLong(offset + 11) // fileSize
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, Song entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setType(cursor.getInt(offset + 1));
        entity.setSongId(cursor.getLong(offset + 2));
        entity.setTitle(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setArtist(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setAlbum(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setAlbumId(cursor.getLong(offset + 6));
        entity.setCovPath(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
        entity.setDuration(cursor.getLong(offset + 8));
        entity.setPath(cursor.getString(offset + 9));
        entity.setDosName(cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10));
        entity.setDosSize(cursor.getLong(offset + 11));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(Song entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(Song entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(Song entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
