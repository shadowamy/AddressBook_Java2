package com.example.addressbook_java2;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.addressbook_java2.Entity.User;
import com.example.addressbook_java2.Util.Http_messageRequest;

public class LoginActivity extends AppCompatActivity {

    public static Activity loginActivity = null;

    EditText username;
    EditText password;
    Button but_login;
    Button but_register;

    String LoginServlet = "http://192.168.1.100:8080/AddressBookWeb/LoginServlet";
    String sendMessage = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,  WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);

        loginActivity = this;

        username = (EditText) findViewById(R.id.editText_username);
        password = (EditText) findViewById(R.id.editText_password);
        but_login = (Button) findViewById(R.id.button_login);
        but_register = (Button)findViewById(R.id.button_register);

        final Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                Bundle data = msg.getData();
                String result = data.getString("Login_result");

                if(result.equals("Lack_error"))
                {
                    Toast.makeText(LoginActivity.this, "没有此账号信息，请先注册", Toast.LENGTH_LONG).show();
                }
                else if(result.equals("Pwd_error"))
                {
                    Toast.makeText(LoginActivity.this, "用户名或密码错误", Toast.LENGTH_LONG).show();
                }
                else if(result.equals("Login_Success"))
                {
                    User user = new User(username.getText().toString(), password.getText().toString());
                    saveUser(user);

                    Toast.makeText(LoginActivity.this, "登陆成功", Toast.LENGTH_LONG).show();

                    finish();
                    Intent intent = new Intent();
                    intent.setClass(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                }

                //Toast.makeText(LoginActivity.this, result, Toast.LENGTH_LONG).show();

            }
        };

        but_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Username = username.getText().toString();
                String Password = password.getText().toString();

                if(Username.equals("")||Username.equals(""))
                {
                    Toast.makeText(LoginActivity.this, "输入不能为空", Toast.LENGTH_LONG).show();
                }
                else {
                    sendMessage = "Username=" + Username + "&" + "Password=" + Password;

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String result = Http_messageRequest.sendPost(LoginServlet, sendMessage);

                            Message msg = new Message();
                            Bundle data = new Bundle();
                            data.putString("Login_result", result);
                            msg.setData(data);
                            handler.sendMessage(msg);
                        }
                    }).start();
                }
            }
        });

        but_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

    }

    public void saveUser(User user){
        SharedPreferences sp = this.getSharedPreferences("user_info", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("username",user.getUsername());
        editor.putString("password",user.getPassword());

        editor.commit();//必须提交，否则保存不成功
    }


}
