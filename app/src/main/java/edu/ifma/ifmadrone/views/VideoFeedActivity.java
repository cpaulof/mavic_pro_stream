package edu.ifma.ifmadrone.views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.TextureView;
import android.view.View;
import android.widget.TextView;
import java.io.ByteArrayOutputStream;
import dji.midware.usb.P3.UsbAccessoryService;
import dji.sdk.camera.VideoFeeder;
import dji.sdk.codec.DJICodecManager;
import dji.sdk.sdkmanager.DJISDKManager;
import edu.ifma.ifmadrone.R;


public class VideoFeedActivity extends AppCompatActivity {
    private VideoFeeder.VideoFeed videoFeed;
    private TextureView videoFeedView;
    DJICodecManager codecManager;
    VideoFeeder.VideoDataListener videoDataListener;
    ByteArrayOutputStream outStream;
    private TextView hostText;
    private Boolean isAPISetup = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_feed);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        videoFeedView = findViewById(R.id.textureView);
        // request = new JSONRequest("http://127.0.0.1:5000/inference");
        hostText = findViewById(R.id.hostText);

        //init();
    }

    public void setHostBtn(View view){
        String host = hostText.getText().toString();
        isAPISetup = false;
    }

    public void startLiveStream(View view){
        String host = hostText.getText().toString();
        DJISDKManager.getInstance().getLiveStreamManager().setLiveUrl(host);
        DJISDKManager.getInstance().getLiveStreamManager().startStream();
    }

    public void stopLiveStream(View view){
        DJISDKManager.getInstance().getLiveStreamManager().stopStream();
    }

    private void init(){
        Context context = this;
        videoFeedView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener(){

            @Override
            public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surfaceTexture, int i, int i1) {
                if(codecManager== null){
                    codecManager = new DJICodecManager(
                            context,
                            surfaceTexture,
                            i,
                            i1,
                            UsbAccessoryService.VideoStreamSource.Camera);

                }
            }

            @Override
            public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surfaceTexture, int i, int i1) {
                // code
            }
            @Override
            public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surfaceTexture) {
                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surfaceTexture) {
                //TensorImage tensorImage = TensorImage.fromBitmap(videoFeedView.getBitmap());

            }
        });
        videoDataListener = new VideoFeeder.VideoDataListener() {
            @Override
            public void onReceive(byte[] videoBuffer, int size) {
                //lastReceivedFrameTime.set(System.currentTimeMillis());

                if (codecManager != null) {
                    codecManager.sendDataToDecoder(videoBuffer,
                            size,
                            UsbAccessoryService.VideoStreamSource.Camera.getIndex());
                }
            }
        };
        videoFeed = VideoFeeder.getInstance().getPrimaryVideoFeed();
        videoFeed.addVideoDataListener(videoDataListener);


    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
//        if(keylistener !=null){
//            KeyManager.getInstance().removeListener(keylistener);
//            keylistener = null;
//        }
        codecManager = null;
        videoFeed.removeVideoDataListener(videoDataListener);
        videoDataListener = null;
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        init();
    }
}
