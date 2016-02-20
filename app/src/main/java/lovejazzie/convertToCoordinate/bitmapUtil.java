package lovejazzie.convertToCoordinate;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.os.Message;

import com.socks.library.KLog;

import java.io.File;
import java.util.List;

/**
 * Created by Administrator on 2016/2/20.
 */
public class bitmapUtil implements Runnable {

    private final List<File> files;
    private final MainActivity.myHandle handle;
    private Bitmap[] bitmaps;
    public static boolean isStoped = false;

    public bitmapUtil(List<File> files, MainActivity.myHandle myHandle) {
        this.files = files;
        this.handle = myHandle;
        KLog.e("新建对象");
    }

    public Bitmap getBitmap(File filePath, int width, int height) {
        Bitmap bitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        // 获取这个图片的宽和高，注意此处的bitmap为null
        bitmap = BitmapFactory.decodeFile(filePath.getPath(), options);
        options.inJustDecodeBounds = false; // 设为 false
        // 计算缩放比
        int h = options.outHeight;
        int w = options.outWidth;
        int beWidth = w / width;
        int beHeight = h / height;
        int be = 1;
        if (beWidth < beHeight) {
            be = beWidth;
        } else {
            be = beHeight;
        }
        if (be <= 0) {
            be = 1;
        }
        options.inSampleSize = be;
        // 重新读入图片，读取缩放后的bitmap，注意这次要把options.inJustDecodeBounds 设为 false
        bitmap = BitmapFactory.decodeFile(filePath.getPath(), options);
        // 利用ThumbnailUtils来创建缩略图，这里要指定要缩放哪个Bitmap对象
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
                ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        return bitmap;
    }


    public Bitmap[] getBitmaps() {
        return bitmaps;
    }

    @Override
    public void run() {
        if (isStoped)
            return;
        File[] files = this.files.toArray(new File[this.files.size()]);
        for (File file : files) {
            //            bitmaps[i] = getBitmap(file, 100, 100);
            if (isStoped)
                return;
            Bundle bundle = new Bundle();
            Bitmap bitmap = getBitmap(file, 100, 100);
            bundle.putParcelable("bitmap", bitmap);
            bundle.putString("name", file.getName());
            Message message = Message.obtain();
            message.setData(bundle);
            handle.sendMessage(message);

        }
        isStoped = false;
    }

}
