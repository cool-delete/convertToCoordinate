package lovejazzie.convertToCoordinate;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {
//    double pi = 3.14159265358979324;
//    double a = 6378245.0;
//    double ee = 0.00669342162296594323;
//    EditText editText;
//    int count = 0;
//    boolean ispath = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
//        String path = getIntent().getStringExtra("path");
//        if (path != null) {
//            ispath = true;
//            convert(new File[]{new File(path)},ispath);
//            return;
//
//        }
        setContentView(R.layout.activity_main);
//        //        String[] strings = {MediaStore.Images.Media.DATA};
//        //        Cursor cursor = this.getContentResolver().query(data, strings, null, null, null);
//        //        String string = cursor.toString();
//        //        System.out.println("到底是什么" + string);
//        //        cursor.close();
//        editText = (EditText) findViewById(R.id.editText);
//        Button button = (Button) findViewById(R.id.button);
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String root = editText.getText().toString();
//
//                File r = new File(root);
//                if (r.isDirectory()) {
//
//                    File[] listImg = r.listFiles();
//                    if (listImg == null) {
//                        Toast.makeText(MainActivity.this, "亲~..好像这个不是文件夹吧,我要文件夹哦",
//                                Toast.LENGTH_LONG).show();
//
//                    } else if (0 == listImg.length) {
//                        Toast.makeText(MainActivity.this, "诶多.里面找不到文件诶 要不确认一下路径?",
//                                Toast.LENGTH_LONG).show();
//
//                    } else {
//                        convert(listImg,ispath);
//                    }
//                } else {
//                    Toast.makeText(MainActivity.this, "亲~..好像这个不是文件夹吧,我要文件夹哦",
//                            Toast.LENGTH_LONG).show();
//                }
//
//            }
//
//        });
//

    }

//    void convert(File[] listImg,boolean ispath) {
//        boolean is = false;
//        for (File fa : listImg) {
//
//
//            //System.out.println("相片名字是:" + fa.getName());
//            //String p=fa.getName();
//            //System.out.println("用equals判断相片名字是否包含[GCJ火星] :" +
//            //        p.indexOf("[GCJ火星]"));
//            if (!fa.getName().contains("GCJ")) {
//                //已经改成GCJ
//                ExifInterface exif = null;
//                try {
//                    exif = new ExifInterface(fa.getPath());
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//
//                String lat = null;
//                String lng = null;
//                //                System.out.println("打印看看"+exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE));
//                if (exif != null) {
//                    lng = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
//                    lat = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
//                    //                    System.out.println(lat);
//                }
//
//
//                if (lat != null) {
//                    is = true;//有纬度
//                    try {
//                        boolean b = imgConvertToGCJ(fa, lat, lng, exif);
//                        if (b)
//                            count++;//能改
//                        //MediaStore.Images.Media.insertImage(getContentResolver(), fa.getPath(),
//                        // "GCJ-" + fa.getName(), null);
//                        //sendBroadcast(
//                        // new Intent(Intent.ACTION_MEDIA_MOUNTED,
//                        // Uri.parse("file://" + Environment.getExternalStorageDirectory())));
//
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//
//            }
//
//
//        }
//        System.out.println(is);
//        if (!ispath) {
//            Toast.makeText(this, is
//                    ? count > 0
//                    ? count > 10
//                    ? "哇 好有趣 好像去了很多好玩的地方,已经帮你转换了" + count + "张相片了," +
//                    "等一下google地图会帮你生成时光轴"
//                    : "嗯嗯,已经完成了" + count + "张图片的转换,可以去google地图查看时光轴"
//                    : "不好意思,没有权限在此目录下修改经纬度 还是换成内部储存的目录吧"
//                    : "现在这个文件夹找不到有位置记录的照片,以后拍照可以试下记录位置,google地图会生成时间轴的", Toast.LENGTH_LONG).show();
//            if (is && count > 0)
//                finish();
//        }
//    }
//
//
//    private boolean imgConvertToGCJ(File fa, String lat, String lng, ExifInterface exif) throws IOException {
//        String old_path = fa.getParentFile() + "/" + fa.getName();
//        String new_path = fa.getParentFile() + "/" + "#GCJ"
//                + "[" + convertToDouble(lat) + "," + convertToDouble(lng) + "]" + fa.getName();
//        String ss = convertToDouble(lat) + "纬度"
//                + convertToDouble(lng) + "经度";
//        double loc[] = transform(convertToDouble(lat), convertToDouble(lng));
//        String GCJ_lat = convertTOString(loc[0]);
//        String GCJ_lng = convertTOString(loc[1]);
//        System.out.println("写入经纬度");
//        exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE, GCJ_lat);
//        exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, GCJ_lng);
//        System.out.println("保存ing");
//        exif.saveAttributes();
//        System.out.println("源坐标是 " + ss + "真实输出是:" + lat + " , " + lng + "\n"
//                + "已转成GCJ" + "\n"
//                + "纬度 " + convertToDouble(GCJ_lat) + "" + "经度 " + convertToDouble(GCJ_lng) + "\n"
//                + "文件名是:" + old_path + "\n"
//                + "将要改变为:" + new_path);
//
//        System.out.println("目前图片名字是:" + fa.getName());
//        boolean b = false;
//        b = fa.renameTo(new File(new_path));
//        //File filess=new File(fa,fa.getName());//"[GCJ火星]"+
//        System.out.println(b ? "改名成功" : "改名失败");
//        //System.out.println(filess.getName());
//        try {
//            sleep(1000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
//        Uri uri = Uri.fromFile(new File(new_path));
//        intent.setData(uri);
//        sendBroadcast(intent);
//
//        System.out.println("目前图片名字是:" + fa.getName());
//        return b;
//    }
//    //double[] transform = transform(23.125601, 113.365145);
//    //System.out.println(transform[0]+"纬度"+transform[1]+"经度");
//
//
//    private double convertToDouble(String s) {
//        double ss = 0, mm = 0, dd = 0;
//        String[] strings = s.split(",");
//        for (int i = 0; i < strings.length; i++) {
//            String[] sp = strings[i].split("/");
//
//            switch (i) {
//
//                case 0:
//                    ss = Double.parseDouble(sp[0]) / Double.parseDouble(sp[1]);
//                    //此处原为整型封装 因发现计算后丢失精度 改为双精度封装方法
//                    break;
//                case 1:
//                    mm = Double.parseDouble(sp[0]) / Double.parseDouble(sp[1]);
//                    break;
//                case 2:
//                    dd = Double.parseDouble(sp[0]) / Double.parseDouble(sp[1]);
//                    break;
//            }
//        }
//        return ss + (dd / 60 + mm) / 60;
//
//    }
//
//    private String convertTOString(double d) {
//
//        d = Math.abs(d);
//        String dms = Location.convert(d, Location.FORMAT_SECONDS);
//        String[] splits = dms.split(":");
//        String[] secnds = (splits[2]).split("\\.");
//        String seconds;
//        if (secnds.length == 0) {
//            seconds = splits[2];
//        } else {
//            seconds = secnds[0];
//        }
//        return splits[0] + "/1," + splits[1] + "/1," + seconds + "/1";
//    }
//
//    public double[] transform(double wgLat, double wgLon) {
//
//        double dLat = transformLat(wgLon - 105.0, wgLat - 35.0);
//        double dLon = transformLon(wgLon - 105.0, wgLat - 35.0);
//        double radLat = wgLat / 180.0 * pi;
//        double magic = Math.sin(radLat);
//        magic = 1 - ee * magic * magic;
//        double sqrtMagic = Math.sqrt(magic);
//        dLat = (dLat * 180.0) / ((a * (1 - ee)) / (magic * sqrtMagic) * pi);
//        dLon = (dLon * 180.0) / (a / sqrtMagic * Math.cos(radLat) * pi);
//
//        return new double[]{wgLat + dLat, wgLon + dLon};
//    }
//
//    private double transformLon(double x, double y) {
//        double ret = 300.0 + x + 2.0 * y + 0.1 * x * x + 0.1 * x * y + 0.1 * Math.sqrt(Math.abs(x));
//        ret += (20.0 * Math.sin(6.0 * x * pi) + 20.0 * Math.sin(2.0 * x * pi)) * 2.0 / 3.0;
//        ret += (20.0 * Math.sin(x * pi) + 40.0 * Math.sin(x / 3.0 * pi)) * 2.0 / 3.0;
//        ret += (150.0 * Math.sin(x / 12.0 * pi) + 300.0 * Math.sin(x / 30.0 * pi)) * 2.0 / 3.0;
//        return ret;
//    }
//
//    private double transformLat(double x, double y) {
//        double ret = -100.0 + 2.0 * x + 3.0 * y + 0.2 * y * y + 0.1 * x * y + 0.2 * Math.sqrt(Math.abs(x));
//        ret += (20.0 * Math.sin(6.0 * x * pi) + 20.0 * Math.sin(2.0 * x * pi)) * 2.0 / 3.0;
//        ret += (20.0 * Math.sin(y * pi) + 40.0 * Math.sin(y / 3.0 * pi)) * 2.0 / 3.0;
//        ret += (160.0 * Math.sin(y / 12.0 * pi) + 320 * Math.sin(y * pi / 30.0)) * 2.0 / 3.0;
//        return ret;
//    }
}
