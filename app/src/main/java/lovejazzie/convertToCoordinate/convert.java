package lovejazzie.convertToCoordinate;

import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.socks.library.KLog;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

/**
 * Created by Administrator on 2016/1/30.
 */
public class convert extends Service implements Runnable {

    double pi = 3.14159265358979324;
    double a = 6378245.0;
    double ee = 0.00669342162296594323;
    private int count;
    Context context;
    File[] files;
    Uri data;
    Intent intentFile;
    static int cout = 0;

    public convert(String path, Context context1) {//主窗口传回来
        //path是文件夹l
        Log.d(MainActivity.TAG, "构造函数" + path);
        File file = new File(path);
        files = file.listFiles();
        context = context1;
    }

    //    private volatile static convert convert;


    public convert(Intent intent, Context context1) {
        intentFile = intent;
        context = context1;
    }
    //    private convert(Intent intent, Context context1) {//通过意图传回来
    //        intentFile = intent;
    //        context = context1;
    //        //
    //    }
    //
    //    private static class inner {
    //        private static final convert CONVERT = new convert();
    //    }

    //    public static convert getconvet(Intent intent, Context context1context) {
    //        if (convert == null) {
    //             KLog.d("进来第一次");
    //            synchronized (lovejazzie.convertToCoordinate.convert.class) {
    //                if (convert == null) {
    //                     KLog.d("进来第二次");
    //                    convert = new convert(intent, context1context);
    //                    return convert;
    //                }
    //            }
    //        }
    //        //        intentFile = intent;
    //        //        context=context1context;
    //        //        return inner.CONVERT;
    //        return convert;
    //    }

    public String analyze() {
        data = intentFile.getData();
        intentFile = null;
        //        KLog.d(ContentResolver.SCHEME_FILE);
        String[] strings = {MediaStore.Images.Media.DATA};
        KLog.d("默认数组" + Arrays.toString(strings));
        Cursor cursor = context.getContentResolver().query(data, strings, null, null, null);
        if (cursor.moveToFirst()) {
            double columnIndexOrThrow = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            String cursorString = cursor.getString((int) columnIndexOrThrow);
            KLog.d("选择显示" + cursorString);
            cursor.close();
            return cursorString;
        }
        return null;
    }


    void convertImg(File[] listImg) {
        boolean is = false;
        KLog.d("开始转换");
        for (File fa : listImg) {

            KLog.d("相片名字是:" + fa.getName());

            //String p=fa.getName();
            if (!fa.getName().contains("GCJ")) {
                //已经改成GCJ
                KLog.d("用equals判断相片名字是否包含[GCJ火星] :");
                ExifInterface exif = null;
                try {
                    exif = new ExifInterface(fa.getPath());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                String lat = null;
                String lng = null;
                KLog.d("打印看看" + exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE));
                if (exif != null) {
                    lng = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
                    lat = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
                    //                     KLog.d(lat);
                }


                if (lat != null) {
                    is = true;//有纬度
                    try {
                        count = 0;
                        boolean b = imgConvertToGCJ(fa, lat, lng, exif);
                        if (b)
                            count++;//能改
                        //MediaStore.Images.Media.insertImage(getContentResolver(), fa.getPath(),
                        // "GCJ-" + fa.getName(), null);
                        //sendBroadcast(
                        // new Intent(Intent.ACTION_MEDIA_MOUNTED,
                        // Uri.parse("file://" + Environment.getExternalStorageDirectory())));

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }


        }
        KLog.d("trueorfalse经纬度: " + is);
                Looper.prepare();
        final boolean finalIs = is;
        new Handler(Looper.getMainLooper()).post(new Runnable() {
                                                     @Override
                                                     public void run() {
                                                         Toast.makeText(context, finalIs
                                                                 ? count > 0
                                                                 ? count > 10
                                                                 ? "哇 好有趣 好像去了很多好玩的地方,已经帮你转换了" + count + "张相片了," +
                                                                 "等一下google地图会帮你生成时光轴"
                                                                 : "嗯嗯,已经完成了" + count + "张图片的转换,可以去google地图查看时光轴"
                                                                 : "不好意思,没有权限在此目录下修改经纬度 还是换成内部储存的目录吧"
                                                                 : "现在这个文件夹找不到有位置记录的照片,以后拍照可以试下记录位置,google地图会生成时间轴的", Toast.LENGTH_LONG).show();
                                                     }
                                                 }
        );


        Looper.loop();

    }


    private boolean imgConvertToGCJ(File fa, String lat, String lng, ExifInterface exif) throws IOException {
        boolean b = false;
        KLog.d("对比文件名 如果已经修改 就忽略" +  fa.getPath().contains("GCJ"));
        //命名随随机化
        String old_path = fa.getParentFile() + "/" + fa.getName();
        String new_path = fa.getParentFile() + "/" + "#GCJ"
                + "[" + convertToDouble(lat) + "," + convertToDouble(lng) + "]" + System.currentTimeMillis()+".JPG";
        String ss = convertToDouble(lat) + "纬度"
                + convertToDouble(lng) + "经度";
        double loc[] = transform(convertToDouble(lat), convertToDouble(lng));
        String GCJ_lat = convertTOString(loc[0]);
        String GCJ_lng = convertTOString(loc[1]);
        exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE, GCJ_lat);
        exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, GCJ_lng);
        if (!("".equals(fa.getPath()))) {
            exif.saveAttributes();
            KLog.d("源坐标是 " + ss + "真实输出是:" + lat + " , " + lng + "\n"
                    + "已转成GCJ" + "\n"
                    + "纬度 " + convertToDouble(GCJ_lat) + "" + "经度 " + convertToDouble(GCJ_lng) + "\n"
                    + "文件名是:" + old_path + "\n"
                    + "将要改变为:" + new_path);

            KLog.d("目前图片名字是:" + fa.getName());
            b = fa.renameTo(new File(new_path));
            KLog.d(b ? "改名成功" : "改名失败");

            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.Images.Media.LONGITUDE, loc[1]);
            contentValues.put(MediaStore.Images.Media.LATITUDE, loc[0]);

            contentValues.put(MediaStore.Images.Media.DATA,  new_path);//miui系统数据库设定data是主键 不可更换?
            if (b) {
                if (data == null) {
                    data = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    Cursor query = context.getContentResolver().query(data, new String[]{MediaStore.Images.Media._ID},
                            MediaStore.Images.Media.DATA + "=?", new String[]{fa.getPath()}, null);
                    query.moveToFirst();
                    String idString = query.getString(query.getColumnIndexOrThrow(MediaStore.Images.Media._ID));
                    data = Uri.parse("content://media/external/images/media/" + idString);
                    query.close();

                    KLog.d("目前图片名字是:" + fa.getName());
            }
                context.getContentResolver().update(data, contentValues, null, null);
            }
            data = null;
            return b;
        }
        return b;
    }
    //double[] transform = transform(23.125601, 113.365145);
    // KLog.d(transform[0]+"纬度"+transform[1]+"经度");


    private double convertToDouble(String s) {
        double ss = 0, mm = 0, dd = 0;
        String[] strings = s.split(",");
        for (int i = 0; i < strings.length; i++) {
            String[] sp = strings[i].split("/");

            switch (i) {

                case 0:
                    ss = Double.parseDouble(sp[0]) / Double.parseDouble(sp[1]);
                    //此处原为整型封装 因发现计算后丢失精度 改为双精度封装方法
                    break;
                case 1:
                    mm = Double.parseDouble(sp[0]) / Double.parseDouble(sp[1]);
                    break;
                case 2:
                    dd = Double.parseDouble(sp[0]) / Double.parseDouble(sp[1]);
                    break;
            }
        }
        return ss + (dd / 60 + mm) / 60;

    }

    private String convertTOString(double d) {

        d = Math.abs(d);
        String dms = Location.convert(d, Location.FORMAT_SECONDS);
        String[] splits = dms.split(":");
        String[] secnds = (splits[2]).split("\\.");
        String seconds;
        if (secnds.length == 0) {
            seconds = splits[2];
        } else {
            seconds = secnds[0];
        }
        return splits[0] + "/1," + splits[1] + "/1," + seconds + "/1";
    }

    public double[] transform(double wgLat, double wgLon) {

        double dLat = transformLat(wgLon - 105.0, wgLat - 35.0);
        double dLon = transformLon(wgLon - 105.0, wgLat - 35.0);
        double radLat = wgLat / 180.0 * pi;
        double magic = Math.sin(radLat);
        magic = 1 - ee * magic * magic;
        double sqrtMagic = Math.sqrt(magic);
        dLat = (dLat * 180.0) / ((a * (1 - ee)) / (magic * sqrtMagic) * pi);
        dLon = (dLon * 180.0) / (a / sqrtMagic * Math.cos(radLat) * pi);

        return new double[]{wgLat + dLat, wgLon + dLon};
    }

    private double transformLon(double x, double y) {
        double ret = 300.0 + x + 2.0 * y + 0.1 * x * x + 0.1 * x * y + 0.1 * Math.sqrt(Math.abs(x));
        ret += (20.0 * Math.sin(6.0 * x * pi) + 20.0 * Math.sin(2.0 * x * pi)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(x * pi) + 40.0 * Math.sin(x / 3.0 * pi)) * 2.0 / 3.0;
        ret += (150.0 * Math.sin(x / 12.0 * pi) + 300.0 * Math.sin(x / 30.0 * pi)) * 2.0 / 3.0;
        return ret;
    }

    private double transformLat(double x, double y) {
        double ret = -100.0 + 2.0 * x + 3.0 * y + 0.2 * y * y + 0.1 * x * y + 0.2 * Math.sqrt(Math.abs(x));
        ret += (20.0 * Math.sin(6.0 * x * pi) + 20.0 * Math.sin(2.0 * x * pi)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(y * pi) + 40.0 * Math.sin(y / 3.0 * pi)) * 2.0 / 3.0;
        ret += (160.0 * Math.sin(y / 12.0 * pi) + 320 * Math.sin(y * pi / 30.0)) * 2.0 / 3.0;
        return ret;
    }

    @Override
    public void run() {
        convert.cout++;
        //                count++;
        if (cout < 2) {

            if (intentFile != null) {
                String analyzed = analyze();//一个文件
                if (analyzed != null) {

                    convertImg(new File[]{new File(analyzed)});
                }
            }
            if (files != null) {

                convertImg(files);
            }

            //        convert = null;
            files = null;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_STICKY;
    }
}
