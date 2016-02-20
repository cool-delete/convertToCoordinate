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

import com.socks.library.KLog;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {
    EditText editText;
    public final String TAG = "convertToCoordinate";
    private Button btnDo;
    private View view;
    private ListView listView;
    private Button button;
    private TextView tvDir;
    private String rootFile;
    private File[] nowFiles;
    private File nowFile;
    private View mainView;
    private boolean focused;
    private int getCount;
    private static SimpleAdapter adapter;
    private lovejazzie.convertToCoordinate.bitmapUtil bitmapUtil;
    private static List<Map<String, Object>> list;

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
        btnDo = (Button) findViewById(R.id.button3);
        btnDo.setOnClickListener(this);
        ImageButton searchFile = (ImageButton) findViewById(R.id.btn_img);
        searchFile.setOnClickListener(this);
    }


    int count = 0;


    int run(File[] files) {
        try {
            for (File file : files) {
                KLog.e(file.toString());
                if (file.isDirectory()) {
                    count++;
                    File[] files1 = file.listFiles();
                    run(files1);
                }
            }
        } catch (NullPointerException e) {
            e.getCause();
        }
        return count;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button3:

                String root = editText.getText().toString();
                File r = new File(root);
                if (r.isDirectory()) {
                    File[] list = r.listFiles();

                    if (list == null) {
                        getToast("亲~..好像这个不是文件夹吧,我要文件夹哦");

                    } else if (0 == list.length) {
                        getToast("诶多.里面找不到文件诶 要不确认一下路径?");

                    } else {
                        int run = run(list);
                        if (run != 0) {
                            count = 0;
                            getToast("天啊 这个路径有" + run + "个文件夹.勉强试试");
                }
                        lovejazzie.convertToCoordinate.bitmapUtil.isStoped = true;
                        reViewList();
                        nowFile = null;
                        nowFiles = null;


                        convert convert = new convert(root, MainActivity.this);
                        new Thread(convert).start();

                    }
                } else {
                    getToast("亲~..好像这个不是文件夹吧,我要文件夹哦");
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

    private void setMainView() {
        setContentView(mainView);
        editText.setText(tvDir.getText());
        focused = false;
        listView = null;
    }

    @SuppressLint("InflateParams")
    private void GuiList() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (view == null) {

        view = inflater.inflate(R.layout.list_file, null);
        }
        setContentView(view);
        listView = (ListView) view.findViewById(R.id.listView);
        button = (Button) view.findViewById(R.id.button2);
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
        list = new ArrayList<Map<String, Object>>();
        File[] files = filePath.listFiles();
        Map<String, Object> item1 = new HashMap<String, Object>();
        item1.put("fileName", "上一层文件夹");
        list.add(item1);
        List<File> image = new ArrayList<File>();
        for (File file : files) {
            Map<String, Object> item = new HashMap<String, Object>();
            if (file.isDirectory()) {
                item.put("icon", R.drawable.folder);
            } else if (file.getName().contains("JPG") || file.getName().contains(".jpg")) {

                item.put("icon", R.drawable.file);
                image.add(file);
            } else {
                item.put("icon", R.drawable.file);
            }


            item.put("fileName", file.getName());
            list.add(item);
        }
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

    private void upDataAdapter(List<File> list) {
        handle = new myHandle(this);
        lovejazzie.convertToCoordinate.bitmapUtil.isStoped = false;
        bitmapUtil = new bitmapUtil(list, handle);
        new Thread(bitmapUtil).start();
        bitmapUtil = null;
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position == 0) {
            reViewList();
            upFile();
        } else {
            KLog.d(position);
            nowFile = nowFiles[position-1];//list比file数多1,此处减去
            if (nowFile.listFiles() == null) {
                getToast("确定是当前文件夹吗");
                KLog.d(Arrays.toString(nowFile.listFiles()));
                nowFile = nowFile.getParentFile();
            }
//            if (nowFile.listFiles().length == 0) {
//                getToast("里面是空文件哦");
//            }
            else {
                makFileList(nowFile);
            }
        }
    }

    private void reViewList() {
        lovejazzie.convertToCoordinate.bitmapUtil.isStoped = true;
        list = null;
        adapter.notifyDataSetChanged();
        adapter = null;
    }

    private void upFile() {
        String parent = nowFile.getName();
        KLog.d(parent);
        if (parent.equals("")) {
            getToast("真会玩 已经上天花板啦!");
            return;
        }

        makFileList(nowFile.getParentFile());
    }

    private void getToast(String say) {
        Toast.makeText(this, say, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onKeyUp(int keyCode, @NonNull KeyEvent event) {
        if (keyCode == 4) {
            if (focused) {
                setMainView();
                KLog.d("view不活跃");
                return true;
            }
        }
        KLog.d("view活跃");
        return super.onKeyUp(keyCode, event);
    }


    private myHandle handle;


    public myHandle getHandle() {

        return handle;
    }

    public static class myHandle extends Handler {
        private final WeakReference<MainActivity> mainActivityWeakReference;

        private myHandle(MainActivity mainActivityWeakReference) {
            this.mainActivityWeakReference = new WeakReference<MainActivity>(mainActivityWeakReference);
        }

        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            Bitmap bitmap = bundle.getParcelable("bitmap");
            Map<String, Object> data = new HashMap<String, Object>();
            data.put("icon", bitmap);
            String s = bundle.getString("name");
            data.put("fileName", s);
            if (!lovejazzie.convertToCoordinate.bitmapUtil.isStoped) {
                list.set(getInt(list, s), data);
                adapter.notifyDataSetChanged();
            }
        }

        private int getInt(List list, String s) {
            boolean equals = false;
            int loc = 0;
            for (int i = 0; i < list.size(); i++) {

                Object o = list.get(i);
                if (o instanceof Map) {
                    Object fileName = ((Map) o).get("fileName");
                    if (fileName instanceof String) {
                        equals = fileName.equals(s);
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

