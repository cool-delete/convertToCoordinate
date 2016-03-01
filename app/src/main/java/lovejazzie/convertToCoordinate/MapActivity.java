package lovejazzie.convertToCoordinate;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.clusterutil.clustering.ClusterManager;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.model.LatLngBounds;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MapActivity extends AppCompatActivity implements BaiduMap.OnMapLoadedCallback {


    private BaiduMap baiduMap;
    private List<Map<String, convert.file>> imageList;
    private convert.file file;
    private int count = 0;
    private ImageView image;
    private String key = "imageName";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.map);
        final MapView mMap = (MapView) findViewById(R.id.map);
        baiduMap = mMap.getMap();
        ArrayList<Parcelable> listroom = getIntent().getParcelableArrayListExtra("list");

        imageList = (List<Map<String, convert.file>>) listroom.get(0);
        this.baiduMap.setOnMapLoadedCallback(this);
        final List<Myitem> i = new ArrayList<Myitem>();
        Button viewById = (Button) findViewById(R.id.button);
        viewById.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //// TODO: 2016/2/26 以下代码 请写入备忘录 由于parcela的特性 原来的File对象不能获取 要记住
                if (count > imageList.size() - 1) {
                    ClusterManager<Myitem> manager = new ClusterManager<>(getApplication(), baiduMap);
                    manager.addItems(i);
                    baiduMap.setOnMapStatusChangeListener(manager);
                    return;
                }
                for (int c = 0; c < imageList.size(); c++) {

                Map<String, convert.file> fileMap = imageList.get(c);
                convert.file file1 = fileMap.get(key);
                LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                View view = inflater.inflate(R.layout.image_room, null);
                image = (ImageView) view.findViewById(R.id.imagelayout);
                Bitmap bm = bitmapUtil.getBitmap(new File(file1.getFilePath()), 80, 80);
                image.setImageBitmap(bm);
                view.setDrawingCacheEnabled(true);
                view.measure(View.MeasureSpec.makeMeasureSpec(100, View.MeasureSpec.EXACTLY),
                        View.MeasureSpec.makeMeasureSpec(100, View.MeasureSpec.EXACTLY));
                view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
                view.buildDrawingCache();
                Bitmap drawingCache = view.getDrawingCache();

                BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(drawingCache);
                MarkerOptions icon = new MarkerOptions().position(file1.getLatLng())
                        .icon(bitmapDescriptor).animateType(MarkerOptions.MarkerAnimateType.grow);
                baiduMap.addOverlay(icon);
                i.add(new Myitem(icon));
                count++;
                }
            }
        });
    }

    @Override
    public void onMapLoaded() {

        set范围();

    }

    private void set范围() {
        file = null;
        MapStatusUpdate statusUpdate = null;
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Map<String, convert.file> stringfileMap : imageList) {
            file = stringfileMap.get(key);
            builder.include(file.getLatLng());
        }

        statusUpdate = MapStatusUpdateFactory.newLatLngBounds(builder.
                build());

        baiduMap.setMapStatus(statusUpdate);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
