package jp.ac.yuge.location;

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
    //
    private static final String TAG = "MainActivity";

    // 位置情報取得処理用のパーミッションチェック処理で利用するリクエストコード
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1000;
    // 音声認識処理用のパーミッションチェック処理で利用するリクエストコード
    private static final int MY_PERMISSIONS_REQUEST_RECORD_AUDIO = 1001;

    //
    private static final int HANDLER_OPERATION_SUCCESS = 0;
    private static final int HANDLER_OPERATION_FAILED = 1;

    //
    private FusedLocationProviderClient mFusedLocationClient;

    // 音声認識クラスのオブジェクト
    private MySpeechRecognizer mySpeechRecognizer;

    // UI への処理実行を管理するハンドラ
    private Handler mHandler;

    private TextView mTextLatitude;
    private TextView mTextLongitude;
    private TextView mTextErrorMessage;
    private TextView mTextSpeech;

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 位置情報処理で利用するパーミッションが有効になっているかどうかをチェックする
        checkAccessLocationPermission(this);
        //
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // 音声認識処理用クラスのインスタンス生成
        mySpeechRecognizer = new MySpeechRecognizer(this, this);

        //
        ImageButton buttonSpeech = (ImageButton) findViewById(R.id.button_speech);
        buttonSpeech.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Speech button did click.");

                //
                mTextLatitude.setText("");
                mTextLongitude.setText("");
                mTextErrorMessage.setText("");
                mTextSpeech.setText("・・・");

                //
                mySpeechRecognizer.start();
                //getLocation();
            }
        });

        //
        mTextLatitude = (TextView)findViewById(R.id.latitude);
        mTextLatitude.setText("");
        mTextLongitude = (TextView)findViewById(R.id.longitude);
        mTextLongitude.setText("");
        mTextErrorMessage = (TextView)findViewById(R.id.error_message);
        mTextErrorMessage.setText("");
        mTextSpeech = (TextView)findViewById(R.id.speech);
        mTextSpeech.setText("");

        //
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == HANDLER_OPERATION_SUCCESS) {
                    Location location = (Location) msg.obj;

                    mTextLatitude.setText(getString(R.string.latitude_title) + location.getLatitude());
                    mTextLongitude.setText(getString(R.string.longitude_title) + location.getLongitude());

                } else {
                    String message = (String)msg.obj;
                    mTextErrorMessage.setText(message);
                }
            }
        };
    }

    /**
     * SpeechRecognitionListener の onResults メソッドの実装
     * @param results 音声認識結果を持つリスト
     */
    @Override
    public void onResults(List<String> results) {
        // 音声認識結果を識別するための単語のリスト
        final List<String> patterns = Arrays.asList("どこ", "ドコ");

        // 音声認識結果が空の場合は何もしない
        if (results.isEmpty()) {
            mTextErrorMessage.setText(R.string.voice_recognition_failed);
            return;
        }
        // 音声認識結果のリストの先頭の要素を取得
        String result = results.get(0);
        if (result == null) {
            mTextErrorMessage.setText(R.string.voice_recognition_failed);
            return;
        }
        Log.d(TAG, "result = " + result);

        // 音声認識結果に「どこ」などが含まれているかどうかをチェック
        for (String pattern : patterns) {
            if (result.contains(pattern)) {
                mTextSpeech.setText(result);

                //
                getLocation();
                return;
            }
        }

        // いずれにもマッチしなかった場合
        mTextSpeech.setText(result);
        mTextErrorMessage.setText(R.string.voice_command_not_found);
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
                // 位置情報取得のパーミッション確認後、
                // 音声認識処理で利用するパーミッションが有効になっているかどうかをチェックする
                checkSpeechRecognitionPermission(this);
            }
        }
    }

    @SuppressLint("MissingPermission")
    private void getLocation() {
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        Log.d(TAG, "FusedLocationClient Success.");
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            //
                            mHandler.sendMessage(Message.obtain(mHandler, HANDLER_OPERATION_SUCCESS, location));

                        } else {
                            //
                            mHandler.sendMessage(Message.obtain(mHandler, HANDLER_OPERATION_FAILED, getString(R.string.get_location_failed)));
                        }
                    }
                })
                .addOnCanceledListener(this, new OnCanceledListener() {
                    @Override
                    public void onCanceled() {
                        Log.d(TAG, "FusedLocationClient Canceled.");
                        //
                        mHandler.sendMessage(Message.obtain(mHandler, HANDLER_OPERATION_FAILED, getString(R.string.get_location_canceled)));
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "FusedLocationClient Failure.");
                        //
                        mHandler.sendMessage(Message.obtain(mHandler, HANDLER_OPERATION_FAILED,
                                getString(R.string.get_location_failed_with_error) + e.getLocalizedMessage()));
                    }
                });
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
