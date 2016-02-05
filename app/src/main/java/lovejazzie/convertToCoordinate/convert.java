package lovejazzie.convertToCoordinate;

import android.app.Service;
import android.content.ContentResolver;
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
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import static java.lang.Thread.sleep;

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

    public convert() {
    }
    public convert(String path, Context context1) {//主窗口传回来
        //path是文件夹
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
    //            System.out.println("进来第一次");
    //            synchronized (lovejazzie.convertToCoordinate.convert.class) {
    //                if (convert == null) {
    //                    System.out.println("进来第二次");
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
        String scheme = data.getScheme();
        System.out.println(scheme);
        System.out.println(data.getPath());
        System.out.println(ContentResolver.SCHEME_FILE);
        String[] strings = {MediaStore.Images.Media.DATA};
        System.out.println(Arrays.toString(strings));
        Cursor cursor = context.getContentResolver().query(data, strings, null, null, null);
        if (cursor.moveToFirst()) {
            double columnIndexOrThrow = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            String cursorString = cursor.getString((int) columnIndexOrThrow);
            System.out.println(cursorString);
            cursor.close();
            return cursorString;
        }
        return null;
    }


    void convertImg(File[] listImg) {
        boolean is = false;
        for (File fa : listImg) {


            //System.out.println("相片名字是:" + fa.getName());
            //String p=fa.getName();
            //System.out.println("用equals判断相片名字是否包含[GCJ火星] :" +
            //        p.indexOf("[GCJ火星]"));
            if (!fa.getName().contains("GCJ")) {
                //已经改成GCJ
                ExifInterface exif = null;
                try {
                    exif = new ExifInterface(fa.getPath());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                String lat = null;
                String lng = null;
                //                System.out.println("打印看看"+exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE));
                if (exif != null) {
                    lng = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
                    lat = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
                    //                    System.out.println(lat);
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
        System.out.println("trueorfalse经纬度: " + is);
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
        System.out.println("对比文件名 如果已经修改 就忽略" + ((String) fa.getPath()).contains("GCJ"));
        String old_path = fa.getParentFile() + "/" + fa.getName();
        String new_path = fa.getParentFile() + "/" + "#GCJ"
                + "[" + convertToDouble(lat) + "," + convertToDouble(lng) + "]" + fa.getName();
        String new_name = "#GCJ" + "[" + convertToDouble(lat) + "," + convertToDouble(lng) + "]" + fa.getName();
        String ss = convertToDouble(lat) + "纬度"
                + convertToDouble(lng) + "经度";
        double loc[] = transform(convertToDouble(lat), convertToDouble(lng));
        String GCJ_lat = convertTOString(loc[0]);
        String GCJ_lng = convertTOString(loc[1]);
        System.out.println("写入经纬度");
        exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE, GCJ_lat);
        exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, GCJ_lng);
        System.out.println("保存ing");
        if (!("".equals(fa.getPath()))) {
            exif.saveAttributes();
            System.out.println("源坐标是 " + ss + "真实输出是:" + lat + " , " + lng + "\n"
                    + "已转成GCJ" + "\n"
                    + "纬度 " + convertToDouble(GCJ_lat) + "" + "经度 " + convertToDouble(GCJ_lng) + "\n"
                    + "文件名是:" + old_path + "\n"
                    + "将要改变为:" + new_path);

            System.out.println("目前图片名字是:" + fa.getName());
            b = fa.renameTo(new File(new_path));
            //File filess=new File(fa,fa.getName());//"[GCJ火星]"+
            System.out.println(b ? "改名成功" : "改名失败");
            //System.out.println(filess.getName());
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.Images.Media.LONGITUDE, loc[1]);
            contentValues.put(MediaStore.Images.Media.LATITUDE, loc[0]);

            contentValues.put(MediaStore.Images.Media.DATA,  new_path);//miui系统数据库设定data是主键 不可更换?
            double i = context.getContentResolver().update(data, contentValues, null, null);
            data = null;
            if (b) {
                //                context.getContentResolver().insert(data, contentValues);
                System.out.println("返回的数字是 " + i);
                System.out.println("目前图片名字是:" + fa.getName());
            }
            return b;
        }
        return b;
    }
    //double[] transform = transform(23.125601, 113.365145);
    //System.out.println(transform[0]+"纬度"+transform[1]+"经度");


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

            //            System.out.println("目前全局数字" + convert.count);
            //            System.out.println("目前数字" + count);
            try {
                sleep(3000);
                cout = 0;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("执行了" + convert.cout);
            if (intentFile != null) {
                String analyzed = analyze();//一个文件
                if (analyzed != null) {

                    convertImg(new File[]{new File(analyzed)});
                    System.out.println("执行了" + convert.cout);
                }
            }
            if (files != null)
                convertImg(files);
            //        convert = null;
            intentFile = null;
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
