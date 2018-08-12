package jp.ac.kagawanct.location;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SpeechRecognitionListener {
    // ログ出力用タグ名
    private static final String TAG = "MainActivity";

    // 位置情報取得処理用のパーミッションチェック処理で利用するリクエストコード
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1000;
    // 音声認識処理用のパーミッションチェック処理で利用するリクエストコード
    private static final int MY_PERMISSIONS_REQUEST_RECORD_AUDIO = 1001;

    // 位置情報取得の成功・失敗を識別するためのフラグ
    private static final int HANDLER_OPERATION_SUCCESS = 0;
    private static final int HANDLER_OPERATION_FAILED = 1;

    // 位置情報取得機能を提供するオブジェクト
    private FusedLocationProviderClient mFusedLocationClient;

    // 音声認識クラスのオブジェクト
    private MySpeechRecognizer mySpeechRecognizer;

    // UI への処理実行を管理するハンドラ
    private Handler mHandler;

    // 位置情報取得結果などを表示する TextView オブジェクト
    private TextView mTextLatitude;
    private TextView mTextLongitude;
    private TextView mTextErrorMessage;
    private TextView mTextSpeech;

    @SuppressLint("HandlerLeak") // Handler のメモリリーク警告を表示させないための設定
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 位置情報処理で利用するパーミッションが有効になっているかどうかをチェックする
        checkAccessLocationPermission(this);

        // TODO: フィールドの初期化と、位置情報取得時に呼び出されるハンドルメソッドの定義
    }

    /**
     * SpeechRecognitionListener の onResults メソッドの実装
     * @param results 音声認識結果を持つリスト
     */
    @Override
    public void onResults(List<String> results) {
        // TODO: 音声認識結果を解析して、位置情報の計測を開始する
    }

    /**
     * SpeechRecognitionListener の onError メソッドの実装
     * @param code エラーコード
     */
    @Override
    public void onError(int code) {
        mTextSpeech.setText("");
        mTextErrorMessage.setText(getString(R.string.voice_recognition_failed_with_error) + code);
    }

    // requestPermissions の結果を受け取る
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // 位置情報取得のパーミッション確認後、続けて、
                // 音声認識処理で利用するパーミッションが有効になっているかどうかをチェックする
                checkSpeechRecognitionPermission(this);
            }
        }
    }

    @SuppressLint("MissingPermission") // Permission 関連の警告を非表示にするための設定
    private void getLocation() {
        // TODO: 位置情報取得を実行し、その成功・失敗時の処理を定義する
    }

    // 位置情報取得処理で利用するパーミッションが有効になっているかどうかをチェックする
    private static void checkAccessLocationPermission(Activity activity) {
        if (ContextCompat.checkSelfPermission(activity,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(activity,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            }
        }
    }

    // 音声認識処理で利用するパーミッションが有効になっているかどうかをチェックする
    private static void checkSpeechRecognitionPermission(Activity activity) {
        if (ContextCompat.checkSelfPermission(activity,
                Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    Manifest.permission.RECORD_AUDIO)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(activity,
                        new String[]{Manifest.permission.RECORD_AUDIO},
                        MY_PERMISSIONS_REQUEST_RECORD_AUDIO);
            }
        }
    }
}
