package pcrc.sos.Login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import pcrc.sos.Main;
import pcrc.sos.R;

public class Register extends AppCompatActivity {

    private TextInputLayout email, new_password, confirm_password;
    private Button register;
    private FirebaseAuth auth;
    private ProgressDialog dialog;
    private CheckBox checkBox;
    private TextView termsandconditions;
    private String mail, pass, cpass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        //Mostrar vista a pantalla completa
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //Inicializar FirebaseAuth
        auth = FirebaseAuth.getInstance();

        //Definir variables
        email = findViewById(R.id.register_email);
        new_password = findViewById(R.id.register_pass);
        confirm_password = findViewById(R.id.register_confirm);
        register = findViewById(R.id.register_createaccount);
        checkBox = findViewById(R.id.register_check);
        termsandconditions = findViewById(R.id.register_termsandcondit);

        //Inicializar dialog
        dialog = new ProgressDialog(this);

        //Eventos OnClick
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateAccount();
            }
        });
        ViewTermsAndConditions();
    }

    ////Validar términos y condiciones
    private void ViewTermsAndConditions() {
        String terms = getString(R.string.termsandcondit);
        SpannableString text = new SpannableString(terms);
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                startActivity(new Intent(Register.this, Terms.class));
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(Color.GREEN);
            }
        };
        text.setSpan(clickableSpan, 27, 49, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        termsandconditions.setText(text);
        termsandconditions.setMovementMethod(LinkMovementMethod.getInstance());
    }

    //Si el usuario ya esta registrado con todos sus datos, se envía a la actividad principal
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser firebaseUser = auth.getCurrentUser();
        if (firebaseUser != null ) {
            SendToMain();
        }
    }

    //Enviar a la actividad de inicio
    private void SendToMain() {
        Intent main = new Intent(this, Main.class);
        main.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(main);
        finish();
    }

    //Validar check
    private boolean validateCheckBox(){

        if (checkBox.isChecked()){
            return true;
        } else {
            Toast.makeText(this, getString(R.string.accepttermsandcondi), Toast.LENGTH_SHORT).show();
            return false;
        }
    }
    //Validar correo
    private boolean validateEmail(){
        mail = email.getEditText().getText().toString().trim();
        if (TextUtils.isEmpty(mail)){
            email.setError(getString(R.string.fillthisfield));
            return false;
        } else {
            email.setError(null);
            return true;
        }
    }

    //Validar contraseña
    private boolean validatePassword(){
        pass = new_password.getEditText().getText().toString().trim();
        if (TextUtils.isEmpty(pass)){
            new_password.setError(getString(R.string.fillthisfield));
            return false;
        } else if (pass.length() < 6){
            new_password.setError(getString(R.string.yourpasscontent6));
            return false;
        } else {
            new_password.setError(null);
            return true;
        }
    }

    //Validar confirmar contraseña
    private boolean validateConfirmPassword(){

        cpass = confirm_password.getEditText().getText().toString().trim();
        pass = new_password.getEditText().getText().toString().trim();

        if (TextUtils.isEmpty(cpass)){
            confirm_password.setError(getString(R.string.fillthisfield));
            return false;
        } else if (cpass.length() < 6){
            confirm_password.setError(getString(R.string.yourpasscontent6));
            return false;
        } else if (!pass.equals(cpass)){
            confirm_password.setError(getString(R.string.passdonotmatch));
            return false;
        } else {
            confirm_password.setError(null);
            return true;
        }
    }

    //Método para registrar al usuario
    private void CreateAccount() {
        if (!validateEmail() | !validatePassword() | !validateConfirmPassword() | !validateCheckBox()) {
            return;
        } else {
            dialog.setTitle(getString(R.string.creatingnewaccount));
            dialog.setMessage(getString(R.string.pleasewait));
            dialog.show();
            dialog.setCancelable(true);

            auth.createUserWithEmailAndPassword(mail, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        SendUserToOptions();
                        dialog.dismiss();
                    } else {
                        Toast.makeText(Register.this, getString(R.string.therewaserror), Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                }
            });
        }
    }

    //Enviar a la actividad de opciones una vez registrado
    private void SendUserToOptions() {
        Intent options = new Intent(this, Options.class);
        options.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(options);
        finish();
    }
}
