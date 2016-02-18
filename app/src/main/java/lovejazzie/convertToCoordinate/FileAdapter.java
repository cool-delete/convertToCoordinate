package lovejazzie.convertToCoordinate;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;

import java.io.File;
import java.util.List;

/**
 * Created by Administrator on 2016/2/18.
 */
public class FileAdapter extends ArrayAdapter<File> {

    private  int resource;

    public FileAdapter(Context context, int resource, List<File> objects) {
        super(context, resource, objects);
        this.resource = resource;
    }//
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        File file = getItem(position);
        LinearLayout imageView;
        String fileName = file.getName();

        if (convertView == null) {
            imageView = new LinearLayout(getContext());
            String inflaterService = Context.LAYOUT_INFLATER_SERVICE;
            LayoutInflater service = (LayoutInflater) getContext().getSystemService(inflaterService);
            service.inflate(resource, imageView, true);//2参依附到1参上
        }


        return super.getView(position, convertView, parent);
    }


}
