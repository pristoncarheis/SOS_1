package pcrc.sos.Login;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import pcrc.sos.Main;
import pcrc.sos.R;

public class Options extends AppCompatActivity {

    private TextInputLayout name, doc, phone, address;
    private Button saveData;
    private RadioGroup radioGroup;
    private RadioButton gender;
    private ProgressDialog dialog;
    private String fullname, fulldoc, fullphone, fulladdress, g, CurrentUserID;
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.options);

        //Inicializar servicios de Firebase
        auth = FirebaseAuth.getInstance();
        CurrentUserID = auth.getCurrentUser().getUid();
        firestore = FirebaseFirestore.getInstance();

        //Mostrar vista a pantalla completa
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        dialog = new ProgressDialog(this);

        name = findViewById(R.id.options_nombres);
        doc = findViewById(R.id.options_dni);
        phone = findViewById(R.id.options_celular);
        address = findViewById(R.id.options_direccion);
        saveData = findViewById(R.id.options_guardar);
        radioGroup = findViewById(R.id.options_group);
        saveData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendToData();
            }
        });
        
    }

    //Metodo de guardado de datos
    private void SendToData() {
        if(!validateFullName() | !validateDoc() | !validatePhone() | !validateAddress() | !validateRadioButton()){
            return;
        } else {

            dialog.setTitle(getString(R.string.save_your_data));
            dialog.setMessage(getString(R.string.wait_save_yout_data));
            dialog.show();
            dialog.setCanceledOnTouchOutside(true);

            Map<String, Object> data = new HashMap<>();

            data.put("Codigo", CurrentUserID);
            data.put("Nombres", fullname);
            data.put("DNI", fulldoc);
            data.put("Celular", fullphone);
            data.put("Direccion", fulladdress);
            data.put("Genero", g);
            data.put("ImgPerfil", "null");
            data.put("Lat_Usuario", 0);
            data.put("Long_Usuario", 0);
            data.put("Lat_Delito", 0);
            data.put("Long_Delito", 0);
            data.put("Lat_Incendio", 0);
            data.put("Long_Incendio", 0);
            data.put("Lat_Transito", 0);
            data.put("Long_Transito", 0);

            firestore.collection("Usuarios").document(CurrentUserID).set(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    SendUserToMain();
                    dialog.dismiss();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(Options.this, getString(R.string.therewaserror), Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            });



        }
    }

    //Enviar usuario a la actividad principal
    private void SendUserToMain() {
        Intent main = new Intent(Options.this, Main.class);
        main.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(main);
        finish();
    }

    //Validar Nombres
    private boolean validateFullName(){
        fullname = name.getEditText().getText().toString().trim();
        if (TextUtils.isEmpty(fullname)){
            name.setError(getString(R.string.fillthisfield));
            return false;
        } else {
            name.setError(null);
            return true;
        }
    }

    //Validad DNI
    private boolean validateDoc(){
        fulldoc = doc.getEditText().getText().toString().trim();
        if (TextUtils.isEmpty(fulldoc)){
            doc.setError(getString(R.string.fillthisfield));
            return false;
        } else if (fulldoc.length() < 8){
            doc.setError(getString(R.string.entervaliddoc));
            return false;
        } else {
            doc.setError(null);
            return true;
        }
    }

    //Validar celular
    private boolean validatePhone(){
        fullphone = phone.getEditText().getText().toString().trim();
        if (TextUtils.isEmpty(fullphone)){
            phone.setError(getString(R.string.fillthisfield));
            return false;
        } else if(fullphone.length() < 9) {
            phone.setError(getString(R.string.entervalidphone));
            return false;
        } else {
            phone.setError(null);
            return true;
        }
    }

    //Validar direccion
    private boolean validateAddress(){
        fulladdress = address.getEditText().getText().toString().trim();
        if (TextUtils.isEmpty(fulladdress)){
            address.setError(getString(R.string.fillthisfield));
            return false;
        } else {
            address.setError(null);
            return true;
        }
    }

    //Validar RadioGroup
    private boolean validateRadioButton(){
        if (radioGroup.getCheckedRadioButtonId() == -1){
            Toast.makeText(this, getString(R.string.gender_selection), Toast.LENGTH_SHORT).show();
            return false;
        } else {
            int radiobtn = radioGroup.getCheckedRadioButtonId();
            gender = (RadioButton) findViewById(radiobtn);
            g = gender.getText().toString();
            return true;
        }
    }
}
