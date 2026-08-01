package com.cho1022.askme.voice.infrastructure;

public interface SpeechToTextClient {
    String transcribe(byte[] audio, String mimeType);
}
