package com.example.addressbook_java2;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;

import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.example.addressbook_java2.Adpter.AllContactAdapter;
import com.example.addressbook_java2.Entity.Contact;
import com.example.addressbook_java2.Util.Http_messageRequest;

import java.util.List;


public class MainActivity extends AppCompatActivity {

    public static MainActivity mainActivity = null;

    ListView contact_list;
    Spinner spinner_quit;
    ImageView imageView_add;
    EditText contact_search;

    String sendMessage;
    String RegisterServlet = "http://192.168.1.100:8080/AddressBookWeb/AllContactServlet";
    String FuzzyQueryServlet = "http://192.168.1.100:8080/AddressBookWeb/FuzzyQuery";

    List<Contact> Allcontact;
    //List<Contact> Searchcontact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,  WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        mainActivity = this;

        contact_search = findViewById(R.id.search_input);
        contact_list = findViewById(R.id.all_contact_listView);
        imageView_add = findViewById(R.id.imageview_add);
        isLogin();

        spinner_quit = findViewById(R.id.spinner_quit);
        spinner_quit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected_str = MainActivity.this.getResources().getStringArray(R.array.spinner_text)[position];
                if(selected_str.equals("退出登陆"))
                {
                    doLogout();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        contact_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Contact selected_contact = Allcontact.get(position);
                //Toast.makeText(MainActivity.this, selected_contact.getName(), Toast.LENGTH_LONG).show();
                Intent intent = new Intent(MainActivity.this, ContactDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("contactDetail", selected_contact);
                intent.putExtras(bundle);
                startActivity(intent);

            }
        });

        imageView_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,AddContactActivity.class);
                SharedPreferences sp = getSharedPreferences("user_info", Context.MODE_PRIVATE);
                String name = sp.getString("username","");
                intent.putExtra("username",name);
                startActivity(intent);
            }
        });

        contact_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String enterStr = contact_search.getText().toString();
                loadSearchContact(enterStr);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }
    private void isLogin()
    {
        //查看本地是否有用户的登录信息
        SharedPreferences sp = getSharedPreferences("user_info", Context.MODE_PRIVATE);
        String name = sp.getString("username", "");
        if (TextUtils.isEmpty(name)) {
            //本地没有保存过用户信息，给出提示：登录
            doLogin();
        } else {
            //已经登录过，直接加载用户的信息并显示
            loadAllcontact(name);
        }
    }
    //提示用户登录
    private void doLogin()
    {
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("提示")
                .setMessage("您还没有登录，点击确定进入登陆界面")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        finish();

                        Intent intent = new Intent();
                        intent.setClass(MainActivity.this, LoginActivity.class);
                        startActivity(intent);

                    }
                })
                .setCancelable(false)
                .show();
    }

    private void doLogout()
    {
        SharedPreferences sp = getSharedPreferences("user_info", Context.MODE_PRIVATE);
        sp.edit().clear().commit();//清除数据必须要提交:提交以后，文件仍存在，只是文件中的数据被清除了

        finish();

        Intent intent = new Intent();
        intent.setClass(MainActivity.this, LoginActivity.class);
        startActivity(intent);

    }

    Handler handler_all = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            Bundle data = msg.getData();
            String result = data.getString("Allcontact_result");
            Allcontact = JSON.parseArray(result, Contact.class);

            //Toast.makeText(MainActivity.this, result, Toast.LENGTH_LONG).show();
            //contact_list = findViewById(R.id.all_contact_listView);
            AllContactAdapter allContactAdapter = new AllContactAdapter(MainActivity.this, R.layout.list_item_layout, Allcontact);
            contact_list.setAdapter(allContactAdapter);

        }
    };

    private void loadAllcontact(String username)
    {
        if(username!=null&&!username.equals(""))
        {
            sendMessage = "BelongUser="+username;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String result = Http_messageRequest.sendPost(RegisterServlet,sendMessage);
                    Message msg = new Message();
                    Bundle data = new Bundle();
                    data.putString("Allcontact_result", result);
                    msg.setData(data);
                    handler_all.sendMessage(msg);
                }
            }).start();
        }
    }

    Handler handler_search = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            Bundle data = msg.getData();
            String result = data.getString("search_result");
            Allcontact = JSON.parseArray(result, Contact.class);

            AllContactAdapter searchContactAdapter = new AllContactAdapter(MainActivity.this, R.layout.list_item_layout, Allcontact);
            contact_list.setAdapter(searchContactAdapter);
        }
    };

    private void loadSearchContact(String enterStr)
    {
        SharedPreferences sp = getSharedPreferences("user_info", Context.MODE_PRIVATE);
        String username = sp.getString("username", "");
        if(username!=null&&!username.equals(""))
        {
            sendMessage = "BelongUser="+username+"&"+"EnterStr="+enterStr;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String result = Http_messageRequest.sendPost(FuzzyQueryServlet,sendMessage);
                    Message msg = new Message();
                    Bundle data = new Bundle();
                    data.putString("search_result", result);
                    msg.setData(data);
                    handler_search.sendMessage(msg);
                }
            }).start();
        }
    }

}
