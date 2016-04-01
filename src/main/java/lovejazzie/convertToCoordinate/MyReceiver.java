package lovejazzie.convertToCoordinate;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

//import com.socks.library.KLog;

public class MyReceiver extends BroadcastReceiver //implements Thread.UncaughtExceptionHandler
 {
//        int i=0;
//    public MyReceiver() {
//    }

    @Override
    public void onReceive(Context context, Intent intent) {
//        KLog.init(BuildConfig.LOG_DEBUG);
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        // throw new UnsupportedOperationException("Not yet implemented");
//        i++;
//        System.out.println(""+i);


//        Uri data = intent.getData();
//        String scheme = data.getScheme();
//        System.out.println(scheme);
//        System.out.println(data.getScheme()+" "+data.getAuthority()+" "+data.getPath()+"\n");
//        String[] strings = {MediaStore.Images.Media.LATITUDE};
////        System.out.println(Arrays.toString(strings));
//        Cursor cursor = context.getContentResolver().query(data, strings, null, null, null);
//        String string = cursor.toString();
//        cursor.moveToFirst();
//        double columnIndexOrThrow = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//        String cursorString = cursor.getString((int) columnIndexOrThrow);
//        System.out.println(cursorString);
//        cursor.close();
//        Intent intent1 =new Intent(context,MainActivity.class);
//        intent1.putExtra("path",cursorString);
//        intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        context.startActivity(intent1);
//        KLog.d(intent.getAction());
        convert getconvet = new convert(intent, context);
//        if (null == getconvet){
//            System.out.println("没有了");
//        } else {
//            System.out.println("有了");
//
//        }
//        System.out.println("宇宙大爆炸 重新计时 次数 "+i);
        convert.cout = 0;
        new Thread(getconvet).start();
//getconvet.Run();
    }

//    @Override
//    public void uncaughtException(Thread thread, Throwable ex) {
//        System.out.println(""+ex);
//    }
}
