package jp.ac.kagawanct.location;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;

import java.util.List;

/**
 * 音声認識を担当するクラス
 */

public class MySpeechRecognizer {
    // ログ出力用のタグ
    private static final String TAG = "MySpeechRecognizer";
    // SpeechRecognizer オブジェクト
    private SpeechRecognizer recognizer;
    // Intent オブジェクト
    private Intent intent;
    // 音声認識結果を受け取って処理するためのメソッドを提供するインタフェース
    private SpeechRecognitionListener listener;

    /**
     * コンストラクタ
     * @param context Context オブジェクト
     * @param listener SpeechRecognitionListener を実装したクラスのオブジェクト
     */
    public MySpeechRecognizer(Context context, SpeechRecognitionListener listener) {
        // SpeechRecognizer オブジェクトを初期化する
        init(context);
        // SpeechRecognitionListener を実装したクラス（＝TreasureHuntActivity）のオブジェクトを保存する
        this.listener = listener;
    }

    /**
     * 音声認識を開始する
     */
    public void start() {
        recognizer.cancel();
        recognizer.startListening(intent);
    }

    // SpeechRecognizer オブジェクトを初期化する
    private void init(Context context) {
        intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, context.getPackageName());

        recognizer = SpeechRecognizer.createSpeechRecognizer(context);

        // 音声認識結果を受け取った祭の処理を定義する
        recognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle bundle) {
                Log.d(TAG, "onReadyForSpeech");
            }

            @Override
            public void onBeginningOfSpeech() {
                Log.d(TAG, "onBeginningOfSpeech");
            }

            @Override
            public void onRmsChanged(float v) {
                Log.d(TAG, "onRmsChanged");
            }

            @Override
            public void onBufferReceived(byte[] bytes) {
                Log.v(TAG, "onBufferReceived");
            }

            @Override
            public void onEndOfSpeech() {
                Log.d(TAG, "onEndOfSpeech");
            }

            @Override
            public void onError(int error) {
                Log.w(TAG, "onError " + error);
                listener.onError(error);
            }

            @Override
            public void onResults(Bundle bundle) {
                Log.v(TAG, "onResults");

                // 音声認識結果をリストで受け取る
                List<String> results = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                // SpeechRecognitionListener を実装したクラスに、結果を引き渡す
                listener.onResults(results);
            }

            @Override
            public void onPartialResults(Bundle bundle) {
                Log.d(TAG, "onPartialResults");
            }

            @Override
            public void onEvent(int i, Bundle bundle) {
                Log.d(TAG, "onEvent");
            }
        });
    }
}
