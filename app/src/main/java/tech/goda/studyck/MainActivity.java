package tech.goda.studyck;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.provider.DocumentFile;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final int PICK_FILE_REQUEST = 0;
    private static final int LOGIN = 1;
    /*TextView messageText;
    Button uploadButton, choose;
    EditText editFileName;
    ImageView imageView;

    String uploadServerUri = null;
    InputStream in;
    Button button3;
    String fileName;*/
    String loginResponse;
    /**********  File Path *************/
    String uploadFilePath = Environment.getExternalStorageDirectory().getPath() + "/test.png";


    KeyStoreHelper keyStoreHelper;
    SharedPreferencesHelper preferencesHelper;
    private View header;
    private TextView navAccount;
    private TextView navName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.e("Directory", uploadFilePath);
        /*uploadButton = findViewById(R.id.uploadButton);
        choose = findViewById(R.id.choose);
        messageText = findViewById(R.id.messageText);
        editFileName = findViewById(R.id.fileName);
        button3 = findViewById(R.id.button3);
        imageView = findViewById(R.id.imageView);*/


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        header = navigationView.getHeaderView(0);

        CookieManager manager = new CookieManager();
        CookieHandler.setDefault(manager);

        preferencesHelper = new SharedPreferencesHelper(getApplicationContext());
        keyStoreHelper = new KeyStoreHelper(getApplicationContext(), preferencesHelper);

        /*messageText.setText("Uploading file path : " + uploadFilePath);


        //uploadServerUri = "http://study.ck.tp.edu.tw/login_chk.asp";
        uploadServerUri = "http://192.168.173.104/WebPageTest/upload-big5.php";

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                new Thread(new Runnable() {
                    public void run() {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                messageText.setText("uploading started.....");
                            }
                        });

                        Network.uploadFile(uploadServerUri, in, editFileName.getText().toString());

                    }
                }).start();
            }
        });

        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Map<String, String> params = new HashMap<>();
                
                params.put("f_mnuid", "");

                new Thread(new Runnable() {
                    public void run() {
                        Network.requestPost("http://study.ck.tp.edu.tw", params);
                    }
                }).start();
            }
        });

        choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileChooser();
            }
        });*/

        String encryptedText = preferencesHelper.getInput();
        final String mEmail = preferencesHelper.getString(SharedPreferencesHelper.PREF_AC);
        final String mPassword = keyStoreHelper.decrypt(encryptedText);
        if (mEmail.equals("") || mPassword.equals("")) {
            callLogin(mEmail, mPassword);
        } else {
            final Map<String, String> param = new HashMap<>();
            param.put("f_uid", mEmail);
            param.put("f_pwd", mPassword);
            Toast.makeText(getApplicationContext(), "自動登入中...", Toast.LENGTH_SHORT).show();

            View layout = findViewById(android.R.id.content);
            layout.setVisibility(View.GONE);

            new Thread(new Runnable() {
                public void run() {
                    loginResponse = Network.requestPost(Network.LOGIN_URI, param);
                    //Thread.sleep(2000);
                    Log.e("Login", loginResponse);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (!loginResponse.contains("錯誤")) {
                                LoginSuccess(mEmail, mPassword);
                            } else {
                                Toast.makeText(getApplicationContext(), "Login Failed", Toast.LENGTH_SHORT).show();
                                callLogin(mEmail, mPassword);
                            }
                        }
                    });
                }
            }).start();


            //return !response.contains("錯誤");
        }

    }

    private void callLogin(String account, String password) {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.putExtra("account", account);
        intent.putExtra("password", password);
        startActivityForResult(intent, LOGIN);
    }


    private void LoginSuccess(String account, String password) {
        View layout = findViewById(android.R.id.content);
        navName = header.findViewById(R.id.userName);
        navAccount = header.findViewById(R.id.userAccount);
        Toast.makeText(getApplicationContext(), "登入成功！！", Toast.LENGTH_SHORT).show();
        Document doc = Jsoup.parse(loginResponse);
        String name =  doc.select("form > font").first().text();
        //messageText.setText(name);
        name = name.substring(0, name.length()-2); // 扣除 "您好"
        navName.setText(name);
        navAccount.setText(account);

        // Save Login Information
        String encryptedPassword = keyStoreHelper.encrypt(password);
        preferencesHelper.setInput(encryptedPassword);
        preferencesHelper.putString(SharedPreferencesHelper.PREF_AC, account);

        layout.setVisibility(View.VISIBLE);

    }

    private void showFileChooser() {
        Intent intent = new Intent();
        //sets the select file to all types of files
        intent.setType("*/*");
        //allows to select data and return it
        intent.setAction(Intent.ACTION_GET_CONTENT);
        //starts new activity to select file and return data
        startActivityForResult(Intent.createChooser(intent, "Choose File to Upload.."), PICK_FILE_REQUEST);
    }

    private boolean isVirtualFile(Uri uri) {
        if (!DocumentsContract.isDocumentUri(this, uri)) {
            return false;
        }

        Cursor cursor = getContentResolver().query(
                uri,
                new String[]{DocumentsContract.Document.COLUMN_FLAGS},
                null, null, null);

        int flags = 0;
        if (cursor.moveToFirst()) {
            flags = cursor.getInt(0);
        }
        cursor.close();

        return (flags & DocumentsContract.Document.FLAG_VIRTUAL_DOCUMENT) != 0;
    }

    private InputStream getInputStreamForVirtualFile(Uri uri, String mimeTypeFilter)
            throws IOException {

        ContentResolver resolver = getContentResolver();

        String[] openableMimeTypes = resolver.getStreamTypes(uri, mimeTypeFilter);

        if (openableMimeTypes == null ||
                openableMimeTypes.length < 1) {
            throw new FileNotFoundException();
        }

        return resolver
                .openTypedAssetFileDescriptor(uri, openableMimeTypes[0], null)
                .createInputStream();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case PICK_FILE_REQUEST:
                /*if (resultCode == RESULT_OK) {
                    if (data == null) {
                        //no data present
                        return;
                    }


                    Uri selectedFileUri = data.getData();
                    fileName = DocumentFile.fromSingleUri(this, selectedFileUri).getName();
                    editFileName.setText(fileName);
                    messageText.setText("Upload File:" + fileName + "(Change file name below)");
                    Log.e("GetPathDocumentName", fileName);

                    try {
                        if (isVirtualFile(selectedFileUri)) {
                            Log.e("GetPath", "This is virtual file");*/
                            //in = getInputStreamForVirtualFile(selectedFileUri, "*/*");

                        /*} else {
                            in = getContentResolver().openInputStream(selectedFileUri);
                        }


                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.e("GetPathError", e.toString());
                    }
                }*/
            /*if(uploadFilePath != null && !uploadFilePath.equals("")){
                messageText.setText(uploadFilePath);
            }else{
                Toast.makeText(this,"Cannot upload file to server",Toast.LENGTH_SHORT).show();
            }*/
                break;
            case LOGIN:
                if (resultCode == RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    String account = bundle.getString("account");
                    String password = bundle.getString("password");
                    loginResponse = bundle.getString("response");
                    //messageText.setText(loginResponse);
                    LoginSuccess(account, password);
                }
                break;
        }


    }

    @Override
    protected void onDestroy() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Network.requestPost(Network.LOGOUT_URI, new HashMap<String, String>());
            }
        }).start();
        super.onDestroy();
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Fragment fragment = null;
        Bundle bundle = new Bundle();
        if (id == R.id.nav_file) {

        } else if (id == R.id.nav_site) {

        } else if (id == R.id.nav_homework) {

        } else if (id == R.id.nav_email) {

        } else if (id == R.id.nav_schedule) {

        } else if (id == R.id.nav_info) {

        } else if (id == R.id.nav_passwd) {
            fragment = new ChpassFragment();
            String account = navAccount.getText().toString();
            bundle.putString("account", account);
        }
        setTitle(item.getTitle());
        if (fragment != null) {
            fragment.setArguments(bundle);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, fragment);
            ft.commit();

        }
        Toast.makeText(getApplicationContext(), "你點選了！！", Toast.LENGTH_SHORT).show();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}