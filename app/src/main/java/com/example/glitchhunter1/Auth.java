package com.example.glitchhunter1;

import static android.content.ContentValues.TAG;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

public class Auth extends AppCompatActivity {

    //XML Elements
    View auth_layout;
    Button singin_btn;
    Button login_btn;
    SignInButton google_btn;
    EditText user_name;
    EditText password;

    //Variables
    private String[] providers = {"BASIC","GOOGLE"};
    private String providerType = "";

    //Al crear el Activiy
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //XML
        singin_btn = (Button) findViewById(R.id.singin_btn);
        login_btn = (Button) findViewById(R.id.login_btn);
        google_btn = (SignInButton) findViewById(R.id.google_btn);
        google_btn.setSize(SignInButton.SIZE_STANDARD);
        user_name = findViewById(R.id.userName_text);
        password = findViewById(R.id.pass_text);
        auth_layout = findViewById(R.id.auth_layout);
        //Comprobar sesion
        comrobarSessionIniciada();
        //Autenticacion
        authentication();
    }
    //Al iniciar el Activity
    @Override
    protected void onStart(){
        super.onStart();
        //Hacer visible
        auth_layout.setVisibility(View.VISIBLE);
    }


    //Metodos
    public void authentication() {
        //Singin Event
        singin_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Provider type
                providerType = providers[0];
                //Texto
                String user_name_text = user_name.getText().toString();
                String pass_text = password.getText().toString();
                //-comprobar campos (not empty)
                if(!isEmpty(user_name_text) && !isEmpty(pass_text)) {
                    System.out.println("texto lleno");
                    //objeto firebaseAuth
                    FirebaseAuth authentication = FirebaseAuth.getInstance();
                    //crete user Task
                    Task<AuthResult> userTask = authentication.createUserWithEmailAndPassword(user_name_text, pass_text);
                    //Evento - "Registro satisfactorio"
                    userTask.addOnCompleteListener(Auth.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                //cambiar a Home pasando (email y provider)
                                go_Home(userTask.getResult().getUser().getEmail(), providerType, "Registro completo"); // SignIn -> Home | (email, provider)
                            } else {
                                showAlert("Email o contraseña invalidos");
                                //Info consola
                                Log.w(TAG, "signInWithCustomToken:failure", task.getException());
                            }
                        }
                    });
                } else {
                    System.out.println("Campos sin completar");
                }
            }
        });
        //Login Event
        login_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Provider type
                providerType = providers[0];
                //Texto
                String user_name_text = user_name.getText().toString();
                String pass_text = password.getText().toString();
                //-comprobar campos (not empty)
                if(!isEmpty(user_name_text) && !isEmpty(pass_text)) {
                    //objeto firebaseAuth
                    FirebaseAuth authentication = FirebaseAuth.getInstance();
                    //crete user Task
                    Task<AuthResult> userTask = authentication.signInWithEmailAndPassword(user_name_text, pass_text);
                    //Evento - "Registro satisfactorio"
                    userTask.addOnCompleteListener(Auth.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                //cambiar de Activity  (email y provider)
                                go_Home(userTask.getResult().getUser().getEmail(), providerType, "Login correcto"); // Login -> Home | (email, provider)
                            } else {
                                showAlert("Email o contraseña invalidos");
                                //Info consola
                                Log.w(TAG, "signInWithCustomToken:failure", task.getException());
                            }
                        }
                    });
                } else {
                    System.out.println("Campos sin completar");
                }
            }
        });
        //Google Event
        google_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //gso + (email, idToken) -> g_client
                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build();
                GoogleSignInClient g_client = GoogleSignIn.getClient(Auth.this, gso);
                //SingInIntent - Start Menu de Cuentas de Google
                Intent signInIntent = g_client.getSignInIntent();
                //inicia un activity para elegir email, result= email.
                startActivityForResult(signInIntent, 100); //En desuso pero funciona klk

            }
        });
    }
    //ActivityResult Event
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Provider type
        providerType = providers[1];
        //Result from Intent
        if (requestCode == 100) {
            //Task---> Google Account
            Task<GoogleSignInAccount> googleTask = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                //Account
                GoogleSignInAccount account= googleTask.getResult(ApiException.class);
                if(account!=null){
                    //Credentials (Para obtener el id token, primero hay que especificarlo en gsOptions)
                    AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                    //Guardar en FirebaseAuth
                    FirebaseAuth authentication = FirebaseAuth.getInstance();
                    Task<AuthResult> authTask = authentication.signInWithCredential(credential);
                    //Complete Event
                    authTask.addOnCompleteListener(Auth.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                go_Home(account.getEmail(), providerType, "");
                            } else {
                                showAlert("error authTask");
                            }
                        }
                    });
                } else {
                    showAlert("null googleAccount");
                }
            } catch (ApiException e) {
                showAlert("error googleTask");
                e.printStackTrace();
            }
        } else {
            showAlert("no funciona el requestCode 100");
        }
    }
    public void comrobarSessionIniciada(){
        SharedPreferences app_progress = this.getSharedPreferences(getString(R.string.app_progress), MODE_PRIVATE);
        //Comprobar si ya hay datos code usuario
        if(!app_progress.getString("email", "").isEmpty() && !app_progress.getString("providerType", "").isEmpty()) {
            //Hacer invisible
            auth_layout.setVisibility(View.INVISIBLE);
            //Pasa a home
            go_Home(app_progress.getString("email", ""), app_progress.getString("providerType", ""), "sesion iniciada");
        } else {
            showAlert("sin sesion iniciada "+" Email: "+app_progress.getString("email", "sin email")+" Prov: "+app_progress.getString("providerType", "sin prov"));
        }
    }
    public void go_Home(String email, String providerType, String message){
        //Al crear intent - (actualContext, next Activity)
        Intent intent = new Intent(this, Home.class);
        // y varios Datos
        intent.putExtra("email", email);
        intent.putExtra("providerType", providerType);
        intent.putExtra("message", ""+message);
        //Iniciar Home
        startActivity(intent);
    }
    public void go_Home_withGoogle(){
        //Al crear intent - (actualContext, next Activity)
        Intent intent = new Intent(this, Home.class);

        //Iniciar Home
        startActivity(intent);
    }
    public void showAlert(String message){
        //crear alerta
        AlertDialog.Builder builderAlert = new AlertDialog.Builder(this);
        builderAlert.setTitle("Nota");
        builderAlert.setMessage(message);
        builderAlert.setPositiveButton("Ok", null);
        AlertDialog alerta = builderAlert.create();
        //Mostrar alerta
        alerta.show();
    }
    public boolean isEmpty(String text) {
        boolean isEmpty = true;
        if(text.length() == 0){
            isEmpty = true;
        } else {
            isEmpty = false;
        }
        System.out.println(text+" vacio == "+isEmpty);
        return isEmpty;
    }
}