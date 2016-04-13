package lovejazzie.convertToCoordinate;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
//import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
//import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MapActivity extends AppCompatActivity implements BaiduMap.OnMapLoadedCallback {


    private static BaiduMap baiduMap;
    private List<Map<String, convert.file>> imageList;
    private int count = 0;
    private String key = "imageName";
    static LayoutInflater inflater = null;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    @SuppressWarnings(value = {"unchecked"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.map);
        final MapView mMap = (MapView) findViewById(R.id.map);
        baiduMap = mMap.getMap();
        imageList = (List<Map<String, convert.file>>) getIntent().getParcelableArrayListExtra("list").get(0);
        inflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        if (imageList != null) {
            cachedFile(imageList);
        }
        baiduMap.setOnMapLoadedCallback(this);
        final List<Myitem> i = new ArrayList<>();
        Button viewById = (Button) findViewById(R.id.button);
        viewById.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //// TODO: 2016/2/26 以下代码 请写入备忘录 由于parcela的特性 原来的File对象不能获取 要记住
            }
        });
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
    }

    /**
     * 缓存成文件发送到bitmapUitl再通过handle处理
     */
    private void cachedFile(List<Map<String, convert.file>> imageList) {
        List<String> files = new ArrayList<>();

        for (Map<String, convert.file> fileMap : imageList) {
            convert.file file = fileMap.get(key);
            files.add(file.getFilePath());//终于拿到了源文件 好累
        }
        myHandle handle = new myHandle(this);
        bitmapUtil.isStoped = false;
        bitmapUtil<myHandle> bitmapUtil = new bitmapUtil<>(files, handle);
        Thread thread = new Thread(bitmapUtil);
        thread.start();
    }

    public static void addOverlay(Bitmap bitmap, LatLng latLng) {
//        List<Myitem> i = new ArrayList<Myitem>();
//        if (count > imageList.size() - 1) {
//            ClusterManager<Myitem> manager = new ClusterManager<>(getApplication(), baiduMap);
//            manager.addItems(i);
//            baiduMap.setOnMapStatusChangeListener(manager);
//            return;
//        }

        if (inflater == null) {
            return;
        }


        @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.image_room, null);
        ImageView image = (ImageView) view.findViewById(R.id.imagelayout);
        /**
         */
//                    Bitmap bm = bitmapUtil.getBitmap(new File(file1.getFilePath()),80,80);
        image.setImageBitmap(bitmap);
        view.setDrawingCacheEnabled(true);
        view.measure(View.MeasureSpec.makeMeasureSpec(100, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(100, View.MeasureSpec.EXACTLY));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.buildDrawingCache();
        Bitmap drawingCache = view.getDrawingCache();

        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(drawingCache);
        MarkerOptions icon = new MarkerOptions().position(latLng)
                .icon(bitmapDescriptor).animateType(MarkerOptions.MarkerAnimateType.grow);
        baiduMap.addOverlay(icon);
//            i.add(new Myitem(icon));
    }

    @Override
    public void onMapLoaded() {

        设置地图显示范围();

    }

    private void 设置地图显示范围() {//尝试中文方法名
        convert.file file;
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Map<String, convert.file> stringfileMap : imageList) {
            file = stringfileMap.get(key);
            builder.include(file.getLatLng());
        }


        baiduMap.setMapStatus(MapStatusUpdateFactory.newLatLngBounds(builder.
                build()));
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    //    @Override
//    protected void onStop() {
//        super.onStop();
//        // ATTENTION: This was auto-generated to implement the App Indexing API.
//        // See https://g.co/AppIndexing/AndroidStudio for more information.
//        Action viewAction = Action.newAction(
//                Action.TYPE_VIEW, // TODO: choose an action type.
//                "Map Page", // TODO: Define a title for the content shown.
//                // TODO: If you have web page content that matches this app activity's content,
//                // make sure this auto-generated web page URL is correct.
//                // Otherwise, set the URL to null.
//                Uri.parse("http://host/path"),
//                // TODO: Make sure this auto-generated app deep link URI is correct.
//                Uri.parse("android-app://lovejazzie.convertToCoordinate/http/host/path")
//        );
//        AppIndex.AppIndexApi.end(client, viewAction);
//        // ATTENTION: This was auto-generated to implement the App Indexing API.
//        // See https://g.co/AppIndexing/AndroidStudio for more information.
//        client.disconnect();
//    }
//
//    @Override
//    public void onStart() {
//        super.onStart();
//
//        // ATTENTION: This was auto-generated to implement the App Indexing API.
//        // See https://g.co/AppIndexing/AndroidStudio for more information.
//        client.connect();
//        Action viewAction = Action.newAction(
//                Action.TYPE_VIEW, // TODO: choose an action type.
//                "Map Page", // TODO: Define a title for the content shown.
//                // TODO: If you have web page content that matches this app activity's content,
//                // make sure this auto-generated web page URL is correct.
//                // Otherwise, set the URL to null.
//                Uri.parse("http://host/path"),
//                // TODO: Make sure this auto-generated app deep link URI is correct.
//                Uri.parse("android-app://lovejazzie.convertToCoordinate/http/host/path")
//        );
//        AppIndex.AppIndexApi.start(client, viewAction);
//    }
//未解之谜 上面的代码不知道怎么上去的
    public static class myHandle extends Handler {
        private final WeakReference<MapActivity> mainActivityWeakReference;

        private myHandle(MapActivity mainActivityWeakReference) {
            this.mainActivityWeakReference = new WeakReference<>(mainActivityWeakReference);
        }

        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            Bitmap bitmap = bundle.getParcelable("bitmap");
            String Path = bundle.getString("name");
            convert.file file = new convert.file(Path);
            addOverlay(bitmap, file.getLatLng());
        }


    }
}

