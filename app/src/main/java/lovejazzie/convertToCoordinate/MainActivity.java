package lovejazzie.convertToCoordinate;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    EditText editText;
    ListView listView;
    public static final String TAG = "convertToCoordinate";
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent i = new Intent(this, convert.class);
        startService(i);
        listView = (ListView) findViewById(R.id.listView);
        editText = (EditText) findViewById(R.id.editText);
        Button button = (Button) findViewById(R.id.button);
        editText.setText(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString());
        button.setOnClickListener(this);
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
            String root = editText.getText().toString();
            File r = new File(root);
            if (r.isDirectory()) {
                File[] list = r.listFiles();

                if (list == null) {
                    Toast.makeText(MainActivity.this, "亲~..好像这个不是文件夹吧,我要文件夹哦",
                            Toast.LENGTH_LONG).show();

                } else if (0 == list.length) {
                    Toast.makeText(MainActivity.this, "诶多.里面找不到文件诶 要不确认一下路径?",
                            Toast.LENGTH_LONG).show();

                } else {
                    int run = run(list);
                    if (run != 0) {
                        count = 0;
                        Toast.makeText(MainActivity.this, "天啊 这个路径有" + run + "个文件夹", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    System.out.println("怎么了");
                    convert convert = new convert(root, MainActivity.this);
                    new Thread(convert).start();

                }
            } else {
                Toast.makeText(MainActivity.this, "亲~..好像这个不是文件夹吧,我要文件夹哦",
                        Toast.LENGTH_LONG).show();
            }

        }


    }
