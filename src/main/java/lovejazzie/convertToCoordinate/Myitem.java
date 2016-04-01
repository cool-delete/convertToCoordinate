package lovejazzie.convertToCoordinate;

import com.baidu.mapapi.clusterutil.clustering.ClusterItem;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.model.LatLng;

/**
 * Created by Administrator on 2016/2/27.
 */
public class Myitem implements ClusterItem {
    MarkerOptions overlayOptions;

    public Myitem(MarkerOptions options) {
        overlayOptions = options;
    }

    @Override
    public LatLng getPosition() {
        return overlayOptions.getPosition();
    }

    @Override
    public BitmapDescriptor getBitmapDescriptor() {
        return overlayOptions.getIcon();
    }
}
