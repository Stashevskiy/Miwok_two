package com.stashevskiy.miwok;



public class Word {

    @Override
    public String toString() {
        return "Word{" +
                "defaultTranslation='" + defaultTranslation + '\'' +
                ", miwokTranslation='" + miwokTranslation + '\'' +
                ", imageResourceId=" + imageResourceId +
                ", audioResourceId=" + audioResourceId +
                '}';
    }

    // Перевод для слова по умолчанию
    private String defaultTranslation;

    // Miwok перевод слова
    private String miwokTranslation;

    // Идентификатор ресурса изображения для слова
    private int imageResourceId = NO_IMAGE_PROVIDED;

    // Идентификатор ресурса аудио для слова
    private int audioResourceId;

    // Постоянное значение, которое не представляет изображения
    private static final int NO_IMAGE_PROVIDED = -1;

    // Конструктор для создания нового объекта Word (с ID для изображения)
    public Word(String defaultTranslation, String miwokTranslation, int imageResourceId, int audioResourceId) {
        this.defaultTranslation = defaultTranslation;
        this.miwokTranslation = miwokTranslation;
        this.imageResourceId = imageResourceId;
        this.audioResourceId = audioResourceId;
    }

    // Конструктор для создания нового объекта Word (без ID для изображения)
    public Word(String defaultTranslation, String miwokTranslation, int audioResourceId) {
        this.defaultTranslation = defaultTranslation;
        this.miwokTranslation = miwokTranslation;
        this.audioResourceId = audioResourceId;
    }

    // Метод для получения перевода слова по умолчанию
    public String getDefaultTranslation() {
        return defaultTranslation;
    }

    // Метод для получения Miwok перевода слова
    public String getMiwokTranslation() {
        return miwokTranslation;
    }

    // Метод для получения идентификатора ресурса аудио для слова
    public int getAudioResourceId() {
        return audioResourceId;
    }

    // Метод для получения идентификатора ресурса изображения для слова
    public int getImageResourceId() {
        return imageResourceId;
    }

    // Метод, который отображает есть ли изображение для этого слова
    public boolean hasImage(){
        return imageResourceId != NO_IMAGE_PROVIDED;
    }
}
