package com.example.addressbook_java2;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.addressbook_java2.Entity.Contact;
import com.example.addressbook_java2.Util.Http_messageRequest;
import com.example.addressbook_java2.Util.ImgBase64;

public class ContactDetailActivity extends AppCompatActivity {

    ImageView img_contact;
    ImageView img_back;
    ImageView img_delete;
    FloatingActionButton changeDeta_butt;
    public static ContactDetailActivity contactDetailActivity  = null;

    TextView detailTitle;
    TextView detailName;
    TextView detailPhone;
    TextView detailAddress;

    Contact contactDetail;
    String DeleteOneContactServlet = "http://192.168.1.100:8080/AddressBookWeb/DeleteOneContact";
    String AskContactImageContactServlet = "http://192.168.1.100:8080/AddressBookWeb/AskContactImage";
    String sendMessage_detail = "";
    String sendMessage_img = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,  WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_contact_detail);

        contactDetailActivity = this;

        img_contact = findViewById(R.id.PV_avatar);
        img_back = findViewById(R.id.VP_back);
        img_delete = findViewById(R.id.VP_delete);
        changeDeta_butt = findViewById(R.id.change_details);

        detailTitle = findViewById(R.id.detail_title);
        detailName = findViewById(R.id.detail_name);
        detailPhone = findViewById(R.id.detail_phone);
        detailAddress = findViewById(R.id.detail_address);

        Intent intent = this.getIntent();
        contactDetail =(Contact)intent.getSerializableExtra("contactDetail");

        detailTitle.setText(contactDetail.getName());
        detailName.setText(contactDetail.getName());
        detailPhone.setText(contactDetail.getPhonenumber());
        detailAddress.setText(contactDetail.getAddress());

        LoadImg();

        img_back.setOnClickListener(new View.OnClickListener() {
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
                String result_detail = data.getString("Delete_result");
                //String result_img = data.getString("Img_result");

                if(result_detail.equals("lack_error"))
                {
                    Toast.makeText(ContactDetailActivity.this, "系统错误，请稍后再试", Toast.LENGTH_LONG).show();
                }
                else if(result_detail.equals("delete_one_success"))
                {
                    Toast.makeText(ContactDetailActivity.this, "删除成功", Toast.LENGTH_LONG).show();

                    finish();
                    MainActivity.mainActivity.finish();

                    Intent intent_main = new Intent();
                    intent_main.setClass(ContactDetailActivity.this, MainActivity.class);
                    startActivity(intent_main);
                }

            }
        };

        img_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(ContactDetailActivity.this);
                builder.setMessage("是否删除？");
                builder.setTitle("温馨提示");

                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if(contactDetail!=null)
                        {
                            sendMessage_detail = "Name=" + contactDetail.getName() + "&" + "Phonenumber=" + contactDetail.getPhonenumber()
                                            + "&" +"BelongUser=" + contactDetail.getBelongUser()+ "&" +"Address=" + contactDetail.getAddress();

                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    String result_detail = Http_messageRequest.sendPost(DeleteOneContactServlet, sendMessage_detail);
                                    //String result_img = Http_messageRequest.sendPost(AskContactImageContactServlet, sendMessage_img);

                                    Message msg = new Message();
                                    Bundle data = new Bundle();
                                    data.putString("Delete_result", result_detail);
                                    //data.putString("Img_result", result_img);
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

        changeDeta_butt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent_update = new Intent(ContactDetailActivity.this, UpdateDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("OldDetail", contactDetail);
                intent_update.putExtras(bundle);
                startActivity(intent_update);

            }
        });

        //Toast.makeText(ContactDetailActivity.this, contactDetail.getName(), Toast.LENGTH_LONG).show();

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
        if(contactDetail!=null) {
            sendMessage_img = "BelongUser=" + contactDetail.getBelongUser() + "&" + "Phonenumber=" + contactDetail.getPhonenumber();
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
