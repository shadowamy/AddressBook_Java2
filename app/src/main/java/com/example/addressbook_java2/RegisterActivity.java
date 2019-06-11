package com.example.addressbook_java2;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.addressbook_java2.Util.Http_messageRequest;
import com.example.addressbook_java2.Util.Register_Check;

public class RegisterActivity extends AppCompatActivity {

    EditText register_username;
    EditText register_password_first;
    EditText register_password_second;

    Button register_enter;
    Button register_back;

    String RegisterServlet = "http://192.168.1.100:8080/AddressBookWeb/RegisterServlet";
    String sendMessage = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,  WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_register);

        register_username = findViewById(R.id.register_username);
        register_password_first = findViewById(R.id.register_password_first);
        register_password_second = findViewById(R.id.register_password_second);

        register_enter = findViewById(R.id.register_enter);
        register_back = findViewById(R.id.register_back);

        final Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                Bundle data = msg.getData();
                String result = data.getString("Register_result");

                if(result.equals("repeat_error"))
                {
                    Toast.makeText(RegisterActivity.this, "用户名重复，请重新输入", Toast.LENGTH_LONG).show();
                }
                else if(result.equals("register_Success"))
                {
                    Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_LONG).show();

                    LoginActivity.loginActivity.finish();

                    finish();
                    Intent intent = new Intent();
                    intent.setClass(RegisterActivity.this, LoginActivity.class);
                    startActivity(intent);

                }

                //Toast.makeText(RegisterActivity.this, result, Toast.LENGTH_LONG).show();

            }
        };

        register_enter.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                String Username = register_username.getText().toString();
                String Password_first = register_password_first.getText().toString();
                String Password_second = register_password_second.getText().toString();

                if(Username.equals("")||Password_first.equals("")||Password_second.equals(""))
                {
                    Toast.makeText(RegisterActivity.this, "输入不能为空", Toast.LENGTH_LONG).show();
                }
                else {
                    if (!Register_Check.check_number(Username)) {
                        Toast.makeText(RegisterActivity.this, "请输入合法用户名", Toast.LENGTH_LONG).show();
                    }

                    if (!Register_Check.check_passward(Password_first, Password_second)) {
                        Toast.makeText(RegisterActivity.this, "请检查两次密码是否一致", Toast.LENGTH_LONG).show();
                    }
                    if(Register_Check.check_number(Username)&&Register_Check.check_passward(Password_first, Password_second))
                    {
                        sendMessage = "Username="+Username+"&Password="+Password_first;

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                String result = Http_messageRequest.sendPost(RegisterServlet,sendMessage);
                                Message msg = new Message();
                                Bundle data = new Bundle();
                                data.putString("Register_result", result);
                                msg.setData(data);
                                handler.sendMessage(msg);
                            }
                        }).start();
                    }
                }



            }
        });

        register_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
}
