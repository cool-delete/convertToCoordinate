package lovejazzie.convertToCoordinate;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.socks.library.KLog;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {
    EditText editText;
    public static final String TAG = "convertToCoordinate";
    private Button btnDo;
    private View view;
    private ListView listView;
    private Button button;
    private TextView tvDir;
    private String rootFile;
    private File[] nowFiles;
    private File nowFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
        for (File file : files) {
            if (file.isDirectory()) {
                count++;
                File[] files1 = file.listFiles();
                run(files1);
            }
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
                            getToast("天啊 这个路径有" + run + "个文件夹");
                            return;
                }
                        System.out.println("怎么了");
                        convert convert = new convert(root, MainActivity.this);
                        new Thread(convert).start();

                    }
                } else {
                    getToast("亲~..好像这个不是文件夹吧,我要文件夹哦");
                }
                break;
            case R.id.btn_img:
                getToast("....");
                GuiList();
                break;
            case R.id.button2:
                break;
        }
    }

    private void GuiList() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.list_file, null);
        setContentView(view);
        listView = (ListView) view.findViewById(R.id.listView);
        button = (Button) view.findViewById(R.id.button2);
        tvDir = (TextView) view.findViewById(R.id.tv_dir);
        tvDir.setText(rootFile);
        button.setOnClickListener(this);
        nowFiles = new File(rootFile).listFiles();
        listView.setOnItemClickListener(this);
        makFileList(new File(rootFile));
    }

    private void makFileList(File filePath) {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        File[] files = filePath.listFiles();
        Map<String, Object> item1 = new HashMap<String, Object>();
        item1.put("fileName", "上一层文件夹");
        list.add(item1);
        KLog.d(Arrays.toString(files));
        for (File file : files) {
            Map<String, Object> item = new HashMap<String, Object>();
            if (file.isDirectory()) {
                item.put("icon", R.drawable.folder);
            } else {
                item.put("icon", R.drawable.file);
            }
            item.put("fileName", file.getName());
            list.add(item);
        }
        SimpleAdapter adapter = new SimpleAdapter(this, list,
                R.layout.line,
                new String[]{"fileName", "icon"},
                new int[]{R.id.tv_fileName, R.id.imageView});
        listView.setAdapter(adapter);
        nowFiles = filePath.listFiles();
        nowFile = filePath;
        try {
            tvDir.setText(filePath.getCanonicalPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position == 0) {
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

    private void upFile() {
        String parent = nowFile.getParent();
        makFileList(new File(parent));
        KLog.d(parent);
    }

    private void getToast(String say) {
        Toast.makeText(this, say, Toast.LENGTH_SHORT).show();
    }

}
