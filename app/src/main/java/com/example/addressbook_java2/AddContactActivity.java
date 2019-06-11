package com.example.addressbook_java2;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.addressbook_java2.Entity.Contact;
import com.example.addressbook_java2.Util.Check;
import com.example.addressbook_java2.Util.Http_messageRequest;
import com.example.addressbook_java2.Util.ImgBase64;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URLEncoder;

public class AddContactActivity extends AppCompatActivity {

    Contact newContact;

    private ImageView add_enter;
    private ImageView add_back;
    private ImageView add_image;

    private EditText add_name;
    private EditText add_tele;
    private EditText add_adress;

    String username;
    private static int RESULT_LOAD_IMAGE = 10;

    String AddNewContactServlet = "http://192.168.1.100:8080/AddressBookWeb/AddNewContact";
    String AddContactImageServlet = "http://192.168.1.100:8080/AddressBookWeb/AddContactImage";
    String sendMessage = "";
    String sendMessage_img = "";

    String Img_path = "";
    long s = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,  WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_add_contact);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        }

        Intent intent = this.getIntent();
        username = intent.getStringExtra("username");

        add_name = findViewById(R.id.add_name);
        add_tele = findViewById(R.id.add_tele);
        add_adress = findViewById(R.id.add_adress);

        add_back = findViewById(R.id.add_back);
        add_enter = findViewById(R.id.add_enter);
        add_image = findViewById(R.id.add_imge);

        add_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        final Handler handler = new Handler()
        {
            @Override
            public void handleMessage(Message msg){
                super.handleMessage(msg);
                Bundle data = msg.getData();
                String result = data.getString("Add_result");

                if(result.equals("repeat_error"))
                {
                    Toast.makeText(AddContactActivity.this,"请勿重复添加同一手机号",Toast.LENGTH_LONG).show();
                }
                else if(result.equals("add_one_success"))
                {
                    Toast.makeText(AddContactActivity.this, "添加成功", Toast.LENGTH_LONG).show();

                    finish();
                    MainActivity.mainActivity.finish();

                    Intent intent_main = new Intent();
                    intent_main.setClass(AddContactActivity.this, MainActivity.class);
                    startActivity(intent_main);
                }
            }
        };

        add_enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                AlertDialog.Builder builder = new AlertDialog.Builder(AddContactActivity.this);
                builder.setMessage("是否添加？");
                builder.setTitle("温馨提示");

                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        newContact = new Contact(add_name.getText().toString(), add_tele.getText().toString(), add_adress.getText().toString(), username);
                        if(newContact.getAddress().equals("")||newContact.getPhonenumber().equals("")||newContact.getName().equals(""))
                        {
                            Toast.makeText(AddContactActivity.this,"填写不能为空",Toast.LENGTH_LONG).show();
                        }
                        else if(!Check.nameCheck(newContact.getName())){
                            Toast.makeText(AddContactActivity.this,"姓名不能超过10个字符",Toast.LENGTH_LONG).show();
                        } else if(!Check.telteCheck(newContact.getPhonenumber())){
                            Toast.makeText(AddContactActivity.this,"电话号码必须是纯数字",Toast.LENGTH_LONG).show();
                        }
                        else
                        {
                            sendMessage = "Name=" + newContact.getName() + "&" + "Phonenumber=" + newContact.getPhonenumber()
                                    + "&" +"BelongUser=" + newContact.getBelongUser()+ "&" +"Address=" + newContact.getAddress();

                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    String result = Http_messageRequest.sendPost(AddNewContactServlet, sendMessage);

                                    String result_img = null;
                                    if(Img_path!=null&&!Img_path.equals(""))
                                    {
                                        String imgStr = ImgBase64.getImgStr(Img_path);
                                        Log.i("img", imgStr);
                                        sendMessage_img = "Phonenumber=" + newContact.getPhonenumber() + "&" +"BelongUser=" + newContact.getBelongUser()
                                                            + "&" + "Imgstr=" + URLEncoder.encode(imgStr);
                                        result_img = Http_messageRequest.sendPost(AddContactImageServlet, sendMessage_img);
                                    }

                                    Message msg = new Message();
                                    Bundle data = new Bundle();
                                    data.putString("Add_result", result);
                                    data.putString("Img_result", result_img);
                                    msg.setData(data);
                                    handler.sendMessage(msg);
                                }
                            }).start();
                        }

                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.create().show();

            }
        });

        add_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        add_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent1,RESULT_LOAD_IMAGE);
            }
        });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode,resultCode,data);
        if(requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data!=null)
        {
            Uri selectImage = data.getData();
            String [] filePath = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectImage,filePath,null,null,null);
            cursor.moveToFirst();
            int columIndex = cursor.getColumnIndex(filePath[0]);
            Img_path = cursor.getString(columIndex);
            cursor.close();
            FileInputStream fileInputStream = null;
            try {
                fileInputStream = new FileInputStream(Img_path);
                s = fileInputStream.available();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if(s > 1048576)
            {
                Img_path = "";
                Toast.makeText(AddContactActivity.this,"选择的图像不能大于1MB",Toast.LENGTH_LONG).show();
            }
            else
            {
                Bitmap bm = BitmapFactory.decodeFile(Img_path);
                DisplayMetrics dm = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(dm);
                int screenwidth = dm.widthPixels;

                if(bm.getWidth() <= screenwidth)
                    add_image.setImageBitmap(bm);
                else {
                    Bitmap bmp=Bitmap.createScaledBitmap(bm, screenwidth, bm.getHeight()*screenwidth/bm.getWidth(), true);
                    add_image.setImageBitmap(bmp);
                }
            }
        }
    }
}
