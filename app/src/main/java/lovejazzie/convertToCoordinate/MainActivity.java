package lovejazzie.convertToCoordinate;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//import com.socks.library.//KLog;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {
    EditText editText;
    public final String TAG = "convertToCoordinate";
    private View view;
    private ListView listView;
    private TextView tvDir;
    private String rootFile;
    private File[] nowFiles;
    private File nowFile;
    private View mainView;
    private boolean focused;
    private int getCount;
    private static SimpleAdapter adapter;
    private static List<Map<String, Object>> list;
    private static List<Bitmap> lmageRoom = new ArrayList<>();
    private static String root;


    @SuppressLint("InflateParams")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mainView = inflater.inflate(R.layout.activity_main, null);
        setContentView(mainView);
        focused = false;
        Intent i = new Intent(this, convert.class);
        startService(i);
        rootFile = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString();
        editText = (EditText) findViewById(R.id.editText2);
        editText.setText(rootFile);
        Button btnDo = (Button) findViewById(R.id.button3);
        btnDo.setOnClickListener(this);
        ImageButton searchFile = (ImageButton) findViewById(R.id.btn_img);
        searchFile.setOnClickListener(this);
        Button check = (Button) findViewById(R.id.checkloc);
        final Context context = getApplicationContext();
        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckCoordinatesError.init(context);
            }
        });
        CheckCoordinatesError.setCheckListener(new CheckCoordinatesError.checkloc() {
            @Override
            public boolean check(boolean a) {
                System.out.println("是否相等"+a);
                return a;
            }
        });
    }


    int count = 0;
    int imageCount = 0;

    public static List<Bitmap> getLmageRoom() {
        return lmageRoom;
    }

    private void Run(final File[] files) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                sumCount(files);
                handle.sendEmptyMessage(count);

            }
        }).start();


    }

    private int sumCount(File[] files1) {
        try {
            for (File file : files1) {
                ////KLog.e(file.toString());
                if (file.isDirectory()) {
                    count++;
                    File[] files = file.listFiles();
                    sumCount(files);
                } else if (file.getName().endsWith("JPG") | file.getName().endsWith("JPEG")) {
                    imageCount++;
                }
                if (count > 50) {
                    return 0;
                }
            }
        } catch (NullPointerException e) {
            e.getCause();
        }
        return 0;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button3:

                root = editText.getText().toString();
                File r = new File(root);
                if (r.isDirectory()) {
                    File[] list = r.listFiles();

                    if (list == null) {
                        showToast("亲~..好像这个不是文件夹吧,我要文件夹哦");

                    } else if (0 == list.length) {
                        showToast("请换另一个文件夹");

                    } else {
                        if (root.equals("/")) {
                            showToast("不接受根目录");
                            return;
                        }
                        showToast("请稍等");
                        getHandle();
                        Run(list);

                    }
                } else {
                    showToast("亲~..好像这个不是文件夹吧,我要文件夹哦");
                }
                break;
            case R.id.btn_img:
                GuiList();
                break;
            case R.id.button2:
                setMainView();
                reViewList();
                break;
        }
    }

    private void allOk(String root, int run) {
        if (run != 0) {
            count = 0;
            if (imageCount == 0) {
                showToast(run + "个文件夹下都没有图片,请换目录");
                return;
            }/**
             如果传入根目录,会在N次递归后扫描出图片文件 但是在子线程只能执行扫描第一层*/
        }//手贱了 修复完成?
        bitmapUtil.isStoped = true;
        reViewList();
        nowFile = null;
        nowFiles = null;


        convert myConvert = new convert(root, MainActivity.this);
        convert.cout = 0;
        new Thread(myConvert).start();
    }

    private void setMainView() {
        setContentView(mainView);
        editText.setText(tvDir.getText());
        focused = false;
        listView = null;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (handle == null) {
            return;
        }
        handle.removeCallbacksAndMessages(null);
    }

    @SuppressLint("InflateParams")
    private void GuiList() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (view == null) {

            view = inflater.inflate(R.layout.list_file, null);
        }
        setContentView(view);
        listView = (ListView) view.findViewById(R.id.listView);
        Button button = (Button) view.findViewById(R.id.button2);
        tvDir = (TextView) view.findViewById(R.id.tv_dir);
        tvDir.setText(rootFile);
        tvDir.setMovementMethod(ScrollingMovementMethod.getInstance());
        button.setOnClickListener(this);
        nowFiles = new File(rootFile).listFiles();
        listView.setOnItemClickListener(this);
        makFileList(new File(rootFile));
        focused = true;

    }

    private void makFileList(File filePath) {
        list = new ArrayList<>();
        File[] files = filePath.listFiles();
        Map<String, Object> item1 = new HashMap<>();
        item1.put("fileName", "上一层文件夹");
        list.add(item1);
        List<String> image = new ArrayList<>();
        for (File file : files) {
            Map<String, Object> item = new HashMap<>();
            if (file.isDirectory()) {
                item.put("icon", R.drawable.folder);
            } else if (file.getName().contains("JPG") || file.getName().contains(".jpg")) {

                item.put("icon", R.drawable.file);
                image.add(file.getPath());
            } else {
                item.put("icon", R.drawable.file);
            }


            item.put("fileName", file.getName());
            list.add(item);
        }//// TODO: 2016/2/24 下面的simpleAdapter好像有问题 系统会打印e级别的消息 uir错误
        adapter = new SimpleAdapter(this, list,
                R.layout.line,
                new String[]{"fileName", "icon"},
                new int[]{R.id.tv_fileName, R.id.imageView});
        adapter.setViewBinder(new SimpleAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Object data, String textRepresentation) {
                if (view instanceof ImageView & data instanceof Bitmap) {
                    ((ImageView) view).setImageBitmap((Bitmap) data);
                    return true;
                }

                return false;
            }
        });

        listView.setAdapter(adapter);
        nowFiles = filePath.listFiles();
        nowFile = filePath;
        if (image.size() > 0) {
            upDataAdapter(image);
        }
        try {
            tvDir.setText(filePath.getCanonicalPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void upDataAdapter(List<String> list) {
        handle = new myHandle(this);
        lovejazzie.convertToCoordinate.bitmapUtil.isStoped = false;
        bitmapUtil<myHandle> bitmapUtil = new bitmapUtil<>(list, handle);
        new Thread(bitmapUtil).start();
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position == 0) {
            reViewList();
            upFile();
        } else {
            //KLog.d(position);
            nowFile = nowFiles[position - 1];//list比file数多1,此处减去
            if (nowFile.listFiles() == null) {
                showToast("确定是当前文件夹吗");
                //KLog.d(Arrays.toString(nowFile.listFiles()));
                nowFile = nowFile.getParentFile();
            }
//            if (nowFile.listFiles().length == 0) {
//                showToast("里面是空文件哦");
//            }
            else {
                makFileList(nowFile);
            }
        }
    }

    private void reViewList() {
        lovejazzie.convertToCoordinate.bitmapUtil.isStoped = true;
        if (adapter != null) {
            adapter.notifyDataSetChanged();
            list = null;
            adapter = null;
        }
    }

    private void upFile() {
        String parent = nowFile.getName();
        //KLog.d(parent);
        if (parent.equals("")) {
            showToast("真会玩 已经上天花板啦!");
            return;
        }

        makFileList(nowFile.getParentFile());
    }

    private void showToast(String say) {
        Toast.makeText(this, say, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onKeyUp(int keyCode, @NonNull KeyEvent event) {
        if (keyCode == 4) {
            if (focused) {
                setMainView();
                //KLog.d("view不活跃");
                return true;
            }
        }
        //KLog.d("view活跃");
        return super.onKeyUp(keyCode, event);
    }


    private myHandle handle;


    public myHandle getHandle() {

        return handle = new myHandle(this);
    }

    public static class myHandle extends Handler {
        private final WeakReference<MainActivity> mainActivityWeakReference;

        private myHandle(MainActivity mainActivityWeakReference) {
            this.mainActivityWeakReference = new WeakReference<>(mainActivityWeakReference);
        }

        @Override
        public void handleMessage(Message msg) {
            MainActivity mainActivity = mainActivityWeakReference.get();
            if ((mainActivity == null)) {
                return;
            }
            if (msg.what != 0 || mainActivity.imageCount != 0) {
                if (msg.what < 50) {
                    mainActivity.allOk(root, msg.what);
                } else mainActivity.showToast("子文件夹太多了");
                mainActivity.imageCount = mainActivity.count = 0;
                return;
            }
            Bundle bundle = msg.getData();
            Bitmap bitmap = bundle.getParcelable("bitmap");
            Map<String, Object> data = new HashMap<>();

            data.put("icon", bitmap);
            File s = new File(bundle.getString("name", null));
            lmageRoom.add(bitmap);
            data.put("fileName", s.getName());
            if (!bitmapUtil.isStoped) {
                list.set(get_index(list, s.getPath()), data);
                adapter.notifyDataSetChanged();
            }
        }

        private int get_index(List list, String s) {
            boolean equals;
            int loc = 0;
            for (int i = 0; i < list.size(); i++) {

                Object o = list.get(i);
                if (o instanceof Map) {
                    Object fileName = ((Map) o).get("fileName");
                    if (fileName instanceof String) {
                        System.out.println(fileName + "文件名");
                        equals = new File(s).getName().equals(fileName);
                        if (equals) {
                            loc = i;
                            break;
                        }
                    }
                }
            }
            return loc;
        }
    }

}

