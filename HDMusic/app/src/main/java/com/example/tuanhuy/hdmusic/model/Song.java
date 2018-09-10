package com.example.tuanhuy.hdmusic.model;

public class Song {
    private String name;
    private String path;
    private String album;
    private String artist;
    private int duration;

    public Song() {
    }

    public Song(String name, String path, String album, String artist, int duration) {
        this.name = name;
        this.path = path;
        this.album = album;
        this.artist = artist;
        this.duration = duration;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}
