package com.example.projectcallsms;

import android.Manifest;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class MainActivity extends AppCompatActivity {

    private String telefono; //Variable donde se guardara el numero de telefono
    private String nombre;
    private String mensaje;//Variable donde se guardara el nombre de contacto
    private String direccion,mensajeMail, textAsunto;


    private final int codigoRequestContactsTel = 1; //Codigo de respuesta para el requestPermision de Llamada y  Contactos


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        brSMS brSMS = new brSMS();

        final Button mCallButton = (Button) findViewById(R.id.btn_call); //Obtenemos los botones del view
        final Button btnSMS = (Button) findViewById(R.id.btn_sms);
        final Button btnMail = (Button) findViewById(R.id.btn_email);

        checkPhonePermission(); //se checan los permisos otorgados a la app

        mCallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkPhonePermission()==false){
                    Intent pickContact = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                    pickContact.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE); //SOLO MOSTRAR VENTANA CON NOMBRE Y NUMERO
                    startActivityForResult(pickContact, 1);
                }
                else{
                    try {
                        //Pedir que activen el permiso de leer contactos y llamadas
                        if(checkPhonePermission()){
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                                    Manifest.permission.READ_CONTACTS,
                                    Manifest.permission.CALL_PHONE,
                                    Manifest.permission.SEND_SMS}, codigoRequestContactsTel);
                            ActivityCompat.requestPermissions(MainActivity.this,new String[]{
                                    Manifest.permission.RECEIVE_SMS}, 222);
                        }
                    }catch (Exception e){
                        Toast.makeText(MainActivity.this, "Falló la aplicación. Intente de nuevo", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        btnSMS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkPhonePermission()==false){//comprobar permisos otorgados
                    setContentView(R.layout.sms_activity);
                    Button btnVolverS = (Button) findViewById(R.id.btn_VolverSMS);

                    btnVolverS.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(v.getContext(),MainActivity.class);
                            startActivityForResult(intent,0);
                        }
                    });
                    final Button sendMessageBtn = (Button) findViewById(R.id.btn_EnviarSMS);
                    final EditText messagetEt = (EditText) findViewById(R.id.edit_SMS);
                    sendMessageBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent pickContact = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                            pickContact.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE); //SOLO MOSTRAR VENTANA CON NOMBRE Y NUMERO
                            mensaje = messagetEt.getText().toString();
                            startActivityForResult(pickContact, 2);


                        }
                    });

                }
                else{
                    try {
                        //Pedir que activen el permiso de leer contactos y llamadas
                        if(checkPhonePermission()){
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                                    Manifest.permission.READ_CONTACTS,
                                    Manifest.permission.CALL_PHONE,
                                    Manifest.permission.SEND_SMS}, codigoRequestContactsTel);
                            ActivityCompat.requestPermissions(MainActivity.this,new String[]{
                                    Manifest.permission.RECEIVE_SMS}, 222);

                        }
                    }catch (Exception e){
                        Toast.makeText(MainActivity.this, "Falló la aplicación. Intente de nuevo", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });






        //Asignamos un evento al boton btn_call
        btnMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(checkPhonePermission()==false){//comprobar permisos otorgados
                    setContentView(R.layout.mail_activity);
                    Button btnVolverM = (Button) findViewById(R.id.btn_VolverMail);

                    btnVolverM.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(v.getContext(),MainActivity.class);
                            startActivityForResult(intent,0);
                        }
                    });
                    final Button btnEnviarMail = (Button) findViewById(R.id.btn_EnviarMail);
                    final EditText etAsunto = (EditText) findViewById(R.id.edit_Asunto);
                    final EditText etMail = (EditText) findViewById(R.id.edit_Mail);
                    btnEnviarMail.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent pickContact = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                            pickContact.setType(ContactsContract.CommonDataKinds.Email.CONTENT_TYPE); //SOLO MOSTRAR VENTANA CON NOMBRE Y NUMERO
                            textAsunto = etAsunto.getText().toString();
                            mensajeMail = etMail.getText().toString();
                            startActivityForResult(pickContact, 3);
                            etAsunto=null;
                            etMail=null;
                        }
                    });
                }
                else{
                    try {
                        //Pedir que activen el permiso de leer contactos y llamadas
                        if(checkPhonePermission()){
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                                    Manifest.permission.READ_CONTACTS,
                                    Manifest.permission.CALL_PHONE,
                                    Manifest.permission.SEND_SMS}, codigoRequestContactsTel);
                            ActivityCompat.requestPermissions(MainActivity.this,new String[]{
                                    Manifest.permission.RECEIVE_SMS}, 222);
                        }
                    }catch (Exception e){
                        Toast.makeText(MainActivity.this, "Falló la aplicación. Intente de nuevo", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }

    //Booleano para saber si los permisos han sido concedidos
    public boolean checkPhonePermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if ((ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_CONTACTS) == PERMISSION_GRANTED) |
                    (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CALL_PHONE) == PERMISSION_GRANTED) |
                    (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.SEND_SMS) == PERMISSION_GRANTED)|
                    (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.SEND_SMS) == PERMISSION_GRANTED)) {
                return false;
            }
        }
        return true;
    }

    //Revisar las peticiones de activacion para los permisos de Llamadas y Contactos
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case codigoRequestContactsTel: {
                if (grantResults.length > 0
                        && grantResults[0] == PERMISSION_GRANTED) {
                    Toast.makeText(MainActivity.this, "Permiso concedido", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(MainActivity.this, "Permiso denegado", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    //OnActivityResult del boton btn_call
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case 1:
                //LLAMADA
                Uri uri = data.getData();
                Cursor cursor = getContentResolver().query(uri, null, null, null, null);
                cursor.moveToFirst(); //Nos movemos al primer dato
                telefono = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)); //Guardamos el numero encontrado
                cursor.moveToFirst(); //Nos movemos al primer dato
                nombre = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)); //Guardamos el nombre encontrado
                String marcar = "tel:" + telefono; //Concatenamos para completar la URI
                startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(marcar)));
                Toast.makeText(MainActivity.this, "Lamando a "+nombre+"\n"+telefono, Toast.LENGTH_SHORT).show();
                break;
            case 2:
                //SMS
                Uri uriSMS =data.getData();
                Cursor cursorSMS = getContentResolver().query(uriSMS, null, null, null, null);
                cursorSMS.moveToFirst();
                telefono = cursorSMS.getString(cursorSMS.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                if(!TextUtils.isEmpty(mensaje) && !TextUtils.isEmpty(telefono)) {
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(telefono, null, mensaje, null, null);
                    if (resultCode==RESULT_OK){
                        Toast.makeText(this, "Mensaje Enviado con exito", Toast.LENGTH_SHORT).show();
                    }
                    mensaje=null;
                    telefono=null;
                }else{
                    Toast.makeText(MainActivity.this, "Campos vacios", Toast.LENGTH_SHORT).show();
                }
                break;
            case 3:
                //EMAIL
                Uri uriMail =data.getData();
                Cursor cursorMail = getContentResolver().query(uriMail, null, null, null, null);
                cursorMail.moveToFirst();
                direccion = cursorMail.getString(cursorMail.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS));
                Toast.makeText(MainActivity.this,"Selecciona la app para enviarlo",Toast.LENGTH_SHORT).show();

                /*Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.setData(Uri.parse("mailto:"));
                emailIntent.setType("text/plain");
                emailIntent.putExtra(Intent.EXTRA_EMAIL, direccion);

                emailIntent.putExtra(Intent.EXTRA_SUBJECT, textAsunto);
                emailIntent.putExtra(Intent.EXTRA_TEXT, mensajeMail);*/
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO,
                        Uri.fromParts("mailto",direccion, null));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, textAsunto);
                emailIntent.putExtra(Intent.EXTRA_TEXT, mensajeMail);
                startActivity(Intent.createChooser(emailIntent, "Enviar email..."));

                direccion=null;
                textAsunto=null;
                mensajeMail=null;

                /*try {
                    startActivity(Intent.createChooser(emailIntent, "Enviar email..."));
                    finish();

                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(MainActivity.this,
                            "No tienes clientes de email instalados.", Toast.LENGTH_SHORT).show();
                }*/


                break;



        }

    }


    private class brSMS extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if(bundle != null) {
                Object[] pdus = (Object[]) bundle.get("pdus");
                String format = bundle.getString("format");

                final SmsMessage[] messages = new SmsMessage[pdus.length];
                for(int i = 0; i < pdus.length; i++) {
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i], format);
                    }else {
                        messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                    }
                    String senderPhoneNo = messages[i].getDisplayOriginatingAddress();
                    Toast.makeText(context, "Mensaje " + messages[0].getMessageBody() + ", de " + senderPhoneNo, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
