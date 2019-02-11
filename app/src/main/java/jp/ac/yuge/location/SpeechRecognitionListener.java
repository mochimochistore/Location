package jp.ac.yuge.location;

import java.util.List;

/**
 * 音声認識結果を受け取って処理するためのメソッドを提供するインタフェース
 */

public interface SpeechRecognitionListener {
    /**
     * 音声認識結果を受け取って、任意の処理を実行する
     * @param results 音声認識結果を持つリスト
     */
    void onResults(List<String> results);

    /**
     * 音声認識処理のエラーを通知する
     * @param code エラーコード
     */
    void onError(int code);
}
