package com.example.addressbook_java2;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.addressbook_java2.Entity.Contact;
import com.example.addressbook_java2.Util.Check;
import com.example.addressbook_java2.Util.Http_messageRequest;
import com.example.addressbook_java2.Util.ImgBase64;

public class UpdateDetailActivity extends AppCompatActivity {

    Contact oldContact;

    EditText update_name;
    EditText update_phone;
    EditText update_address;

    ImageView update_enter;
    ImageView update_back;
    ImageView img_contact;

    String UpdateOneContactServlet = "http://192.168.1.100:8080/AddressBookWeb/UpdateOneContact";
    String AskContactImageContactServlet = "http://192.168.1.100:8080/AddressBookWeb/AskContactImage";
    String sendMessage = "";
    String sendMessage_img = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,  WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_update_detail);

        Intent intent = this.getIntent();
        oldContact =(Contact)intent.getSerializableExtra("OldDetail");

        update_name = findViewById(R.id.updata_name);
        update_phone = findViewById(R.id.updata_phone);
        update_address =  findViewById(R.id.updata_address);

        update_enter = findViewById(R.id.updata_enter);
        update_back = findViewById(R.id.updata_wrong);
        img_contact = findViewById(R.id.Img_contact);

        update_name.setText(oldContact.getName());
        update_phone.setText(oldContact.getPhonenumber());
        update_address.setText(oldContact.getAddress());

        LoadImg();

        update_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        final Handler handler = new Handler()
        {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                Bundle data = msg.getData();
                String result = data.getString("Update_result");

                if(result.equals("lack_error"))
                {
                    Toast.makeText(UpdateDetailActivity.this, "系统错误，请稍后再试", Toast.LENGTH_LONG).show();
                }
                else if(result.equals("repeat_error"))
                {
                    Toast.makeText(UpdateDetailActivity.this, "请勿修改为已存在的手机号", Toast.LENGTH_LONG).show();
                }
                else if(result.equals("update_success"))
                {
                    Toast.makeText(UpdateDetailActivity.this, "修改成功", Toast.LENGTH_LONG).show();

                    finish();
                    MainActivity.mainActivity.finish();
                    ContactDetailActivity.contactDetailActivity.finish();

                    Intent intent_main = new Intent();
                    intent_main.setClass(UpdateDetailActivity.this, MainActivity.class);
                    startActivity(intent_main);
                }

            }
        };

        update_enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(UpdateDetailActivity.this);
                builder.setMessage("是否修改？");
                builder.setTitle("温馨提示");

                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if(oldContact!=null)
                        {
                            Contact newContact = new Contact(update_name.getText().toString(), update_phone.getText().toString(), update_address.getText().toString(), oldContact.getBelongUser());
                            if(newContact.getName().equals("")||newContact.getPhonenumber().equals("")||newContact.getAddress().equals(""))
                            {
                                Toast.makeText(UpdateDetailActivity.this, "填写不能为空", Toast.LENGTH_LONG).show();
                            } else if(!Check.nameCheck(newContact.getName())){
                                Toast.makeText(UpdateDetailActivity.this,"姓名不能超过10个字符",Toast.LENGTH_LONG).show();
                            } else if(!Check.telteCheck(newContact.getPhonenumber())){
                                Toast.makeText(UpdateDetailActivity.this,"电话号码必须是纯数字",Toast.LENGTH_LONG).show();
                            }
                            else
                            {
                                sendMessage = "old_Name=" + oldContact.getName() + "&" + "old_Phonenumber=" + oldContact.getPhonenumber() + "&" + "old_BelongUser=" + oldContact.getBelongUser() + "&" + "old_Address=" + oldContact.getAddress() + "&"
                                        + "new_Name=" + newContact.getName() + "&" + "new_Phonenumber=" + newContact.getPhonenumber() + "&" + "new_BelongUser=" + newContact.getBelongUser() + "&" + "new_Address=" + newContact.getAddress();

                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        String result = Http_messageRequest.sendPost(UpdateOneContactServlet, sendMessage);

                                        Message msg = new Message();
                                        Bundle data = new Bundle();
                                        data.putString("Update_result", result);
                                        msg.setData(data);
                                        handler.sendMessage(msg);
                                    }
                                }).start();
                            }
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

    }

    Handler handler_img = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            Bundle data = msg.getData();
            String result_img = data.getString("Img_result");

            //Toast.makeText(ContactDetailActivity.this, result_img, Toast.LENGTH_LONG).show();
            if(!result_img.equals("lack_img"))
            {
                Bitmap bitmap = ImgBase64.stringtoBitmap(result_img);
                img_contact.setImageBitmap(bitmap);
            }

        }
    };

    private void LoadImg()
    {
        if(oldContact!=null) {
            sendMessage_img = "BelongUser=" + oldContact.getBelongUser() + "&" + "Phonenumber=" + oldContact.getPhonenumber();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String result_img = Http_messageRequest.sendPost(AskContactImageContactServlet, sendMessage_img);

                    Message msg = new Message();
                    Bundle data = new Bundle();
                    data.putString("Img_result", result_img);
                    msg.setData(data);
                    handler_img.sendMessage(msg);
                }
            }).start();
        }
    }

}
