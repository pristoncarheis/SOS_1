package pcrc.sos.Login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import pcrc.sos.Main;
import pcrc.sos.R;

public class Login extends AppCompatActivity {

    private TextInputLayout email, password, recoveryEmail;
    private Button register, enter, btn;
    private TextView recovery_pass;
    private FirebaseAuth auth;
    private ProgressDialog dialog;
    private String mail, pass, rEmail;
    private AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        //Inicializar Firebase Auth
        auth = FirebaseAuth.getInstance();

        //Inicializar ProgressDialog
        dialog = new ProgressDialog(this);

        //Mostrar vista a pantalla completa
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //Definir variables
        email = findViewById(R.id.login_email);
        password = findViewById(R.id.login_pass);
        register = findViewById(R.id.login_register);
        enter = findViewById(R.id.login_enter);
        recovery_pass = findViewById(R.id.login_revocerypass);
        
        //Evento onClick
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendToRegister();
            }
        });
        enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AllowUserToLogin();
            }
        });

        RecoveryPassToUser();

    }

    //Si el usuario ya inicio sesión anteriormente, este sera enviado directamente a la interfaz principal
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser firebaseUser = auth.getCurrentUser();
        if (firebaseUser != null){
            SendToMain();
        }
    }

    //Método de Inicio de Sesión
    private void AllowUserToLogin() {
        if (!validateEmail() | !validatePassword()){
            return;
        } else {
            dialog.setTitle(getString(R.string.logginin));
            dialog.setMessage(getString(R.string.pleasewaitlogginin));
            dialog.show();
            dialog.setCanceledOnTouchOutside(true);

            auth.signInWithEmailAndPassword(mail, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        SendToMain();
                        dialog.dismiss();
                    } else {
                        Toast.makeText(Login.this, getString(R.string.therewaserror), Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                }
            });
        }
    }

    //Validar ingreso de correo
    private boolean validateEmail (){

        mail = email.getEditText().getText().toString().trim();

        if (TextUtils.isEmpty(mail)){
            email.setError(getString(R.string.fillthisfield));
            return false;
        } else {
            email.setError(null);
            return true;
        }
    }

    //Validar ingreso de contraseña
    private boolean validatePassword (){
         pass = password.getEditText().getText().toString().trim();
         if (TextUtils.isEmpty(pass)){
             password.setError(getString(R.string.fillthisfield));
             return false;
         } else {
             password.setError(null);
             return true;
         }
    }

    //Enviar al usuario a la actividad principal
    private void SendToMain() {
        Intent main = new Intent(this, Main.class);
        main.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(main);
        finish();
    }

    //Recuperar la contraseña del usuario
    private void RecoveryPassToUser() {
        String rp = getString(R.string.recovery_pass);
        SpannableString text = new SpannableString(rp);
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                RecoveryPass();
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(Color.WHITE);
            }
        };
        text.setSpan(clickableSpan, 0, 25, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        recovery_pass.setText(text);
        recovery_pass.setMovementMethod(LinkMovementMethod.getInstance());
    }

    //Dialogo para recuperacion de contraseña
    private void RecoveryPass() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.recovery_pass, null);
        builder.setView(view);
        alertDialog = builder.create();
        alertDialog.show();
        recoveryEmail = view.findViewById(R.id.recovery_email);
        btn = view.findViewById(R.id.recovery_btn);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendToEmail();
            }
        });
    }

    //Metodo de recuperacion de contraseña
    private void SendToEmail() {
        if (!SendMsjRecovery()){
            return;
        } else {

            dialog.setTitle(getString(R.string.sendmsjconf));
            dialog.setMessage(getString(R.string.sendmsjpw));
            dialog.show();
            dialog.setCanceledOnTouchOutside(true);

            auth.sendPasswordResetEmail(rEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(Login.this, getString(R.string.msjsend), Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        alertDialog.dismiss();
                    } else {
                        Toast.makeText(Login.this, getString(R.string.emailinvalid), Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        alertDialog.dismiss();
                    }
                }
            });
        }

    }

    //Validar recuperar contraseña
    private boolean SendMsjRecovery(){
        rEmail = recoveryEmail.getEditText().getText().toString().trim();

        if (TextUtils.isEmpty(rEmail)){
            recoveryEmail.setError(getString(R.string.fillthisfield));
            return false;
        } else {
            recoveryEmail.setError(null);
            return true;
        }
    }

    //Enviar al usuario a registro
    private void SendToRegister() {
        startActivity(new Intent(this, Register.class));
    }
}
